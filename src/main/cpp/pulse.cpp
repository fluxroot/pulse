/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "pulse.h"
#include "castlingtype.h"
#include "file.h"
#include "rank.h"

#include <cassert>
#include <iostream>
#include <string>
#include <sstream>
#include <cctype>

namespace pulse {

void Pulse::run() {
  std::cin.exceptions(std::iostream::eofbit | std::iostream::failbit | std::iostream::badbit);
  while (true) {
    std::string line;
    std::getline(std::cin, line);
    std::istringstream input(line);

    std::string token;
    input >> std::skipws >> token;
    if (token == "uci") {
      receiveInitialize();
    } else if (token == "isready") {
      receiveReady();
    } else if (token == "ucinewgame") {
      receiveNewGame();
    } else if (token == "position") {
      receivePosition(input);
    } else if (token == "go") {
      receiveGo(input);
    } else if (token == "stop") {
      receiveStop();
    } else if (token == "ponderhit") {
      receivePonderHit();
    } else if (token == "quit") {
      receiveQuit();
      break;
    }
  }
}

void Pulse::receiveQuit() {
  // We received a quit command. Stop calculating now and
  // cleanup!
  search->quit();
}

void Pulse::receiveInitialize() {
  search->stop();

  // We received an initialization request.

  // We could do some global initialization here. Probably it would be best
  // to initialize all tables here as they will exist until the end of the
  // program.

  // We must send an initialization answer back!
  std::cout << "id name Pulse 1.6.1-cpp" << std::endl;
  std::cout << "id author Phokham Nonava" << std::endl;
  std::cout << "uciok" << std::endl;
}

void Pulse::receiveReady() {
  // We received a ready request. We must send the token back as soon as we
  // can. However, because we launch the search in a separate thread, our main
  // thread is able to handle the commands asynchronously to the search. If we
  // don't answer the ready request in time, our engine will probably be
  // killed by the GUI.
  std::cout << "readyok" << std::endl;
}

void Pulse::receiveNewGame() {
  search->stop();

  // We received a new game command.

  // Initialize per-game settings here.
  *currentPosition = Notation::toPosition(Notation::STANDARDPOSITION);
}

void Pulse::receivePosition(std::istringstream& input) {
  search->stop();

  // We received an position command. Just setup the position.

  std::string token;
  input >> token;
  if (token == "startpos") {
    *currentPosition = Notation::toPosition(Notation::STANDARDPOSITION);

    if (input >> token) {
      if (token != "moves") {
        throw std::exception();
      }
    }
  } else if (token == "fen") {
    std::string fen;

    while (input >> token) {
      if (token == "moves") {
        break;
      } else {
        fen += token + " ";
      }
    }

    *currentPosition = Notation::toPosition(fen);
  } else {
    throw std::exception();
  }

  MoveGenerator moveGenerator;

  while (input >> token) {
    // Verify moves
    MoveList<MoveEntry>& moves = moveGenerator.getLegalMoves(*currentPosition, 1, currentPosition->isCheck());
    bool found = false;
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.entries[i]->move;
      if (fromMove(move) == token) {
        currentPosition->makeMove(move);
        found = true;
        break;
      }
    }

    if (!found) {
      throw std::exception();
    }
  }

  // Don't start searching though!
}

