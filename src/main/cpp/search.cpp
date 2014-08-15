/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "search.h"

#include <iostream>
#include <string>

namespace pulse {

Search::Timer::Timer(bool& timerStopped, bool& doTimeManagement, int& currentDepth, int& initialDepth, bool& abort)
    : timerStopped(timerStopped), doTimeManagement(doTimeManagement),
    currentDepth(currentDepth), initialDepth(initialDepth), abort(abort) {
}

void Search::Timer::run(uint64_t searchTime) {
  std::unique_lock<std::mutex> lock(mutex);
  if (condition.wait_for(lock, std::chrono::milliseconds(searchTime)) == std::cv_status::timeout) {
    timerStopped = true;

    // If we finished the first iteration, we should have a result.
    // In this case abort the search.
    if (!doTimeManagement || currentDepth > initialDepth) {
      abort = true;
    }
  }
}

void Search::Timer::start(uint64_t searchTime) {
  thread = std::thread(&Search::Timer::run, this, searchTime);
}

void Search::Timer::stop() {
  condition.notify_all();
  thread.join();
}

Search::Semaphore::Semaphore(int permits)
    : permits(permits) {
}

void Search::Semaphore::acquire() {
  std::unique_lock<std::mutex> lock(mutex);
  while (permits == 0) {
    condition.wait(lock);
  }
  --permits;
}

void Search::Semaphore::release() {
  std::unique_lock<std::mutex> lock(mutex);
  ++permits;
  condition.notify_one();
}

std::unique_ptr<Search> Search::newDepthSearch(Board& board, int searchDepth) {
  if (searchDepth < 1 || searchDepth > Depth::MAX_DEPTH) throw std::exception();

  std::unique_ptr<Search> search(new Search(board));

  search->searchDepth = searchDepth;

  return search;
}

std::unique_ptr<Search> Search::newNodesSearch(Board& board, uint64_t searchNodes) {
  if (searchNodes < 1) throw std::exception();

  std::unique_ptr<Search> search(new Search(board));

  search->searchNodes = searchNodes;

  return search;
}

std::unique_ptr<Search> Search::newTimeSearch(Board& board, uint64_t searchTime) {
  if (searchTime < 1) throw std::exception();

  std::unique_ptr<Search> search(new Search(board));

  search->searchTime = searchTime;
  search->runTimer = true;

  return search;
}

std::unique_ptr<Search> Search::newInfiniteSearch(Board& board) {
  std::unique_ptr<Search> search(new Search(board));

  return search;
}

std::unique_ptr<Search> Search::newClockSearch(
    Board& board,
    uint64_t whiteTimeLeft, uint64_t whiteTimeIncrement, uint64_t blackTimeLeft, uint64_t blackTimeIncrement, int movesToGo) {
  std::unique_ptr<Search> search = newPonderSearch(
      board,
      whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, movesToGo
  );

  search->runTimer = true;

  return search;
}

std::unique_ptr<Search> Search::newPonderSearch(
    Board& board,
    uint64_t whiteTimeLeft, uint64_t whiteTimeIncrement, uint64_t blackTimeLeft, uint64_t blackTimeIncrement, int movesToGo) {
  if (whiteTimeLeft < 1) throw std::exception();
  if (whiteTimeIncrement < 0) throw std::exception();
  if (blackTimeLeft < 1) throw std::exception();
  if (blackTimeIncrement < 0) throw std::exception();
  if (movesToGo < 0) throw std::exception();

  std::unique_ptr<Search> search(new Search(board));

  uint64_t timeLeft;
  uint64_t timeIncrement;
  if (board.activeColor == Color::WHITE) {
    timeLeft = whiteTimeLeft;
    timeIncrement = whiteTimeIncrement;
  } else {
    timeLeft = blackTimeLeft;
    timeIncrement = blackTimeIncrement;
  }

  // Don't use all of our time. Search only for 95%. Always leave 1 second as
  // buffer time.
  uint64_t maxSearchTime = (uint64_t) (timeLeft * 0.95) - 1000L;
  if (maxSearchTime < 1) {
    // We don't have enough time left. Search only for 1 millisecond, meaning
    // get a result as fast as we can.
    maxSearchTime = 1;
  }

  // Assume that we still have to do movesToGo number of moves. For every next
  // move (movesToGo - 1) we will receive a time increment.
  search->searchTime = (maxSearchTime + (movesToGo - 1) * timeIncrement) / movesToGo;
  if (search->searchTime > maxSearchTime) {
    search->searchTime = maxSearchTime;
  }

  search->doTimeManagement = true;

  return search;
}

Search::Search(Board& board)
    : board(board),
    timer(timerStopped, doTimeManagement, currentDepth, initialDepth, abort),
    semaphore(0) {
}

void Search::start() {
  if (!running) {
    running = true;
    thread = std::thread(&Search::run, this);
    semaphore.acquire();
  }
}

void Search::stop() {
  if (running) {
    // Signal the search thread that we want to stop it
    abort = true;

    thread.join();
    running = false;
  }
}

void Search::ponderhit() {
  if (running) {
    // Enable time management
    runTimer = true;
    timer.start(searchTime);

    // If we finished the first iteration, we should have a result.
    // In this case check the stop conditions.
    if (currentDepth > initialDepth) {
      checkStopConditions();
    }
  }
}

void Search::run() {
  // Do all initialization before releasing the main thread to JCPI
  startTime = std::chrono::system_clock::now();
  statusStartTime = startTime;
  if (runTimer) {
    timer.start(searchTime);
  }

  // Populate root move list
  bool isCheck = board.isCheck();
  MoveGenerator& moveGenerator = moveGenerators[0];
  MoveList& moves = moveGenerator.getLegalMoves(board, 1, isCheck);
  for (int i = 0; i < moves.size; ++i) {
    int move = moves.entries[i]->move;
    rootMoves.entries[rootMoves.size]->move = move;
    rootMoves.entries[rootMoves.size]->pv.moves[0] = move;
    rootMoves.entries[rootMoves.size]->pv.size = 1;
    ++rootMoves.size;
  }

  // Go...
  semaphore.release();

  //### BEGIN Iterative Deepening
  for (int depth = initialDepth; depth <= searchDepth; ++depth) {
    currentDepth = depth;
    currentMaxDepth = 0;
    sendStatus(false);

    searchRoot(currentDepth, -Value::INFINITE, Value::INFINITE);

    // Sort the root move list, so that the next iteration begins with the
    // best move first.
    rootMoves.sort();

    checkStopConditions();

    if (abort) {
      break;
    }
  }
  //### ENDOF Iterative Deepening

  if (runTimer) {
    timer.stop();
  }

  // Update all stats
  sendStatus(true);

  // Get the best move and convert it to a GenericMove
  if (rootMoves.size > 0) {
    std::cout << "bestmove " << Move::toNotation(rootMoves.entries[0]->move);
    if (rootMoves.entries[0]->pv.size >= 2) {
      std::cout << " ponder " << Move::toNotation(rootMoves.entries[0]->pv.moves[1]);
    }
  } else {
    std::cout << "bestmove nomove";
  }

  std::cout << std::endl;
}

void Search::checkStopConditions() {
  // We will check the stop conditions only if we are using time management,
  // that is if our timer != null.
  if (runTimer && doTimeManagement) {
    if (timerStopped) {
      abort = true;
    } else {
      // Check if we have only one move to make
      if (rootMoves.size == 1) {
        abort = true;
      } else

      // Check if we have a checkmate
      if (std::abs(rootMoves.entries[0]->value) >= Value::CHECKMATE_THRESHOLD
        && std::abs(rootMoves.entries[0]->value) <= Value::CHECKMATE
        && currentDepth >= (Value::CHECKMATE - std::abs(rootMoves.entries[0]->value))) {
        abort = true;
      }
    }
  }
}

void Search::updateSearch(int ply) {
  ++totalNodes;

  if (ply > currentMaxDepth) {
    currentMaxDepth = ply;
  }

  if (searchNodes <= totalNodes) {
    // Hard stop on number of nodes
    abort = true;
  }

  pv[ply].size = 0;

  sendStatus();
}

void Search::searchRoot(int depth, int alpha, int beta) {
  int ply = 0;

  updateSearch(ply);

  // Abort conditions
  if (abort) {
    return;
  }

  for (int i = 0; i < rootMoves.size; ++i) {
    rootMoves.entries[i]->value = -Value::INFINITE;
  }

  for (int i = 0; i < rootMoves.size; ++i) {
    int move = rootMoves.entries[i]->move;

    currentMove = move;
    currentMoveNumber = i + 1;
    sendStatus(false);

    board.makeMove(move);
    int value = -search(depth - 1, -beta, -alpha, ply + 1, board.isCheck());
    board.undoMove(move);

    if (abort) {
      return;
    }

    // Do we have a better value?
    if (value > alpha) {
      alpha = value;

      rootMoves.entries[i]->value = value;
      savePV(move, pv[ply + 1], rootMoves.entries[i]->pv);

      sendMove(*rootMoves.entries[i]);
    }
  }

  if (rootMoves.size == 0) {
    // The root position is a checkmate or stalemate. We cannot search
    // further. Abort!
    abort = true;
  }
}

int Search::search(int depth, int alpha, int beta, int ply, bool isCheck) {
  // We are at a leaf/horizon. So calculate that value.
  if (depth <= 0) {
    // Descend into quiescent
    return quiescent(0, alpha, beta, ply, isCheck);
  }

  updateSearch(ply);

  // Abort conditions
  if (abort || ply == Depth::MAX_PLY) {
    return evaluation.evaluate(board);
  }

  // Check the repetition table and fifty move rule
  if (board.hasInsufficientMaterial() || board.isRepetition() || board.halfmoveClock >= 100) {
    return Value::DRAW;
  }

  // Initialize
  int bestValue = -Value::INFINITE;
  int searchedMoves = 0;

  MoveGenerator& moveGenerator = moveGenerators[ply];
  MoveList& moves = moveGenerator.getMoves(board, depth, isCheck);
  for (int i = 0; i < moves.size; ++i) {
    int move = moves.entries[i]->move;
    int value = bestValue;

    board.makeMove(move);
    if (!board.isAttacked(
        Bitboard::next(board.kings[Color::opposite(board.activeColor)].squares), board.activeColor)) {
      ++searchedMoves;
      value = -search(depth - 1, -beta, -alpha, ply + 1, board.isCheck());
    }
    board.undoMove(move);

    if (abort) {
      return bestValue;
    }

    // Pruning
    if (value > bestValue) {
      bestValue = value;

      // Do we have a better value?
      if (value > alpha) {
        alpha = value;
        savePV(move, pv[ply + 1], pv[ply]);

        // Is the value higher than beta?
        if (value >= beta) {
          // Cut-off
          break;
        }
      }
    }
  }

  // If we cannot move, check for checkmate and stalemate.
  if (searchedMoves == 0) {
    if (isCheck) {
      // We have a check mate. This is bad for us, so return a -CHECKMATE.
      return -Value::CHECKMATE + ply;
    } else {
      // We have a stale mate. Return the draw value.
      return Value::DRAW;
    }
  }

  return bestValue;
}

int Search::quiescent(int depth, int alpha, int beta, int ply, bool isCheck) {
  updateSearch(ply);

  // Abort conditions
  if (abort || ply == Depth::MAX_PLY) {
    return evaluation.evaluate(board);
  }

  // Check the repetition table and fifty move rule
  if (board.hasInsufficientMaterial() || board.isRepetition() || board.halfmoveClock >= 100) {
    return Value::DRAW;
  }

  // Initialize
  int bestValue = -Value::INFINITE;
  int searchedMoves = 0;

  //### BEGIN Stand pat
  if (!isCheck) {
    bestValue = evaluation.evaluate(board);

    // Do we have a better value?
    if (bestValue > alpha) {
      alpha = bestValue;

      // Is the value higher than beta?
      if (bestValue >= beta) {
        // Cut-off
        return bestValue;
      }
    }
  }
  //### ENDOF Stand pat

  // Only generate capturing moves or evasion moves, in case we are in check.
  MoveGenerator& moveGenerator = moveGenerators[ply];
  MoveList& moves = moveGenerator.getMoves(board, depth, isCheck);
  for (int i = 0; i < moves.size; ++i) {
    int move = moves.entries[i]->move;
    int value = bestValue;

    board.makeMove(move);
    if (!board.isAttacked(
        Bitboard::next(board.kings[Color::opposite(board.activeColor)].squares), board.activeColor)) {
      ++searchedMoves;
      bool isCheckingMove = board.isCheck();
      value = -quiescent(depth - 1, -beta, -alpha, ply + 1, isCheckingMove);
    }
    board.undoMove(move);

    if (abort) {
      return bestValue;
    }

    // Pruning
    if (value > bestValue) {
      bestValue = value;

      // Do we have a better value?
      if (value > alpha) {
        alpha = value;
        savePV(move, pv[ply + 1], pv[ply]);

        // Is the value higher than beta?
        if (value >= beta) {
          // Cut-off
          break;
        }
      }
    }
  }

  // If we cannot move, check for checkmate.
  if (searchedMoves == 0 && isCheck) {
    // We have a check mate. This is bad for us, so return a -CHECKMATE.
    return -Value::CHECKMATE + ply;
  }

  return bestValue;
}

void Search::savePV(int move, MoveList::MoveVariation& src, MoveList::MoveVariation& dest) {
  dest.moves[0] = move;
  for (int i = 0; i < src.size; ++i) {
    dest.moves[i + 1] = src.moves[i];
  }
  dest.size = src.size + 1;
}

void Search::sendStatus() {
  if (std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now() - statusStartTime).count() >= 1000) {
    sendStatus(false);
  }
}

