/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import static com.fluxchess.pulse.MoveList.MoveVariation;

/**
 * This class implements our search in a separate thread to keep the main
 * thread available for more commands.
 */
final class Search implements Runnable {

  private final Thread thread = new Thread(this);
  private final Semaphore wakeupSignal = new Semaphore(0);
  private final Semaphore runSignal = new Semaphore(0);
  private final Semaphore stopSignal = new Semaphore(0);
  private final Protocol protocol;
  private boolean running = false;
  private boolean shutdown = false;

  private Board board;
  private final Evaluation evaluation = new Evaluation();

  // We will store a MoveGenerator for each ply so we don't have to create them
  // in search. (which is expensive)
  private final MoveGenerator[] moveGenerators = new MoveGenerator[Depth.MAX_PLY];

  // Depth search
  private int searchDepth;

  // Nodes search
  private long searchNodes;

  // Time & Clock & Ponder search
  private long searchTime;
  private Timer timer;
  private boolean timerStopped;
  private boolean doTimeManagement;

  // Search parameters
  private final MoveList rootMoves = new MoveList();
  private boolean abort;
  private long totalNodes;
  private final int initialDepth = 1;
  private int currentDepth;
  private int currentMaxDepth;
  private int currentMove;
  private int currentMoveNumber;
  private final MoveVariation[] pv = new MoveVariation[Depth.MAX_PLY + 1];

  /**
   * This is our search timer for time & clock & ponder searches.
   */
  private final class SearchTimer extends TimerTask {
    @Override
    public void run() {
      timerStopped = true;

      // If we finished the first iteration, we should have a result.
      // In this case abort the search.
      if (!doTimeManagement || currentDepth > initialDepth) {
        abort = true;
      }
    }
  }

  void newDepthSearch(Board board, int searchDepth) {
    if (board == null) throw new IllegalArgumentException();
    if (searchDepth < 1 || searchDepth > Depth.MAX_DEPTH) throw new IllegalArgumentException();
    if (running) throw new IllegalStateException();

    reset();

    this.board = board;
    this.searchDepth = searchDepth;
  }

  void newNodesSearch(Board board, long searchNodes) {
    if (board == null) throw new IllegalArgumentException();
    if (searchNodes < 1) throw new IllegalArgumentException();
    if (running) throw new IllegalStateException();

    reset();

    this.board = board;
    this.searchNodes = searchNodes;
  }

  void newTimeSearch(Board board, long searchTime) {
    if (board == null) throw new IllegalArgumentException();
    if (searchTime < 1) throw new IllegalArgumentException();
    if (running) throw new IllegalStateException();

    reset();

    this.board = board;
    this.searchTime = searchTime;
    this.timer = new Timer(true);
  }

  void newInfiniteSearch(Board board) {
    if (board == null) throw new IllegalArgumentException();
    if (running) throw new IllegalStateException();

    reset();

    this.board = board;
  }

  void newClockSearch(Board board,
      long whiteTimeLeft, long whiteTimeIncrement, long blackTimeLeft, long blackTimeIncrement, int movesToGo) {
    newPonderSearch(board,
        whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, movesToGo
    );

    this.timer = new Timer(true);
  }