void Pulse::receiveGo(std::istringstream& input) {
  search->stop();

  // We received a start command. Extract all parameters from the
  // command and start the search.
  std::string token;
  input >> token;
  if (token == "depth") {
    int searchDepth;
    if (input >> searchDepth) {
      search->newDepthSearch(*currentPosition, searchDepth);
    } else {
      throw std::exception();
    }
  } else if (token == "nodes") {
    uint64_t searchNodes;
    if (input >> searchNodes) {
      search->newNodesSearch(*currentPosition, searchNodes);
    }
  } else if (token == "movetime") {
    uint64_t searchTime;
    if (input >> searchTime) {
      search->newTimeSearch(*currentPosition, searchTime);
    }
  } else if (token == "infinite") {
    search->newInfiniteSearch(*currentPosition);
  } else {
    uint64_t whiteTimeLeft = 1;
    uint64_t whiteTimeIncrement = 0;
    uint64_t blackTimeLeft = 1;
    uint64_t blackTimeIncrement = 0;
    int searchMovesToGo = 40;
    bool ponder = false;

    do {
      if (token == "wtime") {
        if (!(input >> whiteTimeLeft)) {
          throw std::exception();
        }
      } else if (token == "winc") {
        if (!(input >> whiteTimeIncrement)) {
          throw std::exception();
        }
      } else if (token == "btime") {
        if (!(input >> blackTimeLeft)) {
          throw std::exception();
        }
      } else if (token == "binc") {
        if (!(input >> blackTimeIncrement)) {
          throw std::exception();
        }
      } else if (token == "movestogo") {
        if (!(input >> searchMovesToGo)) {
          throw std::exception();
        }
      } else if (token == "ponder") {
        ponder = true;
      }
    } while (input >> token);

    if (ponder) {
      search->newPonderSearch(*currentPosition,
        whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, searchMovesToGo);
    } else {
      search->newClockSearch(*currentPosition,
        whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, searchMovesToGo);
    }
  }

  // Go...
  search->start();
  startTime = std::chrono::system_clock::now();
  statusStartTime = startTime;
}

void Pulse::receivePonderHit() {
  // We received a ponder hit command. Just call ponderhit().
  search->ponderhit();
}

void Pulse::receiveStop() {
  // We received a stop command. If a search is running, stop it.
  search->stop();
}

void Pulse::sendBestMove(int bestMove, int ponderMove) {
  std::cout << "bestmove ";

  if (bestMove != Move::NOMOVE) {
    std::cout << fromMove(bestMove);

    if (ponderMove != Move::NOMOVE) {
      std::cout << " ponder " << fromMove(ponderMove);
    }
  } else {
    std::cout << "nomove";
  }

  std::cout << std::endl;
}

void Pulse::sendStatus(
    int currentDepth, int currentMaxDepth, uint64_t totalNodes, int currentMove, int currentMoveNumber) {
  if (std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now() - statusStartTime).count() >= 1000) {
    sendStatus(false, currentDepth, currentMaxDepth, totalNodes, currentMove, currentMoveNumber);
  }
}

void Pulse::sendStatus(
    bool force, int currentDepth, int currentMaxDepth, uint64_t totalNodes, int currentMove, int currentMoveNumber) {
  auto timeDelta = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now() - startTime);

  if (force || timeDelta.count() >= 1000) {
    std::cout << "info";
    std::cout << " depth " << currentDepth;
    std::cout << " seldepth " << currentMaxDepth;
    std::cout << " nodes " << totalNodes;
    std::cout << " time " << timeDelta.count();
    std::cout << " nps " << (timeDelta.count() >= 1000 ? (totalNodes * 1000) / timeDelta.count() : 0);

    if (currentMove != Move::NOMOVE) {
      std::cout << " currmove " << fromMove(currentMove);
      std::cout << " currmovenumber " << currentMoveNumber;
    }

    std::cout << std::endl;

    statusStartTime = std::chrono::system_clock::now();
  }
}

void Pulse::sendMove(RootEntry entry, int currentDepth, int currentMaxDepth, uint64_t totalNodes) {
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
      std::cout << " " << fromMove(entry.pv.moves[i]);
    }
  }

  std::cout << std::endl;

  statusStartTime = std::chrono::system_clock::now();
}

std::string Pulse::fromMove(int move) {
  std::string notation;

  notation += Notation::fromSquare(Move::getOriginSquare(move));
  notation += Notation::fromSquare(Move::getTargetSquare(move));

  int promotion = Move::getPromotion(move);
  if (promotion != PieceType::NOPIECETYPE) {
    notation += std::tolower(Notation::fromPieceType(promotion));
  }

  return notation;
}

}
