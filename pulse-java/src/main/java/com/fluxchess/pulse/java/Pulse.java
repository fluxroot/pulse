/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java;

import com.fluxchess.jcpi.AbstractEngine;
import com.fluxchess.jcpi.commands.EngineAnalyzeCommand;
import com.fluxchess.jcpi.commands.EngineDebugCommand;
import com.fluxchess.jcpi.commands.EngineInitializeRequestCommand;
import com.fluxchess.jcpi.commands.EngineNewGameCommand;
import com.fluxchess.jcpi.commands.EnginePonderHitCommand;
import com.fluxchess.jcpi.commands.EngineReadyRequestCommand;
import com.fluxchess.jcpi.commands.EngineSetOptionCommand;
import com.fluxchess.jcpi.commands.EngineStartCalculatingCommand;
import com.fluxchess.jcpi.commands.EngineStopCalculatingCommand;
import com.fluxchess.jcpi.commands.ProtocolBestMoveCommand;
import com.fluxchess.jcpi.commands.ProtocolInformationCommand;
import com.fluxchess.jcpi.commands.ProtocolInitializeAnswerCommand;
import com.fluxchess.jcpi.commands.ProtocolReadyAnswerCommand;
import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericColor;
import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.jcpi.protocols.IProtocolHandler;
import com.fluxchess.pulse.java.model.Move;
import com.fluxchess.pulse.java.model.Value;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static com.fluxchess.pulse.java.model.Move.NOMOVE;
import static com.fluxchess.pulse.java.model.MoveType.CASTLING;
import static com.fluxchess.pulse.java.model.MoveType.ENPASSANT;
import static com.fluxchess.pulse.java.model.MoveType.NORMAL;
import static com.fluxchess.pulse.java.model.MoveType.PAWNDOUBLE;
import static com.fluxchess.pulse.java.model.MoveType.PAWNPROMOTION;
import static java.lang.Integer.signum;
import static java.lang.Math.abs;
import static java.lang.System.currentTimeMillis;

/**
 * Pulse uses the Java Chess Protocol Interface (JCPI) to handle the
 * UCI protocol. We simply extend AbstractEngine and implement the required
 * methods.
 */
public final class Pulse extends AbstractEngine implements Protocol {

	private boolean debug = false;

	private final Search search = new Search(this);
	private long startTime = 0;
	private long statusStartTime = 0;

	private Position currentPosition = Notation.toPosition(new GenericBoard(GenericBoard.STANDARDSETUP));

	// AbstractEngine provides three constructors to help us connecting to a
	// command channel.

	/**
	 * We could also provide our own input and output streams. We could e.g.
	 * connect a network stream to our engine.
	 *
	 * @param input  a buffered reader.
	 * @param output a print stream.
	 */
	public Pulse(BufferedReader input, PrintStream output) {
		super(input, output);
	}

	/**
	 * We could also provide our own IProtocolHandler. We will use this
	 * constructor in our unit tests.
	 *
	 * @param handler a protocol handler.
	 */
	public Pulse(IProtocolHandler handler) {
		super(handler);
	}

	@Override
	protected void quit() {
		// We received a quit command. Stop calculating now and
		// cleanup!
		search.quit();
	}

	@Override
	public void receive(EngineInitializeRequestCommand command) {
		search.stop();

		// We received an initialization request.

		// We could do some global initialization here. Probably it would be best
		// to initialize all tables here as they will exist until the end of the
		// program.

		// We must send an initialization answer back!
		ProtocolInitializeAnswerCommand answerCommand = new ProtocolInitializeAnswerCommand(
			"Pulse Java 2.0.0", "Phokham Nonava"
		);

		// Send the answer back.
		getProtocol().send(answerCommand);
	}

	@Override
	public void receive(EngineSetOptionCommand command) {
	}

	@Override
	public void receive(EngineDebugCommand command) {
		if (command.toggle) {
			debug = !debug;
		} else {
			debug = command.debug;
		}

		ProtocolInformationCommand informationCommand = new ProtocolInformationCommand();
		if (debug) {
			informationCommand.setString("Turning on debugging mode");
		} else {
			informationCommand.setString("Turning off debugging mode");
		}
		getProtocol().send(informationCommand);
	}

	@Override
	public void receive(EngineReadyRequestCommand command) {
		// We received a ready request. We must send the token back as soon as we
		// can. However, because we launch the search in a separate thread, our main
		// thread is able to handle the commands asynchronously to the search. If we
		// don't answer the ready request in time, our engine will probably be
		// killed by the GUI.
		getProtocol().send(new ProtocolReadyAnswerCommand(command.token));
	}

	@Override
	public void receive(EngineNewGameCommand command) {
		search.stop();

		// We received a new game command.

		// Initialize per-game settings here.
		currentPosition = Notation.toPosition(new GenericBoard(GenericBoard.STANDARDSETUP));
	}

