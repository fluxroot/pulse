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
