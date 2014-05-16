/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.commands.*;
import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericColor;
import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.jcpi.models.GenericPosition;
import com.fluxchess.jcpi.protocols.IProtocolHandler;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PulseTest {

  private final BlockingQueue<IEngineCommand> commands = new LinkedBlockingQueue<>();

  @Before
  public void setUp() {
    commands.clear();

    // Put a default command list into the queue for each test
    commands.add(new EngineInitializeRequestCommand());
    commands.add(new EngineReadyRequestCommand("test"));
    commands.add(new EngineNewGameCommand());
    commands.add(new EngineAnalyzeCommand(
        new GenericBoard(GenericBoard.STANDARDSETUP),
        Arrays.asList(new GenericMove(GenericPosition.c2, GenericPosition.c4)))
    );
  }

  @Test
  public void testClock() {
    // Test if our time management works
    EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
    command.setClock(GenericColor.WHITE, 1000L);
    command.setClockIncrement(GenericColor.WHITE, 0L);
    command.setClock(GenericColor.BLACK, 1000L);
    command.setClockIncrement(GenericColor.BLACK, 0L);
    commands.add(command);

    new Pulse(new ProtocolHandler()).run();
  }

  private class ProtocolHandler implements IProtocolHandler {

    @Override
    public IEngineCommand receive() throws IOException {
      IEngineCommand command = null;
      try {
        command = commands.take();
      } catch (InterruptedException e) {
        fail();
      }

      return command;
    }

    @Override
    public void send(ProtocolInitializeAnswerCommand command) {
    }

    @Override
    public void send(ProtocolReadyAnswerCommand command) {
      assertEquals("test", command.token);
    }

    @Override
    public void send(ProtocolBestMoveCommand command) {
      commands.add(new EngineQuitCommand());
    }

    @Override
    public void send(ProtocolInformationCommand command) {
    }

  }

}
