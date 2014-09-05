/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.AbstractEngine;
import com.fluxchess.jcpi.commands.*;
import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericColor;
import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.jcpi.protocols.IProtocolHandler;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Pulse uses the Java Chess Protocol Interface (JCPI) to handle the
 * UCI protocol. We simply extend AbstractEngine and implement the required
 * methods.
 */
final class Jcpi extends AbstractEngine implements Protocol {

  private Search search = new Search(this);
  private long startTime = 0;
  private long statusStartTime = 0;

  private Board currentBoard = Notation.toBoard(new GenericBoard(GenericBoard.STANDARDSETUP));

  // AbstractEngine provides three constructors to help us connecting to a
  // command channel.

  /**
   * This is our default constructor to create Pulse. It will use the standard
   * input and output.
   */
  public Jcpi() {
  }

  /**
   * We could also provide our own input and output streams. We could e.g.
   * connect a network stream to our engine.
   *
   * @param input  a buffered reader.
   * @param output a print stream.
   */
  public Jcpi(BufferedReader input, PrintStream output) {
    super(input, output);
  }

  /**
   * We could also provide our own IProtocolHandler. We will use this
   * constructor in our unit tests.
   *
   * @param handler a protocol handler.
   */
  public Jcpi(IProtocolHandler handler) {
    super(handler);
  }

  protected void quit() {
    // We received a quit command. Stop calculating now and
    // cleanup!
    search.quit();
  }

  public void receive(EngineInitializeRequestCommand command) {
    search.stop();

    // We received an initialization request.

    // We could do some global initialization here. Probably it would be best
    // to initialize all tables here as they will exist until the end of the
    // program.

    // We must send an initialization answer back!
    ProtocolInitializeAnswerCommand answerCommand = new ProtocolInitializeAnswerCommand(
        "Pulse 1.5-java", "Phokham Nonava"
    );

    // Send the answer back.
    getProtocol().send(answerCommand);
  }

  public void receive(EngineSetOptionCommand command) {
  }

  public void receive(EngineDebugCommand command) {
  }

  public void receive(EngineReadyRequestCommand command) {
    if (command == null) throw new IllegalArgumentException();

    // We received a ready request. We must send the token back as soon as we
    // can. However, because we launch the search in a separate thread, our main
    // thread is able to handle the commands asynchronously to the search. If we
    // don't answer the ready request in time, our engine will probably be
    // killed by the GUI.
    getProtocol().send(new ProtocolReadyAnswerCommand(command.token));
  }

  public void receive(EngineNewGameCommand command) {
    search.stop();

    // We received a new game command.

    // Initialize per-game settings here.
    currentBoard = Notation.toBoard(new GenericBoard(GenericBoard.STANDARDSETUP));
  }

  public void receive(EngineAnalyzeCommand command) {
    if (command == null) throw new IllegalArgumentException();

    search.stop();

    // We received an analyze command. Just setup the board.

    // Create a new internal board from the GenericBoard.
    currentBoard = Notation.toBoard(command.board);

    MoveGenerator moveGenerator = new MoveGenerator();

    // Make all moves
    for (GenericMove genericMove : command.moves) {
      // Verify moves
      MoveList moves = moveGenerator.getLegalMoves(currentBoard, 1, currentBoard.isCheck());
      boolean found = false;
      for (int i = 0; i < moves.size; ++i) {
        int move = moves.entries[i].move;
        if (fromMove(move).equals(genericMove)) {
          currentBoard.makeMove(move);
          found = true;
          break;
        }
      }

      if (!found) {
        throw new IllegalArgumentException();
      }
    }

    // Don't start searching though!
  }

