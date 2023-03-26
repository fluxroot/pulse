/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java;

import com.fluxchess.pulse.java.model.Move;
import com.fluxchess.pulse.java.model.Piece;
import com.fluxchess.pulse.java.model.PieceType;
import org.junit.jupiter.api.Test;

import static com.fluxchess.pulse.java.model.Castling.NOCASTLING;
import static com.fluxchess.pulse.java.model.Castling.WHITE_KINGSIDE;
import static com.fluxchess.pulse.java.model.Castling.WHITE_QUEENSIDE;
import static com.fluxchess.pulse.java.model.Color.BLACK;
import static com.fluxchess.pulse.java.model.Color.WHITE;
import static com.fluxchess.pulse.java.model.MoveType.CASTLING;
import static com.fluxchess.pulse.java.model.MoveType.ENPASSANT;
import static com.fluxchess.pulse.java.model.MoveType.NORMAL;
import static com.fluxchess.pulse.java.model.MoveType.PAWNDOUBLE;
import static com.fluxchess.pulse.java.model.MoveType.PAWNPROMOTION;
import static com.fluxchess.pulse.java.model.Square.NOSQUARE;
import static com.fluxchess.pulse.java.model.Square.a2;
import static com.fluxchess.pulse.java.model.Square.a3;
import static com.fluxchess.pulse.java.model.Square.a4;
import static com.fluxchess.pulse.java.model.Square.a7;
import static com.fluxchess.pulse.java.model.Square.a8;
import static com.fluxchess.pulse.java.model.Square.b1;
import static com.fluxchess.pulse.java.model.Square.b6;
import static com.fluxchess.pulse.java.model.Square.b7;
import static com.fluxchess.pulse.java.model.Square.b8;
import static com.fluxchess.pulse.java.model.Square.c1;
import static com.fluxchess.pulse.java.model.Square.c3;
import static com.fluxchess.pulse.java.model.Square.c6;
import static com.fluxchess.pulse.java.model.Square.d3;
import static com.fluxchess.pulse.java.model.Square.d4;
import static com.fluxchess.pulse.java.model.Square.e1;
import static com.fluxchess.pulse.java.model.Square.e4;
import static com.fluxchess.pulse.java.model.Square.f3;
import static com.fluxchess.pulse.java.model.Square.g1;
import static org.assertj.core.api.Assertions.assertThat;

class PositionTest {

