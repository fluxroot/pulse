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
import com.fluxchess.jcpi.options.AbstractOption;
import com.fluxchess.jcpi.protocols.IProtocolHandler;

import java.io.BufferedReader;
import java.io.PrintStream;

/**
 * Pulse uses the Java Chess Protocol Interface (JCPI) to handle the
 * UCI protocol.
 * <p/>
 * We simply extend AbstractEngine and implement the required methods.
 */
public final class Pulse extends AbstractEngine {

  // We have to maintain at least the state of the board and the search.
  private Board board;
  private Search search;

  public static void main(String[] args) {
    // Don't do any fancy stuff here. Just create our engine and
    // run it. JCPI takes care of the rest. It waits for the GUI
    // to issue commands which will call our methods using the
    // visitor pattern.
    new Pulse().run();
  }

  // AbstractEngine provides three constructors to help us connecting to a
  // command channel.

  /**
   * This is our default constructor to create Pulse. It will use the standard
   * input and output.
   */
  public Pulse() {
    initialize();
  }

  /**
   * We could also provide our own input and output streams. We could e.g.
   * connect a network stream to our engine.
   */
  public Pulse(BufferedReader input, PrintStream output) {
    super(input, output);

    initialize();
  }

  /**
   * We could also provide our own IProtocolHandler. We will use this
   * constructor in our unit tests.
   */
  public Pulse(IProtocolHandler handler) {
    super(handler);

    initialize();
  }

  private void initialize() {
    board = new Board(new GenericBoard(GenericBoard.STANDARDSETUP));
    search = Search.newInfiniteSearch(getProtocol(), board);
  }

  protected void quit() {
    // We received a quit command. Stop calculating now and
    // cleanup!
    new EngineStopCalculatingCommand().accept(this);
  }

  public void receive(EngineInitializeRequestCommand command) {
    if (command == null) throw new IllegalArgumentException();

    // We received an initialization request.

    // Precaution for buggy GUIs: Stop calculating first!
    new EngineStopCalculatingCommand().accept(this);

    // We could do some global initialization here. Probably it would be best
    // to initialize all tables here as they will exist until the end of the
    // program.

    // We must send an initialization answer back!
    ProtocolInitializeAnswerCommand answerCommand = new ProtocolInitializeAnswerCommand(
        VersionInfo.current().toString(), "Phokham Nonava"
    );

    // For each supported option, add it to the command.
    for (AbstractOption option : Configuration.options) {
      answerCommand.addOption(option);
    }

    // Send the answer back.
    getProtocol().send(answerCommand);
  }

  public void receive(EngineSetOptionCommand command) {
    if (command == null) throw new IllegalArgumentException();

    // We received a set option command. Just set the option in our
    // configuration.
    if (command.name == null) throw new IllegalArgumentException();

    if (command.name.equalsIgnoreCase(Configuration.ponderOption.name)) {
      if (command.value == null) throw new IllegalArgumentException();

      Configuration.ponder = Boolean.parseBoolean(command.value);
    }
  }

  public void receive(EngineDebugCommand command) {
    if (command == null) throw new IllegalArgumentException();

    // We received a debug command. Pulse currently does not support debug
    // output. However, the following code shows how you would set the debug
    // mode according to the command.
    if (command.toggle) {
      // We have to toggle the debug state.
      Configuration.debug = !Configuration.debug;
    } else {
      // Otherwise set the debug state according to the command.
      Configuration.debug = command.debug;
    }

    // Send a nice string about our debug mode back.
    ProtocolInformationCommand informationCommand = new ProtocolInformationCommand();
    if (Configuration.debug) {
      informationCommand.setString("Debugging mode is on");
    } else {
      informationCommand.setString("Debugging mode is off");
    }
    getProtocol().send(informationCommand);
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
    if (command == null) throw new IllegalArgumentException();

    // We received a new game command.

    // Precaution for buggy GUIs: Stop calculating first!
    new EngineStopCalculatingCommand().accept(this);

    // Initialize per-game settings here.
    board = new Board(new GenericBoard(GenericBoard.STANDARDSETUP));
    search = Search.newInfiniteSearch(getProtocol(), board);
  }

  public void receive(EngineAnalyzeCommand command) {
    if (command == null) throw new IllegalArgumentException();

    // We received an analyze command. Just setup the board.

    // Precaution for buggy GUIs: Stop calculating first!
    new EngineStopCalculatingCommand().accept(this);

    // Create a new internal board from the GenericBoard.
    board = new Board(command.board);

    // Make all moves
    for (GenericMove genericMove : command.moves) {
      // Convert the GenericMove to our internal move representation and make
      // the move on our internal board.
      board.makeMove(Move.valueOf(genericMove, board));
    }

    // Don't start calculating though!
  }

  public void receive(EnginePonderHitCommand command) {
    if (command == null) throw new IllegalArgumentException();

    // We received a ponder hit command. Just call ponderhit().
    search.ponderhit();
  }

  public void receive(EngineStartCalculatingCommand command) {
    if (command == null) throw new IllegalArgumentException();

    // Precaution for buggy GUIs: Stop calculating first!
    new EngineStopCalculatingCommand().accept(this);

    // We received a start calculating command. Extract all parameters from the
    // command and start the search.
    if (command.getDepth() != null) {
      search = Search.newDepthSearch(getProtocol(), board, command.getDepth());
    } else if (command.getNodes() != null) {
      search = Search.newNodesSearch(getProtocol(), board, command.getNodes());
    } else if (command.getMoveTime() != null) {
      search = Search.newTimeSearch(getProtocol(), board, command.getMoveTime());
    } else if (command.getSearchMoveList() != null) {
      search = Search.newMovesSearch(getProtocol(), board, command.getSearchMoveList());
    } else if (command.getInfinite()) {
      search = Search.newInfiniteSearch(getProtocol(), board);
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
        search = Search.newPonderSearch(
            getProtocol(), board,
            whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, searchMovesToGo);
      } else {
        search = Search.newClockSearch(
            getProtocol(), board,
            whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, searchMovesToGo);
      }
    }

    // Go...
    search.start();
  }

  public void receive(EngineStopCalculatingCommand command) {
    if (command == null) throw new IllegalArgumentException();

    // We received a stop calculating command. If a search is running, stop it.
    search.stop();
  }

}