  public void receive(EngineStartCalculatingCommand command) {
    if (command == null) throw new IllegalArgumentException();

    search.stop();

    // We received a start command. Extract all parameters from the
    // command and start the search.
    if (command.getDepth() != null) {
      search.newDepthSearch(currentBoard, command.getDepth());
    } else if (command.getNodes() != null) {
      search.newNodesSearch(currentBoard, command.getNodes());
    } else if (command.getMoveTime() != null) {
      search.newTimeSearch(currentBoard, command.getMoveTime());
    } else if (command.getInfinite()) {
      search.newInfiniteSearch(currentBoard);
    } else {
      long whiteTimeLeft = 1;
      if (command.getClock(GenericColor.WHITE) != null) {
        whiteTimeLeft = command.getClock(GenericColor.WHITE);
      }

      long whiteTimeIncrement = 0;
      if (command.getClockIncrement(GenericColor.WHITE) != null) {
        whiteTimeIncrement = command.getClockIncrement(GenericColor.WHITE);
      }

      long blackTimeLeft = 1;
      if (command.getClock(GenericColor.BLACK) != null) {
        blackTimeLeft = command.getClock(GenericColor.BLACK);
      }

      long blackTimeIncrement = 0;
      if (command.getClockIncrement(GenericColor.BLACK) != null) {
        blackTimeIncrement = command.getClockIncrement(GenericColor.BLACK);
      }

      int searchMovesToGo = 40;
      if (command.getMovesToGo() != null) {
        searchMovesToGo = command.getMovesToGo();
      }

      if (command.getPonder()) {
        search.newPonderSearch(currentBoard,
            whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, searchMovesToGo);
      } else {
        search.newClockSearch(currentBoard,
            whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, searchMovesToGo);
      }
    }

    // Go...
    search.start();
    startTime = System.currentTimeMillis();
    statusStartTime = startTime;
  }

  public void receive(EnginePonderHitCommand command) {
    // We received a ponder hit command. Just call ponderhit().
    search.ponderhit();
  }

  public void receive(EngineStopCalculatingCommand command) {
    // We received a stop command. If a search is running, stop it.
    search.stop();
  }

  public void sendBestMove(int bestMove, int ponderMove) {
    GenericMove genericBestMove = null;
    GenericMove genericPonderMove = null;
    if (bestMove != Move.NOMOVE) {
      genericBestMove = fromMove(bestMove);

      if (ponderMove != Move.NOMOVE) {
        genericPonderMove = fromMove(ponderMove);
      }
    }

    // Send the best move to the GUI
    getProtocol().send(new ProtocolBestMoveCommand(genericBestMove, genericPonderMove));
  }

  public void sendStatus(
      int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
    if (System.currentTimeMillis() - statusStartTime >= 1000) {
      sendStatus(false, currentDepth, currentMaxDepth, totalNodes, currentMove, currentMoveNumber);
    }
  }

  public void sendStatus(
      boolean force, int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
    long timeDelta = System.currentTimeMillis() - startTime;

    if (force || timeDelta >= 1000) {
      ProtocolInformationCommand command = new ProtocolInformationCommand();

      command.setDepth(currentDepth);
      command.setMaxDepth(currentMaxDepth);
      command.setNodes(totalNodes);
      command.setTime(timeDelta);
      command.setNps(timeDelta >= 1000 ? (totalNodes * 1000) / timeDelta : 0);
      if (currentMove != Move.NOMOVE) {
        command.setCurrentMove(Jcpi.fromMove(currentMove));
        command.setCurrentMoveNumber(currentMoveNumber);
      }

      getProtocol().send(command);

      statusStartTime = System.currentTimeMillis();
    }
  }

  public void sendMove(MoveList.Entry entry, int currentDepth, int currentMaxDepth, long totalNodes) {
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
      moveList.add(Jcpi.fromMove(entry.pv.moves[i]));
    }
    command.setMoveList(moveList);

    getProtocol().send(command);

    statusStartTime = System.currentTimeMillis();
  }

  static GenericMove fromMove(int move) {
    int type = Move.getType(move);
    int originSquare = Move.getOriginSquare(move);
    int targetSquare = Move.getTargetSquare(move);

    switch (type) {
      case MoveType.NORMAL:
      case MoveType.PAWNDOUBLE:
      case MoveType.ENPASSANT:
      case MoveType.CASTLING:
        return new GenericMove(
            Notation.fromSquare(originSquare),
            Notation.fromSquare(targetSquare)
        );
      case MoveType.PAWNPROMOTION:
        return new GenericMove(
            Notation.fromSquare(originSquare),
            Notation.fromSquare(targetSquare),
            Notation.fromPieceType(Move.getPromotion(move))
        );
      default:
        throw new IllegalArgumentException();
    }
  }

}
