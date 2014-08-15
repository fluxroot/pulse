/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.commands.IProtocol;
import com.fluxchess.jcpi.commands.ProtocolBestMoveCommand;
import com.fluxchess.jcpi.commands.ProtocolInformationCommand;
import com.fluxchess.jcpi.models.GenericMove;

import java.util.ArrayList;
import java.util.List;
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
  private final Semaphore semaphore = new Semaphore(0);
  private final IProtocol protocol;

  private final Board board;
  private final Evaluation evaluation = new Evaluation();

  // We will store a MoveGenerator for each ply so we don't have to create them
  // in search. (which is expensive)
  private final MoveGenerator[] moveGenerators = new MoveGenerator[Depth.MAX_PLY];

  // Depth search
  private int searchDepth = Depth.MAX_DEPTH;

  // Nodes search
  private long searchNodes = Long.MAX_VALUE;

  // Time & Clock & Ponder search
  private long searchTime = 0;
  private Timer timer = null;
  private boolean timerStopped = false;
  private boolean doTimeManagement = false;

  // Search parameters
  private final MoveList rootMoves = new MoveList();
  private boolean abort = false;
  private long startTime = 0;
  private long statusStartTime = 0;
  private long totalNodes = 0;
  private int initialDepth = 1;
  private int currentDepth = initialDepth;
  private int currentMaxDepth = initialDepth;
  private int currentMove = Move.NOMOVE;
  private int currentMoveNumber = 0;
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

  static Search newDepthSearch(IProtocol protocol, Board board, int searchDepth) {
    if (protocol == null) throw new IllegalArgumentException();
    if (board == null) throw new IllegalArgumentException();
    if (searchDepth < 1 || searchDepth > Depth.MAX_DEPTH) throw new IllegalArgumentException();

    Search search = new Search(protocol, board);

    search.searchDepth = searchDepth;

    return search;
  }

  static Search newNodesSearch(IProtocol protocol, Board board, long searchNodes) {
    if (protocol == null) throw new IllegalArgumentException();
    if (board == null) throw new IllegalArgumentException();
    if (searchNodes < 1) throw new IllegalArgumentException();

    Search search = new Search(protocol, board);

    search.searchNodes = searchNodes;

    return search;
  }

  static Search newTimeSearch(IProtocol protocol, Board board, long searchTime) {
    if (protocol == null) throw new IllegalArgumentException();
    if (board == null) throw new IllegalArgumentException();
    if (searchTime < 1) throw new IllegalArgumentException();

    Search search = new Search(protocol, board);

    search.searchTime = searchTime;
    search.timer = new Timer(true);

    return search;
  }

  static Search newInfiniteSearch(IProtocol protocol, Board board) {
    if (protocol == null) throw new IllegalArgumentException();
    if (board == null) throw new IllegalArgumentException();

    return new Search(protocol, board);
  }

  static Search newClockSearch(
      IProtocol protocol, Board board,
      long whiteTimeLeft, long whiteTimeIncrement, long blackTimeLeft, long blackTimeIncrement, int movesToGo) {
    Search search = newPonderSearch(
        protocol, board,
        whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, movesToGo
    );

    search.timer = new Timer(true);

    return search;
  }

  static Search newPonderSearch(
      IProtocol protocol, Board board,
      long whiteTimeLeft, long whiteTimeIncrement, long blackTimeLeft, long blackTimeIncrement, int movesToGo) {
    if (protocol == null) throw new IllegalArgumentException();
    if (board == null) throw new IllegalArgumentException();
    if (whiteTimeLeft < 1) throw new IllegalArgumentException();
    if (whiteTimeIncrement < 0) throw new IllegalArgumentException();
    if (blackTimeLeft < 1) throw new IllegalArgumentException();
    if (blackTimeIncrement < 0) throw new IllegalArgumentException();
    if (movesToGo < 0) throw new IllegalArgumentException();

    Search search = new Search(protocol, board);

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
    search.searchTime = (maxSearchTime + (movesToGo - 1) * timeIncrement) / movesToGo;
    if (search.searchTime > maxSearchTime) {
      search.searchTime = maxSearchTime;
    }

    search.doTimeManagement = true;

    return search;
  }

  private Search(IProtocol protocol, Board board) {
    assert protocol != null;
    assert board != null;

    this.protocol = protocol;
    this.board = board;

    for (int i = 0; i < Depth.MAX_PLY; ++i) {
      moveGenerators[i] = new MoveGenerator();
    }

    for (int i = 0; i < pv.length; ++i) {
      pv[i] = new MoveVariation();
    }
  }

  void start() {
    if (!thread.isAlive()) {
      thread.setDaemon(true);
      thread.start();
      try {
        // Wait for initialization
        semaphore.acquire();
      } catch (InterruptedException e) {
        // Do nothing
      }
    }
  }

  void stop() {
    if (thread.isAlive()) {
      // Signal the search thread that we want to stop it
      abort = true;

      try {
        // Wait for the thread to die
        thread.join(5000);
      } catch (InterruptedException e) {
        // Do nothing
      }
    }
  }

  void ponderhit() {
    if (thread.isAlive()) {
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

  public void run() {
    // Do all initialization before releasing the main thread to JCPI
    startTime = System.currentTimeMillis();
    statusStartTime = startTime;
    if (timer != null) {
      timer.schedule(new SearchTimer(), searchTime);
    }

    // Populate root move list
    boolean isCheck = board.isCheck();
    MoveGenerator moveGenerator = moveGenerators[0];
    MoveList moves = moveGenerator.getLegalMoves(board, 1, isCheck);
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.entries[i].move;
      rootMoves.entries[rootMoves.size].move = move;
      rootMoves.entries[rootMoves.size].pv.moves[0] = move;
      rootMoves.entries[rootMoves.size].pv.size = 1;
      ++rootMoves.size;
    }

    // Go...
    semaphore.release();

    //### BEGIN Iterative Deepening
    for (int depth = initialDepth; depth <= searchDepth; ++depth) {
      currentDepth = depth;
      currentMaxDepth = 0;
      sendStatus(false);

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
    sendStatus(true);

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
        if (Math.abs(rootMoves.entries[0].value) >= Value.CHECKMATE_THRESHOLD
            && Math.abs(rootMoves.entries[0].value) <= Value.CHECKMATE
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

    sendStatus();
  }

  private void searchRoot(int depth, int alpha, int beta) {
    int ply = 0;

    updateSearch(ply);

    // Abort conditions
    if (abort) {
      return;
    }

    for (int i = 0; i < rootMoves.size; ++i) {
      rootMoves.entries[i].value = -Value.INFINITE;
    }

    for (int i = 0; i < rootMoves.size; ++i) {
      int move = rootMoves.entries[i].move;

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

        rootMoves.entries[i].value = value;
        savePV(move, pv[ply + 1], rootMoves.entries[i].pv);

        sendMove(rootMoves.entries[i]);
      }
    }

    if (rootMoves.size == 0) {
      // The root position is a checkmate or stalemate. We cannot search
      // further. Abort!
      abort = true;
    }
  }

  private int search(int depth, int alpha, int beta, int ply, boolean isCheck) {
    // We are at a leaf/horizon. So calculate that value.
    if (depth <= 0) {
      // Descend into quiescent
      return quiescent(0, alpha, beta, ply, isCheck);
    }

    updateSearch(ply);

    // Abort conditions
    if (abort || ply == Depth.MAX_PLY) {
      return evaluation.evaluate(board);
    }

    // Check the repetition table and fifty move rule
    if (board.hasInsufficientMaterial() || board.isRepetition() || board.halfmoveClock >= 100) {
      return Value.DRAW;
    }

    // Initialize
    int bestValue = -Value.INFINITE;
    int searchedMoves = 0;

    MoveGenerator moveGenerator = moveGenerators[ply];
    MoveList moves = moveGenerator.getMoves(board, depth, isCheck);
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.entries[i].move;
      int value = bestValue;

      board.makeMove(move);
      if (!board.isAttacked(
          Bitboard.next(board.kings[Color.opposite(board.activeColor)].squares), board.activeColor)) {
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
        return -Value.CHECKMATE + ply;
      } else {
        // We have a stale mate. Return the draw value.
        return Value.DRAW;
      }
    }

    return bestValue;
  }

  private int quiescent(int depth, int alpha, int beta, int ply, boolean isCheck) {
    updateSearch(ply);

    // Abort conditions
    if (abort || ply == Depth.MAX_PLY) {
      return evaluation.evaluate(board);
    }

    // Check the repetition table and fifty move rule
    if (board.hasInsufficientMaterial() || board.isRepetition() || board.halfmoveClock >= 100) {
      return Value.DRAW;
    }

    // Initialize
    int bestValue = -Value.INFINITE;
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
    MoveGenerator moveGenerator = moveGenerators[ply];
    MoveList moves = moveGenerator.getMoves(board, depth, isCheck);
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.entries[i].move;
      int value = bestValue;

      board.makeMove(move);
      if (!board.isAttacked(
          Bitboard.next(board.kings[Color.opposite(board.activeColor)].squares), board.activeColor)) {
        ++searchedMoves;
        boolean isCheckingMove = board.isCheck();
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
      return -Value.CHECKMATE + ply;
    }

    return bestValue;
  }

  private void savePV(int move, MoveVariation src, MoveVariation dest) {
    dest.moves[0] = move;
    System.arraycopy(src.moves, 0, dest.moves, 1, src.size);
    dest.size = src.size + 1;
  }

  private void sendStatus() {
    if (System.currentTimeMillis() - statusStartTime >= 1000) {
      sendStatus(false);
    }
  }

  private void sendStatus(boolean force) {
    long timeDelta = System.currentTimeMillis() - startTime;

    if (force || timeDelta >= 1000) {
      ProtocolInformationCommand command = new ProtocolInformationCommand();

      command.setDepth(currentDepth);
      command.setMaxDepth(currentMaxDepth);
      command.setNodes(totalNodes);
      command.setTime(timeDelta);
      command.setNps(timeDelta >= 1000 ? (totalNodes * 1000) / timeDelta : 0);
      if (currentMove != Move.NOMOVE) {
        command.setCurrentMove(Move.toGenericMove(currentMove));
        command.setCurrentMoveNumber(currentMoveNumber);
      }

      protocol.send(command);

      statusStartTime = System.currentTimeMillis();
    }
  }

  private void sendMove(MoveList.Entry entry) {
    long timeDelta = System.currentTimeMillis() - startTime;

    ProtocolInformationCommand command = new ProtocolInformationCommand();

    command.setDepth(currentDepth);
    command.setMaxDepth(currentMaxDepth);
    command.setNodes(totalNodes);
    command.setTime(timeDelta);
    command.setNps(timeDelta >= 1000 ? (totalNodes * 1000) / timeDelta : 0);
    if (Math.abs(entry.value) >= Value.CHECKMATE_THRESHOLD) {
      // Calculate mate distance
      int mateDepth = Value.CHECKMATE - Math.abs(entry.value);
      command.setMate(Integer.signum(entry.value) * (mateDepth + 1) / 2);
    } else {
      command.setCentipawns(entry.value);
    }
    List<GenericMove> moveList = new ArrayList<>();
    for (int i = 0; i < entry.pv.size; ++i) {
      moveList.add(Move.toGenericMove(entry.pv.moves[i]));
    }
    command.setMoveList(moveList);

    protocol.send(command);

    statusStartTime = System.currentTimeMillis();
  }

}
