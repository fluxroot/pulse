/*
 * Copyright 2013-2014 the original author or authors.
 *
 * This file is part of Pulse Chess.
 *
 * Pulse Chess is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pulse Chess is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pulse Chess.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.commands.IProtocol;
import com.fluxchess.jcpi.commands.ProtocolBestMoveCommand;
import com.fluxchess.jcpi.commands.ProtocolInformationCommand;
import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.jcpi.models.IntColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * This class implements our search in a separate thread to keep the main
 * thread available for more commands.
 */
public final class Search implements Runnable {

  public static final int MAX_HEIGHT = 256;
  public static final int MAX_DEPTH = 64;

  private final Thread thread = new Thread(this);
  private final Semaphore semaphore = new Semaphore(0);
  private final IProtocol protocol;

  private final Board board;
  private final Evaluation evaluation = new Evaluation();

  // Depth search
  private int searchDepth = MAX_DEPTH;

  // Nodes search
  private long searchNodes = Long.MAX_VALUE;

  // Time & Clock & Ponder search
  private long searchTime = 0;
  private Timer timer = null;
  private boolean timerStopped = false;

  // Moves search
  private final MoveList searchMoves = new MoveList();

  // Search parameters
  private final MoveList rootMoves = new MoveList();
  private boolean abort = false;
  private long startTime = 0;
  private long statusStartTime = 0;
  private long totalNodes = 0;
  private int currentDepth = 0;
  private int currentMove = Move.NOMOVE;
  private int currentMoveNumber = 0;
  private int currentPonderMove = Move.NOMOVE;

  /**
   * This is our search timer for time & clock & ponder searches.
   */
  private final class SearchTimer extends TimerTask {
    @Override
    public void run() {
      timerStopped = true;

      // If we finished the first iteration, we should have a result.
      // In this case abort the search.
      if (currentDepth > 1) {
        abort = true;
      }
    }
  }

  public static Search newDepthSearch(Board board, IProtocol protocol, int searchDepth) {
    if (board == null) throw new IllegalArgumentException();
    if (protocol == null) throw new IllegalArgumentException();
    if (searchDepth < 1 || searchDepth > MAX_DEPTH) throw new IllegalArgumentException();

    Search search = new Search(board, protocol);

    search.searchDepth = searchDepth;

    return search;
  }

  public static Search newNodesSearch(Board board, IProtocol protocol, long searchNodes) {
    if (board == null) throw new IllegalArgumentException();
    if (protocol == null) throw new IllegalArgumentException();
    if (searchNodes < 1) throw new IllegalArgumentException();

    Search search = new Search(board, protocol);

    search.searchNodes = searchNodes;

    return search;
  }

  public static Search newTimeSearch(Board board, IProtocol protocol, long searchTime) {
    if (board == null) throw new IllegalArgumentException();
    if (protocol == null) throw new IllegalArgumentException();
    if (searchTime < 1) throw new IllegalArgumentException();

    Search search = new Search(board, protocol);

    search.searchTime = searchTime;
    search.timer = new Timer(true);

    return search;
  }

  public static Search newMovesSearch(Board board, IProtocol protocol, List<GenericMove> searchMoves) {
    if (board == null) throw new IllegalArgumentException();
    if (protocol == null) throw new IllegalArgumentException();
    if (searchMoves == null) throw new IllegalArgumentException();

    Search search = new Search(board, protocol);

    for (GenericMove move : searchMoves) {
      search.searchMoves.entries[search.searchMoves.size++].move = Move.valueOf(move, board);
    }

    return search;
  }

  public static Search newInfiniteSearch(Board board, IProtocol protocol) {
    if (board == null) throw new IllegalArgumentException();
    if (protocol == null) throw new IllegalArgumentException();

    return new Search(board, protocol);
  }

  public static Search newClockSearch(
    Board board, IProtocol protocol,
    long whiteTimeLeft, long whiteTimeIncrement, long blackTimeLeft, long blackTimeIncrement, int movesToGo
  ) {
    Search search = newPonderSearch(
      board, protocol,
      whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, movesToGo
    );

    search.timer = new Timer(true);

    return search;
  }