	@Test
	void testActiveColor() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);

		// Move white pawn
		int move = Move.valueOf(NORMAL, a2, a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.activeColor).isEqualTo(BLACK);

		// Move black pawn
		move = Move.valueOf(NORMAL, b7, b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.activeColor).isEqualTo(WHITE);
	}

	@Test
	void testHalfMoveClock() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);

		// Move white pawn
		int move = Move.valueOf(NORMAL, a2, a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.halfmoveClock).isEqualTo(0);

		// Move black pawn
		move = Move.valueOf(NORMAL, b7, b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);

		// Move white knight
		move = Move.valueOf(NORMAL, b1, c3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.halfmoveClock).isEqualTo(1);
	}

	@Test
	void testFullMoveNumber() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);

		// Move white pawn
		int move = Move.valueOf(NORMAL, a2, a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.getFullmoveNumber()).isEqualTo(1);

		// Move black pawn
		move = Move.valueOf(NORMAL, b7, b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);
		assertThat(position.getFullmoveNumber()).isEqualTo(2);
	}

	@Test
	void testIsRepetition() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);

		// Move white knight
		int move = Move.valueOf(NORMAL, b1, c3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);

		// Move black knight
		move = Move.valueOf(NORMAL, b8, c6, Piece.BLACK_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);

		// Move white knight
		move = Move.valueOf(NORMAL, g1, f3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);

		// Move black knight
		move = Move.valueOf(NORMAL, c6, b8, Piece.BLACK_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);

		// Move white knight
		move = Move.valueOf(NORMAL, f3, g1, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);

		assertThat(position.isRepetition()).isEqualTo(true);
	}

	@Test
	void testHasInsufficientMaterial() {
		Position position = Notation.toPosition("8/4k3/8/8/8/8/2K5/8 w - - 0 1");
		assertThat(position.hasInsufficientMaterial()).isEqualTo(true);

		position = Notation.toPosition("8/4k3/8/2B5/8/8/2K5/8 b - - 0 1");
		assertThat(position.hasInsufficientMaterial()).isEqualTo(true);

		position = Notation.toPosition("8/4k3/8/2B3n1/8/8/2K5/8 b - - 0 1");
		assertThat(position.hasInsufficientMaterial()).isEqualTo(true);
	}

	@Test
	void testNormalMove() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);
		long zobristKey = position.zobristKey;

		int move = Move.valueOf(NORMAL, a2, a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);
		position.undoMove(move);

		assertThat(Notation.fromPosition(position)).isEqualTo(Notation.STANDARDPOSITION);
		assertThat(position.zobristKey).isEqualTo(zobristKey);
	}

	@Test
	void testPawnDoubleMove() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);
		long zobristKey = position.zobristKey;

		int move = Move.valueOf(PAWNDOUBLE, a2, a4, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);

		assertThat(position.enPassantSquare).isEqualTo(a3);

		position.undoMove(move);

		assertThat(Notation.fromPosition(position)).isEqualTo(Notation.STANDARDPOSITION);
		assertThat(position.zobristKey).isEqualTo(zobristKey);
	}

	@Test
	void testPawnPromotionMove() {
		Position position = Notation.toPosition("8/P5k1/8/8/2K5/8/8/8 w - - 0 1");
		long zobristKey = position.zobristKey;

		int move = Move.valueOf(PAWNPROMOTION, a7, a8, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.QUEEN);
		position.makeMove(move);

		assertThat(position.board[a8]).isEqualTo(Piece.WHITE_QUEEN);

		position.undoMove(move);

		assertThat(Notation.fromPosition(position)).isEqualTo("8/P5k1/8/8/2K5/8/8/8 w - - 0 1");
		assertThat(position.zobristKey).isEqualTo(zobristKey);
	}

	@Test
	void testEnPassantMove() {
		Position position = Notation.toPosition("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1");
		long zobristKey = position.zobristKey;

		// Make en passant move
		int move = Move.valueOf(ENPASSANT, e4, d3, Piece.BLACK_PAWN, Piece.WHITE_PAWN, PieceType.NOPIECETYPE);
		position.makeMove(move);

		assertThat(position.board[d4]).isEqualTo(Piece.NOPIECE);
		assertThat(position.board[d3]).isEqualTo(Piece.BLACK_PAWN);
		assertThat(position.enPassantSquare).isEqualTo(NOSQUARE);

		position.undoMove(move);

		assertThat(Notation.fromPosition(position)).isEqualTo("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1");
		assertThat(position.zobristKey).isEqualTo(zobristKey);
	}

	@Test
	void testCastlingMove() {
		Position position = Notation.toPosition("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
		long zobristKey = position.zobristKey;

		int move = Move.valueOf(CASTLING, e1, c1, Piece.WHITE_KING, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);

		assertThat(position.castlingRights & WHITE_QUEENSIDE).isEqualTo(NOCASTLING);

		position.undoMove(move);

		assertThat(Notation.fromPosition(position)).isEqualTo("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
		assertThat(position.zobristKey).isEqualTo(zobristKey);

		position = Notation.toPosition("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
		zobristKey = position.zobristKey;

		move = Move.valueOf(CASTLING, e1, g1, Piece.WHITE_KING, Piece.NOPIECE, PieceType.NOPIECETYPE);
		position.makeMove(move);

		assertThat(position.castlingRights & WHITE_KINGSIDE).isEqualTo(NOCASTLING);

		position.undoMove(move);

		assertThat(Notation.fromPosition(position)).isEqualTo("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
		assertThat(position.zobristKey).isEqualTo(zobristKey);
	}
}
