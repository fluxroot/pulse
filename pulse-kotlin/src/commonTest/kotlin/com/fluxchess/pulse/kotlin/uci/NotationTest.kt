/*
 * Copyright 2013-2025 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.uci

import com.fluxchess.pulse.kotlin.engine.A1
import com.fluxchess.pulse.kotlin.engine.A7
import com.fluxchess.pulse.kotlin.engine.A8
import com.fluxchess.pulse.kotlin.engine.B1
import com.fluxchess.pulse.kotlin.engine.B2
import com.fluxchess.pulse.kotlin.engine.B8
import com.fluxchess.pulse.kotlin.engine.BLACK
import com.fluxchess.pulse.kotlin.engine.BLACK_BISHOP
import com.fluxchess.pulse.kotlin.engine.BLACK_KING
import com.fluxchess.pulse.kotlin.engine.BLACK_KINGSIDE
import com.fluxchess.pulse.kotlin.engine.BLACK_KNIGHT
import com.fluxchess.pulse.kotlin.engine.BLACK_PAWN
import com.fluxchess.pulse.kotlin.engine.BLACK_QUEEN
import com.fluxchess.pulse.kotlin.engine.BLACK_QUEENSIDE
import com.fluxchess.pulse.kotlin.engine.BLACK_ROOK
import com.fluxchess.pulse.kotlin.engine.C1
import com.fluxchess.pulse.kotlin.engine.C8
import com.fluxchess.pulse.kotlin.engine.D1
import com.fluxchess.pulse.kotlin.engine.D8
import com.fluxchess.pulse.kotlin.engine.E1
import com.fluxchess.pulse.kotlin.engine.E6
import com.fluxchess.pulse.kotlin.engine.E8
import com.fluxchess.pulse.kotlin.engine.F1
import com.fluxchess.pulse.kotlin.engine.F8
import com.fluxchess.pulse.kotlin.engine.G1
import com.fluxchess.pulse.kotlin.engine.G8
import com.fluxchess.pulse.kotlin.engine.H1
import com.fluxchess.pulse.kotlin.engine.H8
import com.fluxchess.pulse.kotlin.engine.NORMAL_MOVE
import com.fluxchess.pulse.kotlin.engine.NO_CASTLING
import com.fluxchess.pulse.kotlin.engine.NO_PIECE
import com.fluxchess.pulse.kotlin.engine.NO_PIECE_TYPE
import com.fluxchess.pulse.kotlin.engine.PAWN_PROMOTION_MOVE
import com.fluxchess.pulse.kotlin.engine.Position
import com.fluxchess.pulse.kotlin.engine.QUEEN
import com.fluxchess.pulse.kotlin.engine.RANK_2
import com.fluxchess.pulse.kotlin.engine.RANK_7
import com.fluxchess.pulse.kotlin.engine.WHITE
import com.fluxchess.pulse.kotlin.engine.WHITE_BISHOP
import com.fluxchess.pulse.kotlin.engine.WHITE_KING
import com.fluxchess.pulse.kotlin.engine.WHITE_KINGSIDE
import com.fluxchess.pulse.kotlin.engine.WHITE_KNIGHT
import com.fluxchess.pulse.kotlin.engine.WHITE_PAWN
import com.fluxchess.pulse.kotlin.engine.WHITE_QUEEN
import com.fluxchess.pulse.kotlin.engine.WHITE_QUEENSIDE
import com.fluxchess.pulse.kotlin.engine.WHITE_ROOK
import com.fluxchess.pulse.kotlin.engine.files
import com.fluxchess.pulse.kotlin.engine.moveOf
import com.fluxchess.pulse.kotlin.engine.squareOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FenToPositionTest {
	@Test
	fun `Valid FEN should return a valid position`() {
		val position = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1".toPosition()
		assertEquals(startingPosition(), position)
	}

	@Test
	fun `Valid FEN without halfmove clock and fullmove number should return a valid position`() {
		val position = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -".toPosition()
		assertEquals(startingPosition(), position)
	}

	@Test
	fun `Invalid FEN should return an error`() {
		val exception = assertFailsWith<FenException> { "invalid FEN".toPosition() }
		assertEquals("Invalid FEN: invalid FEN", exception.message)
	}

	@Test
	fun `Invalid ranks should return an error`() {
		val exception = assertFailsWith<FenException> { "invalid-ranks w KQkq - 0 1".toPosition() }
		assertEquals("Invalid board: invalid-ranks", exception.message)
	}

	@Test
	fun `Invalid rank with too many squares should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbnr1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1".toPosition() }
		assertEquals("Invalid rank: rnbqkbnr1", exception.message)
	}

	@Test
	fun `Invalid rank with too few squares should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbn/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1".toPosition() }
		assertEquals("Invalid rank: rnbqkbn", exception.message)
	}

	@Test
	fun `Invalid piece type should return an error`() {
		val exception = assertFailsWith<FenException> { "xnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1".toPosition() }
		assertEquals("Invalid rank: xnbqkbnr", exception.message)
	}

	@Test
	fun `Invalid number of empty squares should return an error`() {
		val exception = assertFailsWith<FenException> { "9/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1".toPosition() }
		assertEquals("Invalid rank: 9", exception.message)
	}

	@Test
	fun `Invalid active color should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR x KQkq - 0 1".toPosition() }
		assertEquals("Invalid active color: x", exception.message)
	}

	@Test
	fun `No castling rights should return a valid position`() {
		val position = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1".toPosition()
		val startingPosition = startingPosition()
		startingPosition.castlingRights = NO_CASTLING
		assertEquals(startingPosition, position)
	}

	@Test
	fun `Invalid castling rights with too many castlings should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkqK - 0 1".toPosition() }
		assertEquals("Invalid castling rights: KQkqK", exception.message)
	}

	@Test
	fun `Invalid castling rights should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w XQkq - 0 1".toPosition() }
		assertEquals("Invalid castling type: X", exception.message)
	}

	@Test
	fun `Valid en passant square should return a valid position`() {
		val position = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e6 0 1".toPosition()
		val startingPosition = startingPosition()
		startingPosition.enPassantSquare = E6
		assertEquals(startingPosition, position)
	}

	@Test
	fun `Invalid en passant square length should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e31 0 1".toPosition() }
		assertEquals("Invalid en passant square: e31", exception.message)
	}

	@Test
	fun `Invalid en passant square with non-existing file should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq x3 0 1".toPosition() }
		assertEquals("Invalid file: x", exception.message)
	}

	@Test
	fun `Invalid en passant square with non-existing rank should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e9 0 1".toPosition() }
		assertEquals("Invalid rank: 9", exception.message)
	}

	@Test
	fun `Invalid en passant square should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e2 0 1".toPosition() }
		assertEquals("Invalid en passant square: e2", exception.message)
	}

	@Test
	fun `Invalid halfmove clock should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - x 1".toPosition() }
		assertEquals("Invalid halfmove clock: x", exception.message)
	}

	@Test
	fun `Valid fullmove number should return a valid position`() {
		val position = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1".toPosition()
		val startingPosition = startingPosition()
		startingPosition.activeColor = BLACK
		startingPosition.halfmoveNumber = 3
		assertEquals(startingPosition, position)
	}

	@Test
	fun `Invalid fullmove number should return an error`() {
		val exception = assertFailsWith<FenException> { "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 x".toPosition() }
		assertEquals("Invalid fullmove number: x", exception.message)
	}
}

class PositionToFenTest {
	@Test
	fun `Starting position should return a valid FEN`() {
		val fen = startingPosition().toFEN()
		assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", fen)
	}

	@Test
	fun `No castling rights should return a valid FEN`() {
		val startingPosition = startingPosition()
		startingPosition.castlingRights = NO_CASTLING
		val fen = startingPosition.toFEN()
		assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1", fen)
	}

	@Test
	fun `Valid en passant square should return a valid FEN`() {
		val startingPosition = startingPosition()
		startingPosition.enPassantSquare = E6
		val fen = startingPosition.toFEN()
		assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e6 0 1", fen)
	}
}

class MoveToNotationTest {
	@Test
	fun `Valid move should return a valid notation`() {
		val move = moveOf(NORMAL_MOVE, A1, B2, WHITE_QUEEN, BLACK_ROOK, NO_PIECE_TYPE)
		assertEquals("a1b2", move.toNotation())
	}

	@Test
	fun `Valid move with promotion should return a valid notation`() {
		val move = moveOf(PAWN_PROMOTION_MOVE, A7, A8, WHITE_PAWN, NO_PIECE, QUEEN)
		assertEquals("a7a8q", move.toNotation())
	}
}

private fun startingPosition(): Position {
	val position = Position()
	position.activeColor = WHITE
	position.castlingRights = WHITE_KINGSIDE or WHITE_QUEENSIDE or BLACK_KINGSIDE or BLACK_QUEENSIDE
	position.halfmoveNumber = 2
	position.put(WHITE_ROOK, A1)
	position.put(WHITE_KNIGHT, B1)
	position.put(WHITE_BISHOP, C1)
	position.put(WHITE_QUEEN, D1)
	position.put(WHITE_KING, E1)
	position.put(WHITE_BISHOP, F1)
	position.put(WHITE_KNIGHT, G1)
	position.put(WHITE_ROOK, H1)
	position.put(BLACK_ROOK, A8)
	position.put(BLACK_KNIGHT, B8)
	position.put(BLACK_BISHOP, C8)
	position.put(BLACK_QUEEN, D8)
	position.put(BLACK_KING, E8)
	position.put(BLACK_BISHOP, F8)
	position.put(BLACK_KNIGHT, G8)
	position.put(BLACK_ROOK, H8)
	for (file in files) {
		position.put(WHITE_PAWN, squareOf(file, RANK_2))
		position.put(BLACK_PAWN, squareOf(file, RANK_7))
	}
	return position
}