void Search::sendStatus(bool force) {
  auto timeDelta = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now() - startTime);

  if (force || timeDelta.count() >= 1000) {
    std::cout << "info";
    std::cout << " depth " << currentDepth;
    std::cout << " seldepth " << currentMaxDepth;
    std::cout << " nodes " << totalNodes;
    std::cout << " time " << timeDelta.count();
    std::cout << " nps " << (timeDelta.count() >= 1000 ? (totalNodes * 1000) / timeDelta.count() : 0);

    if (currentMove != Move::NOMOVE) {
      std::cout << " currmove " << Move::toNotation(currentMove);
      std::cout << " currmovenumber " << currentMoveNumber;
    }

    std::cout << std::endl;

    statusStartTime = std::chrono::system_clock::now();
  }
}

void Search::sendMove(MoveList::Entry& entry) {
  auto timeDelta = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now() - startTime);

  std::cout << "info";
  std::cout << " depth " << currentDepth;
  std::cout << " seldepth " << currentMaxDepth;
  std::cout << " nodes " << totalNodes;
  std::cout << " time " << timeDelta.count();
  std::cout << " nps " << (timeDelta.count() >= 1000 ? (totalNodes * 1000) / timeDelta.count() : 0);

  if (std::abs(entry.value) >= Value::CHECKMATE_THRESHOLD) {
    // Calculate mate distance
    int mateDepth = Value::CHECKMATE - std::abs(entry.value);
    std::cout << " score mate " << ((entry.value > 0) - (entry.value < 0)) * (mateDepth + 1) / 2;
  } else {
    std::cout << " score cp " << entry.value;
  }

  if (entry.pv.size > 0) {
    std::cout << " pv";
    for (int i = 0; i < entry.pv.size; ++i) {
      std::cout << " " << Move::toNotation(entry.pv.moves[i]);
    }
  }

  std::cout << std::endl;

  statusStartTime = std::chrono::system_clock::now();
}

}
