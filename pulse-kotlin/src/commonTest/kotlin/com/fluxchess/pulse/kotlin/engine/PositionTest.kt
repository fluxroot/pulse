/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

import kotlin.test.Test
import kotlin.test.assertEquals

class PositionTest {
	@Test
	fun `Moving a white piece should set the active color to black`() {
		val position = Position()
		position.activeColor = WHITE
		position.put(WHITE_KING, E1)
		position.put(BLACK_KING, E8)
		val move = moveOf(NORMAL_MOVE, E1, E2, WHITE_KING, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(BLACK, position.activeColor)

		position.undoMove(move)

		assertEquals(WHITE, position.activeColor)
	}

	@Test
	fun `Moving a black piece should set the active color to white`() {
		val position = Position()
		position.activeColor = BLACK
		position.put(WHITE_KING, E1)
		position.put(BLACK_KING, E8)
		val move = moveOf(NORMAL_MOVE, E8, E7, BLACK_KING, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(WHITE, position.activeColor)

		position.undoMove(move)

		assertEquals(BLACK, position.activeColor)
	}

	@Test
	fun `Moving white's kingside rook should remove white's kingside castling right`() {
		val position = Position()
		position.activeColor = WHITE
		position.castlingRights = WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE
		position.put(WHITE_KING, E1)
		position.put(WHITE_ROOK, A1)
		position.put(WHITE_ROOK, H1)
		position.put(BLACK_KING, E8)
		position.put(BLACK_ROOK, A8)
		position.put(BLACK_ROOK, H8)
		val move = moveOf(NORMAL_MOVE, H1, H2, WHITE_ROOK, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE, position.castlingRights)

		position.undoMove(move)

		assertEquals(WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE, position.castlingRights)
	}

	@Test
	fun `Moving white's queenside rook should remove white's queenside castling right`() {
		val position = Position()
		position.activeColor = WHITE
		position.castlingRights = WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE
		position.put(WHITE_KING, E1)
		position.put(WHITE_ROOK, A1)
		position.put(WHITE_ROOK, H1)
		position.put(BLACK_KING, E8)
		position.put(BLACK_ROOK, A8)
		position.put(BLACK_ROOK, H8)
		val move = moveOf(NORMAL_MOVE, A1, A2, WHITE_ROOK, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(WHITE_KINGSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE, position.castlingRights)

		position.undoMove(move)

		assertEquals(WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE, position.castlingRights)
	}

	@Test
	fun `Moving white's king should remove white's kingside and queenside castling right`() {
		val position = Position()
		position.activeColor = WHITE
		position.castlingRights = WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE
		position.put(WHITE_KING, E1)
		position.put(WHITE_ROOK, A1)
		position.put(WHITE_ROOK, H1)
		position.put(BLACK_KING, E8)
		position.put(BLACK_ROOK, A8)
		position.put(BLACK_ROOK, H8)
		val move = moveOf(NORMAL_MOVE, E1, E2, WHITE_KING, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(BLACK_KINGSIDE or BLACK_QUEENSIDE, position.castlingRights)

		position.undoMove(move)

		assertEquals(WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE, position.castlingRights)
	}

	@Test
	fun `Moving black's kingside rook should remove blacks's kingside castling right`() {
		val position = Position()
		position.activeColor = BLACK
		position.castlingRights = WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE
		position.put(WHITE_KING, E1)
		position.put(WHITE_ROOK, A1)
		position.put(WHITE_ROOK, H1)
		position.put(BLACK_KING, E8)
		position.put(BLACK_ROOK, A8)
		position.put(BLACK_ROOK, H8)
		val move = moveOf(NORMAL_MOVE, H8, H7, BLACK_ROOK, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_QUEENSIDE, position.castlingRights)

		position.undoMove(move)

		assertEquals(WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE, position.castlingRights)
	}

	@Test
	fun `Moving black's queenside rook should remove blacks's queenside castling right`() {
		val position = Position()
		position.activeColor = BLACK
		position.castlingRights = WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE
		position.put(WHITE_KING, E1)
		position.put(WHITE_ROOK, A1)
		position.put(WHITE_ROOK, H1)
		position.put(BLACK_KING, E8)
		position.put(BLACK_ROOK, A8)
		position.put(BLACK_ROOK, H8)
		val move = moveOf(NORMAL_MOVE, A8, A7, BLACK_ROOK, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE, position.castlingRights)

		position.undoMove(move)

		assertEquals(WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE, position.castlingRights)
	}

	@Test
	fun `Moving black's king should remove blacks's kingside and queenside castling right`() {
		val position = Position()
		position.activeColor = BLACK
		position.castlingRights = WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE
		position.put(WHITE_KING, E1)
		position.put(WHITE_ROOK, A1)
		position.put(WHITE_ROOK, H1)
		position.put(BLACK_KING, E8)
		position.put(BLACK_ROOK, A8)
		position.put(BLACK_ROOK, H8)
		val move = moveOf(NORMAL_MOVE, E8, E7, BLACK_KING, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(WHITE_KINGSIDE or WHITE_QUEENSIDE, position.castlingRights)

		position.undoMove(move)

		assertEquals(WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE, position.castlingRights)
	}

	@Test
	fun `Making a pawn double move for white should set the en passant square`() {
		val position = Position()
		position.activeColor = WHITE
		position.put(WHITE_KING, E1)
		position.put(WHITE_PAWN, E2)
		position.put(BLACK_KING, E8)
		val move = moveOf(PAWN_DOUBLE_MOVE, E2, E4, WHITE_PAWN, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(E3, position.enPassantSquare)

		position.undoMove(move)

		assertEquals(NO_SQUARE, position.enPassantSquare)
	}

	@Test
	fun `Making a pawn double move for black should set the en passant square`() {
		val position = Position()
		position.activeColor = BLACK
		position.put(WHITE_KING, E1)
		position.put(BLACK_KING, E8)
		position.put(BLACK_PAWN, E7)
		val move = moveOf(PAWN_DOUBLE_MOVE, E7, E5, BLACK_PAWN, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(E6, position.enPassantSquare)

		position.undoMove(move)

		assertEquals(NO_SQUARE, position.enPassantSquare)
	}

	@Test
	fun `Making an en passant move for white should clear the en passant square`() {
		val position = Position()
		position.activeColor = WHITE
		position.enPassantSquare = E6
		position.put(WHITE_KING, E1)
		position.put(WHITE_PAWN, D5)
		position.put(BLACK_KING, E8)
		position.put(BLACK_PAWN, E5)
		val move = moveOf(EN_PASSANT_MOVE, D5, E6, WHITE_PAWN, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(NO_SQUARE, position.enPassantSquare)

		position.undoMove(move)

		assertEquals(E6, position.enPassantSquare)
	}

	@Test
	fun `Making an en passant move for black should clear the en passant square`() {
		val position = Position()
		position.activeColor = BLACK
		position.enPassantSquare = E3
		position.put(WHITE_KING, E1)
		position.put(WHITE_PAWN, E4)
		position.put(BLACK_KING, E8)
		position.put(BLACK_PAWN, D4)
		val move = moveOf(EN_PASSANT_MOVE, D4, E3, BLACK_PAWN, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(NO_SQUARE, position.enPassantSquare)

		position.undoMove(move)

		assertEquals(E3, position.enPassantSquare)
	}

	@Test
	fun `Making a move should increment the halfmove clock`() {
		val position = Position()
		position.activeColor = WHITE
		position.halfmoveClock = 0
		position.put(WHITE_KING, E1)
		position.put(BLACK_KING, E8)
		val move = moveOf(NORMAL_MOVE, E1, E2, WHITE_KING, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(1, position.halfmoveClock)

		position.undoMove(move)

		assertEquals(0, position.halfmoveClock)
	}

	@Test
	fun `Moving a pawn should reset the halfmove clock`() {
		val position = Position()
		position.activeColor = WHITE
		position.halfmoveClock = 1
		position.put(WHITE_KING, E1)
		position.put(WHITE_PAWN, E2)
		position.put(BLACK_KING, E8)
		val move = moveOf(NORMAL_MOVE, E2, E3, WHITE_PAWN, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(0, position.halfmoveClock)

		position.undoMove(move)

		assertEquals(1, position.halfmoveClock)
	}

	@Test
	fun `Capturing a piece should reset the halfmove clock`() {
		val position = Position()
		position.activeColor = WHITE
		position.halfmoveClock = 1
		position.put(WHITE_KING, E1)
		position.put(WHITE_QUEEN, D1)
		position.put(BLACK_KING, E8)
		position.put(BLACK_PAWN, D7)
		val move = moveOf(NORMAL_MOVE, D1, D7, WHITE_QUEEN, BLACK_PAWN, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(0, position.halfmoveClock)

		position.undoMove(move)

		assertEquals(1, position.halfmoveClock)
	}

	@Test
	fun `Making a move should increment the halfmove number`() {
		val position = Position()
		position.activeColor = WHITE
		position.halfmoveNumber = 0
		position.put(WHITE_KING, E1)
		position.put(BLACK_KING, E8)
		val move = moveOf(NORMAL_MOVE, E1, E2, WHITE_KING, NO_PIECE, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(1, position.halfmoveNumber)

		position.undoMove(move)

		assertEquals(0, position.halfmoveNumber)
	}

	@Test
	fun `Capturing a piece should replace the target piece by the origin piece`() {
		val position = Position()
		position.activeColor = WHITE
		position.put(WHITE_KING, E1)
		position.put(WHITE_QUEEN, D1)
		position.put(BLACK_KING, E8)
		position.put(BLACK_PAWN, D7)
		val move = moveOf(NORMAL_MOVE, D1, D7, WHITE_QUEEN, BLACK_PAWN, NO_PIECE_TYPE)

		position.makeMove(move)

		assertEquals(NO_PIECE, position.board[D1])
		assertEquals(WHITE_QUEEN, position.board[D7])

		position.undoMove(move)

		assertEquals(WHITE_QUEEN, position.board[D1])
		assertEquals(BLACK_PAWN, position.board[D7])
	}

	@Test
	fun `Making a pawn promotion move for white should replace the pawn by the promotion`() {
		val position = Position()
		position.activeColor = WHITE
		position.put(WHITE_KING, E1)
		position.put(WHITE_PAWN, C7)
		position.put(BLACK_KING, E8)
		val move = moveOf(PAWN_PROMOTION_MOVE, C7, C8, WHITE_PAWN, NO_PIECE, QUEEN)

		position.makeMove(move)

		assertEquals(NO_PIECE, position.board[C7])
		assertEquals(WHITE_QUEEN, position.board[C8])

		position.undoMove(move)

		assertEquals(WHITE_PAWN, position.board[C7])
		assertEquals(NO_PIECE, position.board[C8])
	}

	@Test
	fun `Making a pawn promotion move for black should replace the pawn by the promotion`() {
		val position = Position()
		position.activeColor = BLACK
		position.put(WHITE_KING, E1)
		position.put(BLACK_KING, E8)
		position.put(BLACK_PAWN, C2)
		val move = moveOf(PAWN_PROMOTION_MOVE, C2, C1, BLACK_PAWN, NO_PIECE, QUEEN)

		position.makeMove(move)

		assertEquals(NO_PIECE, position.board[C2])
		assertEquals(BLACK_QUEEN, position.board[C1])

		position.undoMove(move)

		assertEquals(BLACK_PAWN, position.board[C2])
		assertEquals(NO_PIECE, position.board[C1])
	}
}