  public static Search newPonderSearch(
    Board board, IProtocol protocol,
    long whiteTimeLeft, long whiteTimeIncrement, long blackTimeLeft, long blackTimeIncrement, int movesToGo
  ) {
    if (board == null) throw new IllegalArgumentException();
    if (protocol == null) throw new IllegalArgumentException();
    if (whiteTimeLeft < 1) throw new IllegalArgumentException();
    if (whiteTimeIncrement < 0) throw new IllegalArgumentException();
    if (blackTimeLeft < 1) throw new IllegalArgumentException();
    if (blackTimeIncrement < 0) throw new IllegalArgumentException();
    if (movesToGo < 0) throw new IllegalArgumentException();

    Search search = new Search(board, protocol);

    long timeLeft;
    long timeIncrement;
    if (board.activeColor == IntColor.WHITE) {
      timeLeft = whiteTimeLeft;
      timeIncrement = whiteTimeIncrement;
    } else {
      timeLeft = blackTimeLeft;
      timeIncrement = blackTimeIncrement;
    }

    // Don't use all of our time. Search only for 95%. Always leave 1 second as
    // buffer time.
    long maxSearchTime = (long) (timeLeft * 0.95) - 1000L;
    if (maxSearchTime < 1) {
      // We don't have enough time left. Search only for 1 millisecond, meaning
      // get a result as fast as we can.
      maxSearchTime = 1;
    }

    // Assume that we still have to do movesToGo number of moves. For every next
    // move (movesToGo - 1) we will receive a time increment.
    search.searchTime = (maxSearchTime + (movesToGo - 1) * timeIncrement) / movesToGo;
    if (search.searchTime > maxSearchTime) {
      search.searchTime = maxSearchTime;
    }

    return search;
  }

  private Search(Board board, IProtocol protocol) {
    assert board != null;
    assert protocol != null;

    this.board = board;
    this.protocol = protocol;
  }

  public void start() {
    if (!thread.isAlive()) {
      thread.start();
      try {
        // Wait for initialization
        semaphore.acquire();
      } catch (InterruptedException e) {
        // Do nothing
      }
    }
  }

  public void stop() {
    if (thread.isAlive()) {
      // Signal the search thread that we want to stop it
      abort = true;

      try {
        // Wait for the thread to die
        thread.join();
      } catch (InterruptedException e) {
        // Do nothing
      }
    }
  }

  public void ponderhit() {
    if (thread.isAlive()) {
      // Enable time management
      timer = new Timer(true);
      timer.schedule(new SearchTimer(), searchTime);

      checkStopConditions();
    }
  }

  public void run() {
    // Do all initialization before releasing the main thread to JCPI
    startTime = System.currentTimeMillis();
    statusStartTime = startTime;
    if (timer != null) {
      timer.schedule(new SearchTimer(), searchTime);
    }

    // Populate root move list
    boolean isCheck = board.isCheck();
    if (searchMoves.size == 0) {
      MoveGenerator moveGenerator = MoveGenerator.getMainGenerator(board, 0, isCheck);
      int move;
      while ((move = moveGenerator.next()) != Move.NOMOVE) {
        rootMoves.entries[rootMoves.size].move = move;
        rootMoves.entries[rootMoves.size].pv.moves[0] = move;
        rootMoves.entries[rootMoves.size].pv.size = 1;
        ++rootMoves.size;
      }
    } else {
      for (int i = 0; i < searchMoves.size; ++i) {
        rootMoves.entries[rootMoves.size].move = searchMoves.entries[i].move;
        rootMoves.entries[rootMoves.size].pv.moves[0] = searchMoves.entries[i].move;
        rootMoves.entries[rootMoves.size].pv.size = 1;
        ++rootMoves.size;
      }
    }

    // Go...
    semaphore.release();

    //### BEGIN Iterative Deepening
    for (currentDepth = 1; currentDepth <= searchDepth; ++currentDepth) {
      sendStatus(true);

      rootMoves.save();

      alphaBetaRoot(currentDepth, -Evaluation.CHECKMATE, Evaluation.CHECKMATE, 0, isCheck);

      checkStopConditions();

      if (abort) {
        rootMoves.restore();
        break;
      }
    }
    //### ENDOF Iterative Deepening

    if (timer != null) {
      timer.cancel();
    }

    // Get the best move and convert it to a GenericMove
    GenericMove bestMove = null;
    GenericMove ponderMove = null;
    if (rootMoves.size > 0) {
      bestMove = Move.toGenericMove(rootMoves.entries[0].move);
      if (rootMoves.entries[0].pv.size >= 2) {
        ponderMove = Move.toGenericMove(rootMoves.entries[0].pv.moves[1]);
      }
    }

    // Send the best move to the GUI
    protocol.send(new ProtocolBestMoveCommand(bestMove, ponderMove));
  }

