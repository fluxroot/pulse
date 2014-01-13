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
import com.fluxchess.jcpi.options.CheckboxOption;
import com.fluxchess.jcpi.options.Options;
import com.fluxchess.jcpi.protocols.IProtocolHandler;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PulseTest {

  private final BlockingQueue<IEngineCommand> commands = new LinkedBlockingQueue<>();

  @Before
  public void setUp() {
    commands.clear();

    // Put a default command list into the queue for each test
    commands.add(new EngineInitializeRequestCommand());
    CheckboxOption ponderOption = Options.newPonderOption(true);
    commands.add(new EngineSetOptionCommand(
      ponderOption.name,
      ponderOption.defaultValue)
    );
    commands.add(new EngineDebugCommand(false, true));
    commands.add(new EngineDebugCommand(true, false));
    commands.add(new EngineReadyRequestCommand("test"));
    commands.add(new EngineNewGameCommand());
    commands.add(new EngineAnalyzeCommand(
      new GenericBoard(GenericBoard.STANDARDSETUP),
      Arrays.asList(new GenericMove(GenericPosition.c2, GenericPosition.c4)))
    );
  }

  @Test
  public void testDepth() throws InterruptedException {
    final GenericMove[] bestMove = {null};
    final GenericMove[] ponderMove = {null};

    final Semaphore semaphore = new Semaphore(0);

    // Test searching to a depth of 2
    EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
    command.setDepth(2);
    commands.add(command);

    new Pulse(new ProtocolHandler() {
      @Override
      public void send(ProtocolBestMoveCommand command) {
        super.send(command);

        bestMove[0] = command.bestMove;
        ponderMove[0] = command.ponderMove;

        semaphore.release();
      }
    }).run();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertNotNull(bestMove[0]);
    assertNotNull(ponderMove[0]);
  }

  @Test
  public void testNodes() throws InterruptedException {
    final GenericMove[] bestMove = {null};
    final GenericMove[] ponderMove = {null};

    final Semaphore semaphore = new Semaphore(0);

    // Test if we can search only 1 node
    EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
    command.setNodes(1L);
    commands.add(command);

    new Pulse(new ProtocolHandler() {
      @Override
      public void send(ProtocolBestMoveCommand command) {
        super.send(command);

        bestMove[0] = command.bestMove;
        ponderMove[0] = command.ponderMove;

        semaphore.release();
      }
    }).run();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertNull(bestMove[0]);
    assertNull(ponderMove[0]);
  }

  @Test
  public void testMoveTime() {
    // Test searching for 1 second
    EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
    command.setMoveTime(1000L);
    commands.add(command);

    new Pulse(new ProtocolHandler()).run();
  }

  @Test
  public void testFastMoveTime() {
    // Test seaching for 1 millisecond, which should be stable
    EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
    command.setMoveTime(1L);
    commands.add(command);

    new Pulse(new ProtocolHandler()).run();
  }

  @Test
  public void testMoves() {
    // Test searching only specific moves
    EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
    command.setSearchMoveList(Arrays.asList(
      new GenericMove(GenericPosition.b7, GenericPosition.b6),
      new GenericMove(GenericPosition.f7, GenericPosition.f5)
    ));
    commands.add(command);
    new Timer(true).schedule(new TimerTask() {
      @Override
      public void run() {
        commands.add(new EngineStopCalculatingCommand());
      }
    }, 1000);

    new Pulse(new ProtocolHandler()).run();
  }

  @Test
  public void testInfinite() {
    // Test searching infinitely
    EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
    command.setInfinite();
    commands.add(command);
    new Timer(true).schedule(new TimerTask() {
      @Override
      public void run() {
        commands.add(new EngineStopCalculatingCommand());
      }
    }, 1000);

    new Pulse(new ProtocolHandler()).run();
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

  @Test
  public void testMovesToGo() {
    // Test our time management with moves to go
    EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
    command.setClock(GenericColor.WHITE, 1000L);
    command.setClockIncrement(GenericColor.WHITE, 0L);
    command.setClock(GenericColor.BLACK, 1000L);
    command.setClockIncrement(GenericColor.BLACK, 0L);
    command.setMovesToGo(20);
    commands.add(command);

    new Pulse(new ProtocolHandler()).run();
  }

  @Test
  public void testPonder() {
    // Test if ponder works with time management
    EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
    command.setClock(GenericColor.WHITE, 1000L);
    command.setClockIncrement(GenericColor.WHITE, 0L);
    command.setClock(GenericColor.BLACK, 1000L);
    command.setClockIncrement(GenericColor.BLACK, 0L);
    command.setPonder();
    commands.add(command);
    new Timer(true).schedule(new TimerTask() {
      @Override
      public void run() {
        commands.add(new EnginePonderHitCommand());
      }
    }, 1000);

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
