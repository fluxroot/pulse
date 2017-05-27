/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static com.fluxchess.pulse.Castling.*;
import static com.fluxchess.pulse.Color.BLACK;
import static com.fluxchess.pulse.Color.WHITE;
import static com.fluxchess.pulse.MoveType.*;
import static com.fluxchess.pulse.Piece.*;
import static com.fluxchess.pulse.PieceType.NOPIECETYPE;
import static com.fluxchess.pulse.PieceType.QUEEN;
import static com.fluxchess.pulse.Square.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PositionTest {

	@Test
	public void testActiveColor() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);

		// Move white pawn
		int move = Move.valueOf(NORMAL, a2, a3, WHITE_PAWN, NOPIECE, NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.activeColor, is(BLACK));

		// Move black pawn
		move = Move.valueOf(NORMAL, b7, b6, BLACK_PAWN, NOPIECE, NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.activeColor, is(WHITE));
	}

	@Test
	public void testHalfMoveClock() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);

		// Move white pawn
		int move = Move.valueOf(NORMAL, a2, a3, WHITE_PAWN, NOPIECE, NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.halfmoveClock, is(0));

		// Move black pawn
		move = Move.valueOf(NORMAL, b7, b6, BLACK_PAWN, NOPIECE, NOPIECETYPE);
		position.makeMove(move);

		// Move white knight
		move = Move.valueOf(NORMAL, b1, c3, WHITE_KNIGHT, NOPIECE, NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.halfmoveClock, is(1));
	}

	@Test
	public void testFullMoveNumber() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);

		// Move white pawn
		int move = Move.valueOf(NORMAL, a2, a3, WHITE_PAWN, NOPIECE, NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.getFullmoveNumber(), is(1));

		// Move black pawn
		move = Move.valueOf(NORMAL, b7, b6, BLACK_PAWN, NOPIECE, NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.getFullmoveNumber(), is(2));
	}

	@Test
	public void testIsRepetition() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);

		// Move white knight
		int move = Move.valueOf(NORMAL, b1, c3, WHITE_KNIGHT, NOPIECE, NOPIECETYPE);
		position.makeMove(move);

		// Move black knight
		move = Move.valueOf(NORMAL, b8, c6, BLACK_KNIGHT, NOPIECE, NOPIECETYPE);
		position.makeMove(move);

		// Move white knight
		move = Move.valueOf(NORMAL, g1, f3, WHITE_KNIGHT, NOPIECE, NOPIECETYPE);
		position.makeMove(move);

		// Move black knight
		move = Move.valueOf(NORMAL, c6, b8, BLACK_KNIGHT, NOPIECE, NOPIECETYPE);
		position.makeMove(move);

		// Move white knight
		move = Move.valueOf(NORMAL, f3, g1, WHITE_KNIGHT, NOPIECE, NOPIECETYPE);
		position.makeMove(move);

		assertThat(position.isRepetition(), is(true));
	}

	@Test
	public void testHasInsufficientMaterial() {
		Position position = Notation.toPosition("8/4k3/8/8/8/8/2K5/8 w - - 0 1");
		assertThat(position.hasInsufficientMaterial(), is(true));

		position = Notation.toPosition("8/4k3/8/2B5/8/8/2K5/8 b - - 0 1");
		assertThat(position.hasInsufficientMaterial(), is(true));

		position = Notation.toPosition("8/4k3/8/2B3n1/8/8/2K5/8 b - - 0 1");
		assertThat(position.hasInsufficientMaterial(), is(true));
	}

	@Test
	public void testNormalMove() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);
		long zobristKey = position.zobristKey;

		int move = Move.valueOf(NORMAL, a2, a3, WHITE_PAWN, NOPIECE, NOPIECETYPE);
		position.makeMove(move);
		position.undoMove(move);

		assertThat(Notation.fromPosition(position), is(Notation.STANDARDPOSITION));
		assertThat(position.zobristKey, is(zobristKey));
	}

	@Test
	public void testPawnDoubleMove() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);
		long zobristKey = position.zobristKey;

		int move = Move.valueOf(PAWNDOUBLE, a2, a4, WHITE_PAWN, NOPIECE, NOPIECETYPE);
		position.makeMove(move);

		assertThat(position.enPassantSquare, is(a3));

		position.undoMove(move);

		assertThat(Notation.fromPosition(position), is(Notation.STANDARDPOSITION));
		assertThat(position.zobristKey, is(zobristKey));
	}

	@Test
	public void testPawnPromotionMove() {
		Position position = Notation.toPosition("8/P5k1/8/8/2K5/8/8/8 w - - 0 1");
		long zobristKey = position.zobristKey;

		int move = Move.valueOf(PAWNPROMOTION, a7, a8, WHITE_PAWN, NOPIECE, QUEEN);
		position.makeMove(move);

		assertThat(position.board[a8], is(WHITE_QUEEN));

		position.undoMove(move);

		assertThat(Notation.fromPosition(position), is("8/P5k1/8/8/2K5/8/8/8 w - - 0 1"));
		assertThat(position.zobristKey, is(zobristKey));
	}

	@Test
	public void testEnPassantMove() {
		Position position = Notation.toPosition("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1");
		long zobristKey = position.zobristKey;

		// Make en passant move
		int move = Move.valueOf(ENPASSANT, e4, d3, BLACK_PAWN, WHITE_PAWN, NOPIECETYPE);
		position.makeMove(move);

		assertThat(position.board[d4], is(NOPIECE));
		assertThat(position.board[d3], is(BLACK_PAWN));
		assertThat(position.enPassantSquare, is(NOSQUARE));

		position.undoMove(move);

		assertThat(Notation.fromPosition(position), is("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1"));
		assertThat(position.zobristKey, is(zobristKey));
	}

	@Test
	public void testCastlingMove() {
		Position position = Notation.toPosition("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
		long zobristKey = position.zobristKey;

		int move = Move.valueOf(CASTLING, e1, c1, WHITE_KING, NOPIECE, NOPIECETYPE);
		position.makeMove(move);

		assertThat(position.castlingRights & WHITE_QUEENSIDE, is(NOCASTLING));

		position.undoMove(move);

		assertThat(Notation.fromPosition(position), is("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1"));
		assertThat(position.zobristKey, is(zobristKey));

		position = Notation.toPosition("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
		zobristKey = position.zobristKey;

		move = Move.valueOf(CASTLING, e1, g1, WHITE_KING, NOPIECE, NOPIECETYPE);
		position.makeMove(move);

		assertThat(position.castlingRights & WHITE_KINGSIDE, is(NOCASTLING));

		position.undoMove(move);

		assertThat(Notation.fromPosition(position), is("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1"));
		assertThat(position.zobristKey, is(zobristKey));
	}

}