	@Override
	public void receive(EngineAnalyzeCommand command) {
		search.stop();

		// We received an analyze command. Just setup the position.

		// Create a new internal position from the GenericBoard.
		currentPosition = Notation.toPosition(command.board);

		MoveGenerator moveGenerator = new MoveGenerator();

		// Make all moves
		for (GenericMove genericMove : command.moves) {
			// Verify moves
			MoveList<MoveList.MoveEntry> moves = moveGenerator.getLegalMoves(currentPosition, 1, currentPosition.isCheck());
			boolean found = false;
			for (int i = 0; i < moves.size; i++) {
				int move = moves.entries[i].move;
				if (fromMove(move).equals(genericMove)) {
					currentPosition.makeMove(move);
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

	@Override
	public void receive(EngineStartCalculatingCommand command) {
		search.stop();

		// We received a start command. Extract all parameters from the
		// command and start the search.
		if (command.getDepth() != null) {
			search.newDepthSearch(currentPosition, command.getDepth());
		} else if (command.getNodes() != null) {
			search.newNodesSearch(currentPosition, command.getNodes());
		} else if (command.getMoveTime() != null) {
			search.newTimeSearch(currentPosition, command.getMoveTime());
		} else if (command.getInfinite()) {
			search.newInfiniteSearch(currentPosition);
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
				search.newPonderSearch(currentPosition,
					whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, searchMovesToGo);
			} else {
				search.newClockSearch(currentPosition,
					whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, searchMovesToGo);
			}
		}

		// Go...
		search.start();
		startTime = currentTimeMillis();
		statusStartTime = startTime;
	}

	@Override
	public void receive(EnginePonderHitCommand command) {
		// We received a ponder hit command. Just call ponderhit().
		search.ponderhit();
	}

	@Override
	public void receive(EngineStopCalculatingCommand command) {
		// We received a stop command. If a search is running, stop it.
		search.stop();
	}

	@Override
	public void sendBestMove(int bestMove, int ponderMove) {
		GenericMove genericBestMove = null;
		GenericMove genericPonderMove = null;
		if (bestMove != NOMOVE) {
			genericBestMove = fromMove(bestMove);

			if (ponderMove != NOMOVE) {
				genericPonderMove = fromMove(ponderMove);
			}
		}

		// Send the best move to the GUI
		getProtocol().send(new ProtocolBestMoveCommand(genericBestMove, genericPonderMove));
	}

	@Override
	public void sendStatus(
		int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
		if (currentTimeMillis() - statusStartTime >= 1000) {
			sendStatus(false, currentDepth, currentMaxDepth, totalNodes, currentMove, currentMoveNumber);
		}
	}

	@Override
	public void sendStatus(
		boolean force, int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
		long timeDelta = currentTimeMillis() - startTime;

		if (force || timeDelta >= 1000) {
			ProtocolInformationCommand command = new ProtocolInformationCommand();

			command.setDepth(currentDepth);
			command.setMaxDepth(currentMaxDepth);
			command.setNodes(totalNodes);
			command.setTime(timeDelta);
			command.setNps(timeDelta >= 1000 ? (totalNodes * 1000) / timeDelta : 0);
			if (currentMove != NOMOVE) {
				command.setCurrentMove(fromMove(currentMove));
				command.setCurrentMoveNumber(currentMoveNumber);
			}

			getProtocol().send(command);

			statusStartTime = currentTimeMillis();
		}
	}

	@Override
	public void sendMove(MoveList.RootEntry entry, int currentDepth, int currentMaxDepth, long totalNodes) {
		long timeDelta = currentTimeMillis() - startTime;

		ProtocolInformationCommand command = new ProtocolInformationCommand();

		command.setDepth(currentDepth);
		command.setMaxDepth(currentMaxDepth);
		command.setNodes(totalNodes);
		command.setTime(timeDelta);
		command.setNps(timeDelta >= 1000 ? (totalNodes * 1000) / timeDelta : 0);
		if (abs(entry.value) >= Value.CHECKMATE_THRESHOLD) {
			// Calculate mate distance
			int mateDepth = Value.CHECKMATE - abs(entry.value);
			command.setMate(signum(entry.value) * (mateDepth + 1) / 2);
		} else {
			command.setCentipawns(entry.value);
		}
		List<GenericMove> moveList = new ArrayList<>();
		for (int i = 0; i < entry.pv.size; i++) {
			moveList.add(fromMove(entry.pv.moves[i]));
		}
		command.setMoveList(moveList);

		getProtocol().send(command);

		statusStartTime = currentTimeMillis();
	}

	@Override
	public void sendInfo(String message) {
		ProtocolInformationCommand command = new ProtocolInformationCommand();
		command.setString(message);
		getProtocol().send(command);
	}

	protected Pulse() {
		super();
	}

	@Override
	public void sendDebug(String message) {
		if (debug) {
			ProtocolInformationCommand command = new ProtocolInformationCommand();
			command.setString(message);
			getProtocol().send(command);
		}
	}

	static GenericMove fromMove(int move) {
		int type = Move.getType(move);
		int originSquare = Move.getOriginSquare(move);
		int targetSquare = Move.getTargetSquare(move);

		switch (type) {
			case NORMAL:
			case PAWNDOUBLE:
			case ENPASSANT:
			case CASTLING:
				return new GenericMove(
					Notation.fromSquare(originSquare),
					Notation.fromSquare(targetSquare)
				);
			case PAWNPROMOTION:
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