  private void checkStopConditions() {
    // We will check the stop conditions only if we are using time management,
    // that is if our timer != null. Also we cannot stop the search if we don't
    // have any result if using time management.
    if (currentDepth > 1 && timer != null) {
      if (timerStopped) {
        abort = true;
      } else {
        // Check if we have only one move to make
        if (rootMoves.size == 1) {
          abort = true;
        }

        // Check if we have a checkmate
        else if (Math.abs(rootMoves.entries[0].value) > Evaluation.CHECKMATE_THRESHOLD) {
          abort = true;
        }
      }
    }
  }

  private void updateSearch() {
    ++totalNodes;

    if (searchNodes <= totalNodes) {
      // Hard stop on number of nodes
      abort = true;
    }

    sendStatus(false);
  }

  private void alphaBetaRoot(int depth, int alpha, int beta, int height, boolean isCheck) {
    updateSearch();

    // Abort conditions
    if (abort) {
      return;
    }

    // Initialize
    int bestValue = -Evaluation.INFINITY;

    for (int i = 0; i < rootMoves.size; ++i) {
      int move = rootMoves.entries[i].move;

      currentMove = move;
      currentMoveNumber = i + 1;
      sendStatus(true);

      currentPonderMove = Move.NOMOVE;

      board.makeMove(move);
      int value = -alphaBeta(depth - 1, -beta, -alpha, height + 1);
      board.undoMove(move);

      if (abort) {
        return;
      }

      rootMoves.entries[i].value = value;
      rootMoves.entries[i].pv.size = 1;
      if (currentPonderMove != Move.NOMOVE) {
        rootMoves.entries[i].pv.moves[rootMoves.entries[i].pv.size++] = currentPonderMove;
      }

      // Pruning
      if (value > bestValue) {
        bestValue = value;

        // Do we have a better value?
        if (value > alpha) {
          alpha = value;

          // Is the value higher than beta?
          if (value >= beta) {
            // Cut-off
            break;
          }
        }
      }
    }

    // If we cannot move, check for checkmate and stalemate.
    if (bestValue == -Evaluation.INFINITY) {
      if (isCheck) {
        // We have a check mate. This is bad for us, so return a -CHECKMATE.
        bestValue = -Evaluation.CHECKMATE + height;
      } else {
        // We have a stale mate. Return the draw value.
        bestValue = Evaluation.DRAW;
      }
      rootMoves.entries[0].value = bestValue;

      // The root position is a checkmate or stalemate. We cannot search
      // further. Abort!
      abort = true;
    }

    // Sort the root move list, so that the next iteration begins with the
    // best move first.
    rootMoves.sort();

    sendSummary();
  }

  private int alphaBeta(int depth, int alpha, int beta, int height) {
    // We are at a leaf/horizon. So calculate that value.
    if (depth <= 0) {
      // Descend into quiescent
      return quiescent(alpha, beta, height);
    }

    updateSearch();

    // Abort conditions
    if (abort || height == MAX_HEIGHT) {
      return evaluation.evaluate(board);
    }

    // Check the repetition table and fifty move rule
    if (board.isRepetition() || board.halfMoveClock >= 100) {
      return Evaluation.DRAW;
    }

    // Initialize
    int bestValue = -Evaluation.INFINITY;
    int bestMove = Move.NOMOVE;
    boolean isCheck = board.isCheck();

    MoveGenerator moveGenerator = MoveGenerator.getMainGenerator(board, height, isCheck);
    int move;
    while ((move = moveGenerator.next()) != Move.NOMOVE) {
      board.makeMove(move);
      int value = -alphaBeta(depth - 1, -beta, -alpha, height + 1);
      board.undoMove(move);

      if (abort) {
        return bestValue;
      }

      // Pruning
      if (value > bestValue) {
        bestValue = value;
        bestMove = move;

        // Do we have a better value?
        if (value > alpha) {
          alpha = value;

          // Is the value higher than beta?
          if (value >= beta) {
            // Cut-off
            break;
          }
        }
      }
    }

    // If we cannot move, check for checkmate and stalemate.
    if (bestValue == -Evaluation.INFINITY) {
      if (isCheck) {
        // We have a check mate. This is bad for us, so return a -CHECKMATE.
        bestValue = -Evaluation.CHECKMATE + height;
      } else {
        // We have a stale mate. Return the draw value.
        bestValue = Evaluation.DRAW;
      }
    }

    if (height == 1 && bestMove != Move.NOMOVE) {
      currentPonderMove = bestMove;
    }

    return bestValue;
  }