  void newPonderSearch(Board board,
      long whiteTimeLeft, long whiteTimeIncrement, long blackTimeLeft, long blackTimeIncrement, int movesToGo) {
    if (board == null) throw new IllegalArgumentException();
    if (whiteTimeLeft < 1) throw new IllegalArgumentException();
    if (whiteTimeIncrement < 0) throw new IllegalArgumentException();
    if (blackTimeLeft < 1) throw new IllegalArgumentException();
    if (blackTimeIncrement < 0) throw new IllegalArgumentException();
    if (movesToGo < 0) throw new IllegalArgumentException();
    if (running) throw new IllegalStateException();

    reset();

    this.board = board;

    long timeLeft;
    long timeIncrement;
    if (board.activeColor == Color.WHITE) {
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
    this.searchTime = (maxSearchTime + (movesToGo - 1) * timeIncrement) / movesToGo;
    if (this.searchTime > maxSearchTime) {
      this.searchTime = maxSearchTime;
    }

    this.doTimeManagement = true;
  }

  Search(Protocol protocol) {
    assert protocol != null;

    this.protocol = protocol;

    for (int i = 0; i < Depth.MAX_PLY; ++i) {
      moveGenerators[i] = new MoveGenerator();
    }

    for (int i = 0; i < pv.length; ++i) {
      pv[i] = new MoveVariation();
    }

    reset();

    thread.setDaemon(true);
    thread.start();
  }

  private void reset() {
    searchDepth = Depth.MAX_DEPTH;
    searchNodes = Long.MAX_VALUE;
    searchTime = 0;
    timer = null;
    timerStopped = false;
    doTimeManagement = false;
    rootMoves.size = 0;
    abort = false;
    totalNodes = 0;
    currentDepth = initialDepth;
    currentMaxDepth = 0;
    currentMove = Move.NOMOVE;
    currentMoveNumber = 0;
  }

  synchronized void start() {
    if (!running) {
      try {
        wakeupSignal.release();
        runSignal.acquire();
      } catch (InterruptedException e) {
        // Do nothing
      }
    }
  }

  synchronized void stop() {
    if (running) {
      // Signal the search thread that we want to stop it
      abort = true;

      try {
        stopSignal.acquire();
      } catch (InterruptedException e) {
        // Do nothing
      }
    }
  }

  synchronized void ponderhit() {
    if (running) {
      // Enable time management
      timer = new Timer(true);
      timer.schedule(new SearchTimer(), searchTime);

      // If we finished the first iteration, we should have a result.
      // In this case check the stop conditions.
      if (currentDepth > initialDepth) {
        checkStopConditions();
      }
    }
  }

  synchronized void quit() {
    stop();

    shutdown = true;
    wakeupSignal.release();

    // Wait for the thread to die
    try {
      thread.join(5000);
    } catch (InterruptedException e) {
      // Do nothing
    }
  }

  public void run() {
    while (true) {
      try {
        wakeupSignal.acquire();
      } catch (InterruptedException e) {
        // Do nothing
      }

      if (shutdown) {
        break;
      }

      // Do all initialization before releasing the main thread to JCPI
      if (timer != null) {
        timer.schedule(new SearchTimer(), searchTime);
      }

      // Populate root move list
      MoveList moves = moveGenerators[0].getLegalMoves(board, 1, board.isCheck());
      for (int i = 0; i < moves.size; ++i) {
        int move = moves.entries[i].move;
        rootMoves.entries[rootMoves.size].move = move;
        rootMoves.entries[rootMoves.size].pv.moves[0] = move;
        rootMoves.entries[rootMoves.size].pv.size = 1;
        ++rootMoves.size;
      }

      // Go...
      stopSignal.drainPermits();
      running = true;
      runSignal.release();

      //### BEGIN Iterative Deepening
      for (int depth = initialDepth; depth <= searchDepth; ++depth) {
        currentDepth = depth;
        currentMaxDepth = 0;
        protocol.sendStatus(false, currentDepth, currentMaxDepth, totalNodes, currentMove, currentMoveNumber);

        searchRoot(currentDepth, -Value.INFINITE, Value.INFINITE);

        // Sort the root move list, so that the next iteration begins with the
        // best move first.
        rootMoves.sort();

        checkStopConditions();

        if (abort) {
          break;
        }
      }
      //### ENDOF Iterative Deepening

      if (timer != null) {
        timer.cancel();
      }

      // Update all stats
      protocol.sendStatus(true, currentDepth, currentMaxDepth, totalNodes, currentMove, currentMoveNumber);

      // Send the best move and ponder move
      int bestMove = Move.NOMOVE;
      int ponderMove = Move.NOMOVE;
      if (rootMoves.size > 0) {
        bestMove = rootMoves.entries[0].move;
        if (rootMoves.entries[0].pv.size >= 2) {
          ponderMove = rootMoves.entries[0].pv.moves[1];
        }
      }

      // Send the best move to the GUI
      protocol.sendBestMove(bestMove, ponderMove);

      running = false;
      stopSignal.release();
    }
  }

  private void checkStopConditions() {
    // We will check the stop conditions only if we are using time management,
    // that is if our timer != null.
    if (timer != null && doTimeManagement) {
      if (timerStopped) {
        abort = true;
      } else {
        // Check if we have only one move to make
        if (rootMoves.size == 1) {
          abort = true;
        } else

        // Check if we have a checkmate
        if (Value.isCheckmate(rootMoves.entries[0].value)
            && currentDepth >= (Value.CHECKMATE - Math.abs(rootMoves.entries[0].value))) {
          abort = true;
        }
      }
    }
  }

  private void updateSearch(int ply) {
    ++totalNodes;

    if (ply > currentMaxDepth) {
      currentMaxDepth = ply;
    }

    if (searchNodes <= totalNodes) {
      // Hard stop on number of nodes
      abort = true;
    }

    pv[ply].size = 0;

    protocol.sendStatus(currentDepth, currentMaxDepth, totalNodes, currentMove, currentMoveNumber);
  }

  private void searchRoot(int depth, int alpha, int beta) {
    int ply = 0;

    updateSearch(ply);

    // Abort conditions
    if (abort) {
      return;
    }

    // Reset all values, so the best move is pushed to the front
    for (int i = 0; i < rootMoves.size; ++i) {
      rootMoves.entries[i].value = -Value.INFINITE;
    }

    for (int i = 0; i < rootMoves.size; ++i) {
      int move = rootMoves.entries[i].move;

      currentMove = move;
      currentMoveNumber = i + 1;
      protocol.sendStatus(false, currentDepth, currentMaxDepth, totalNodes, currentMove, currentMoveNumber);

      board.makeMove(move);
      int value = -search(depth - 1, -beta, -alpha, ply + 1);
      board.undoMove(move);

      if (abort) {
        return;
      }

      // Do we have a better value?
      if (value > alpha) {
        alpha = value;

        // We found a new best move
        rootMoves.entries[i].value = value;
        savePV(move, pv[ply + 1], rootMoves.entries[i].pv);

        protocol.sendMove(rootMoves.entries[i], currentDepth, currentMaxDepth, totalNodes);
      }
    }

    if (rootMoves.size == 0) {
      // The root position is a checkmate or stalemate. We cannot search
      // further. Abort!
      abort = true;
    }
  }

  private int search(int depth, int alpha, int beta, int ply) {
    // We are at a leaf/horizon. So calculate that value.
    if (depth <= 0) {
      // Descend into quiescent
      return quiescent(0, alpha, beta, ply);
    }

    updateSearch(ply);

    // Abort conditions
    if (abort || ply == Depth.MAX_PLY) {
      return evaluation.evaluate(board);
    }

    // Check insufficient material, repetition and fifty move rule
    if (board.isRepetition() || board.hasInsufficientMaterial() || board.halfmoveClock >= 100) {
      return Value.DRAW;
    }

    // Initialize
    int bestValue = -Value.INFINITE;
    int searchedMoves = 0;
    boolean isCheck = board.isCheck();

    MoveList moves = moveGenerators[ply].getMoves(board, depth, isCheck);
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.entries[i].move;
      int value = bestValue;

      board.makeMove(move);
      if (!board.isCheck(Color.opposite(board.activeColor))) {
        ++searchedMoves;
        value = -search(depth - 1, -beta, -alpha, ply + 1);
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
        return -Value.CHECKMATE + ply;
      } else {
        // We have a stale mate. Return the draw value.
        return Value.DRAW;
      }
    }

    return bestValue;
  }

  private int quiescent(int depth, int alpha, int beta, int ply) {
    updateSearch(ply);

    // Abort conditions
    if (abort || ply == Depth.MAX_PLY) {
      return evaluation.evaluate(board);
    }

    // Check insufficient material, repetition and fifty move rule
    if (board.isRepetition() || board.hasInsufficientMaterial() || board.halfmoveClock >= 100) {
      return Value.DRAW;
    }

    // Initialize
    int bestValue = -Value.INFINITE;
    int searchedMoves = 0;
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

    MoveList moves = moveGenerators[ply].getMoves(board, depth, isCheck);
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.entries[i].move;
      int value = bestValue;

      board.makeMove(move);
      if (!board.isCheck(Color.opposite(board.activeColor))) {
        ++searchedMoves;
        value = -quiescent(depth - 1, -beta, -alpha, ply + 1);
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
      return -Value.CHECKMATE + ply;
    }

    return bestValue;
  }

  private void savePV(int move, MoveVariation src, MoveVariation dest) {
    dest.moves[0] = move;
    System.arraycopy(src.moves, 0, dest.moves, 1, src.size);
    dest.size = src.size + 1;
  }

}
