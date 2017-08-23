/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.commands.*;
import com.fluxchess.jcpi.models.*;
import com.fluxchess.jcpi.options.CheckboxOption;
import com.fluxchess.jcpi.options.Options;
import com.fluxchess.jcpi.protocols.IProtocolHandler;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
				ponderOption.defaultValue));
		commands.add(new EngineDebugCommand(false, true));
		commands.add(new EngineDebugCommand(true, false));
		commands.add(new EngineReadyRequestCommand("test"));
		commands.add(new EngineNewGameCommand());
	}

	@Test
	public void testDepth() throws InterruptedException {
		final GenericMove[] bestMove = {null};
		final GenericMove[] ponderMove = {null};
		final int[] depth = {0};

		final Semaphore semaphore = new Semaphore(0);

		// Test searching to a depth of 2
		commands.add(new EngineAnalyzeCommand(
				new GenericBoard(GenericBoard.STANDARDSETUP),
				singletonList(new GenericMove(GenericPosition.c2, GenericPosition.c4))));
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

			@Override
			public void send(ProtocolInformationCommand command) {
				super.send(command);

				if (command.getDepth() != null) {
					depth[0] = command.getDepth();
				}
			}
		}).run();

		assertThat(semaphore.tryAcquire(10000, MILLISECONDS), is(true));

		assertThat(bestMove[0], is(notNullValue()));
		assertThat(ponderMove[0], is(notNullValue()));
		assertThat(depth[0], is(2));
	}

	@Test
	public void testNodes() throws InterruptedException {
		final GenericMove[] bestMove = {null};
		final GenericMove[] ponderMove = {null};
		final long[] nodes = {0};

		final Semaphore semaphore = new Semaphore(0);

		// Test if we can search only 1 node
		commands.add(new EngineAnalyzeCommand(
				new GenericBoard(GenericBoard.STANDARDSETUP),
				singletonList(new GenericMove(GenericPosition.c2, GenericPosition.c4))));
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

			@Override
			public void send(ProtocolInformationCommand command) {
				super.send(command);

				if (command.getNodes() != null) {
					nodes[0] = command.getNodes();
				}
			}
		}).run();

		assertThat(semaphore.tryAcquire(10000, MILLISECONDS), is(true));

		assertThat(bestMove[0], is(notNullValue()));
		assertThat(ponderMove[0], is(nullValue()));
		assertThat(nodes[0], is(1L));
	}

	@Test
	public void testMoveTime() throws IllegalNotationException {
		// Test searching for 1 second
		commands.add(new EngineAnalyzeCommand(
				new GenericBoard("8/4K3/8/7p/5QkP/6P1/8/8 b - - 2 76"), new ArrayList<GenericMove>()));
		EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
		command.setMoveTime(1000L);
		commands.add(command);

		long startTime = System.currentTimeMillis();
		new Pulse(new ProtocolHandler()).run();
		long stopTime = System.currentTimeMillis();

		assertThat(stopTime - startTime >= 1000L, is(true));
	}

	@Test
	public void testFastMoveTime() {
		// Test seaching for 1 millisecond, which should be stable
		commands.add(new EngineAnalyzeCommand(
				new GenericBoard(GenericBoard.STANDARDSETUP),
				singletonList(new GenericMove(GenericPosition.c2, GenericPosition.c4))));
		EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
		command.setMoveTime(1L);
		commands.add(command);

		new Pulse(new ProtocolHandler()).run();
	}

	@Test
	public void testMoves() {
		// Test searching only specific moves
		commands.add(new EngineAnalyzeCommand(
				new GenericBoard(GenericBoard.STANDARDSETUP),
				singletonList(new GenericMove(GenericPosition.c2, GenericPosition.c4))));
		EngineStartCalculatingCommand command = new EngineStartCalculatingCommand();
		command.setSearchMoveList(asList(
				new GenericMove(GenericPosition.b7, GenericPosition.b6),
				new GenericMove(GenericPosition.f7, GenericPosition.f5)));
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
		commands.add(new EngineAnalyzeCommand(
				new GenericBoard(GenericBoard.STANDARDSETUP),
				singletonList(new GenericMove(GenericPosition.c2, GenericPosition.c4))));
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
		commands.add(new EngineAnalyzeCommand(
				new GenericBoard(GenericBoard.STANDARDSETUP),
				singletonList(new GenericMove(GenericPosition.c2, GenericPosition.c4))));
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
		commands.add(new EngineAnalyzeCommand(
				new GenericBoard(GenericBoard.STANDARDSETUP),
				singletonList(new GenericMove(GenericPosition.c2, GenericPosition.c4))));
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
		commands.add(new EngineAnalyzeCommand(
				new GenericBoard(GenericBoard.STANDARDSETUP),
				singletonList(new GenericMove(GenericPosition.c2, GenericPosition.c4))));
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
			assertThat(command.token, is("test"));
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