  private int quiescent(int alpha, int beta, int height) {
    updateSearch();

    // Abort conditions
    if (abort || height == MAX_HEIGHT) {
      return evaluation.evaluate(board);
    }

    // Check the repetition table and fifty move rule
    if (board.isRepetition() || board.halfMoveClock >= 100) {
      return Evaluation.DRAW;
    }

    // Initialize
    int bestValue = -Evaluation.INFINITY;
    boolean isCheck = board.isCheck();

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
    MoveGenerator moveGenerator = MoveGenerator.getQuiescentGenerator(board, height, isCheck);
    int move;
    while ((move = moveGenerator.next()) != Move.NOMOVE) {
      board.makeMove(move);
      int value = -quiescent(-beta, -alpha, height + 1);
      board.undoMove(move);

      if (abort) {
        break;
      }

      // Pruning
      if (value > bestValue) {
        bestValue = value;

        // Do we have a better value?
        if (value > alpha) {
          alpha = value;

          // Is the value higher than beta?
          if (value >= beta) {
            // Cut-off
            break;
          }
        }
      }
    }

    if (!abort) {
      // If we cannot move, check for checkmate.
      if (bestValue == -Evaluation.INFINITY) {
        assert isCheck;

        // We have a check mate. This is bad for us, so return a -CHECKMATE.
        bestValue = -Evaluation.CHECKMATE + height;
      }
    }

    return bestValue;
  }

  private void sendStatus(boolean force) {
    long currentTime = System.currentTimeMillis();
    if ((currentTime - startTime >= 1000)
      && (force || (currentTime - statusStartTime) >= 1000)) {
      ProtocolInformationCommand command = new ProtocolInformationCommand();

      command.setDepth(currentDepth);
      command.setNodes(totalNodes);
      command.setTime(currentTime - startTime);
      command.setNps(totalNodes * 1000 / (currentTime - startTime));
      if (currentMove != Move.NOMOVE) {
        command.setCurrentMove(Move.toGenericMove(currentMove));
        command.setCurrentMoveNumber(currentMoveNumber);
      }

      protocol.send(command);

      statusStartTime = System.currentTimeMillis();
    }
  }

  private void sendSummary() {
    long timeDelta = System.currentTimeMillis() - startTime;
    MoveList.Entry bestEntry = rootMoves.entries[0];

    ProtocolInformationCommand command = new ProtocolInformationCommand();

    command.setDepth(currentDepth);
    command.setNodes(totalNodes);
    command.setTime(timeDelta);
    command.setNps(timeDelta >= 1000 ? (totalNodes * 1000) / timeDelta : 0);
    if (Math.abs(bestEntry.value) > Evaluation.CHECKMATE_THRESHOLD) {
      // Calculate mate distance
      int mateDepth = Evaluation.CHECKMATE - Math.abs(bestEntry.value);
      command.setMate(Integer.signum(bestEntry.value) * (mateDepth + 1) / 2);
    } else {
      command.setCentipawns(bestEntry.value);
    }
    List<GenericMove> pv = new ArrayList<>();
    for (int i = 0; i < bestEntry.pv.size; ++i) {
      pv.add(Move.toGenericMove(bestEntry.pv.moves[i]));
    }
    command.setMoveList(pv);

    protocol.send(command);

    statusStartTime = System.currentTimeMillis();
  }

}
