/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PieceTest {
	@Test
	fun `Pieces array should be valid`() {
		for (piece in pieces.indices) {
			assertEquals(pieces[piece], piece)
		}
	}

	@Test
	fun `isValidPiece should return true if piece is valid false otherwise`() {
		assertTrue(isValidPiece(WHITE_PAWN))
		assertTrue(isValidPiece(WHITE_KNIGHT))
		assertTrue(isValidPiece(WHITE_BISHOP))
		assertTrue(isValidPiece(WHITE_ROOK))
		assertTrue(isValidPiece(WHITE_QUEEN))
		assertTrue(isValidPiece(WHITE_KING))
		assertTrue(isValidPiece(BLACK_PAWN))
		assertTrue(isValidPiece(BLACK_KNIGHT))
		assertTrue(isValidPiece(BLACK_BISHOP))
		assertTrue(isValidPiece(BLACK_ROOK))
		assertTrue(isValidPiece(BLACK_QUEEN))
		assertTrue(isValidPiece(BLACK_KING))

		assertFalse(isValidPiece(NO_PIECE))
	}

	@Test
	fun `pieceOf should return the correct piece`() {
		assertEquals(WHITE_PAWN, pieceOf(WHITE, PAWN))
		assertEquals(WHITE_KNIGHT, pieceOf(WHITE, KNIGHT))
		assertEquals(WHITE_BISHOP, pieceOf(WHITE, BISHOP))
		assertEquals(WHITE_ROOK, pieceOf(WHITE, ROOK))
		assertEquals(WHITE_QUEEN, pieceOf(WHITE, QUEEN))
		assertEquals(WHITE_KING, pieceOf(WHITE, KING))
		assertEquals(BLACK_PAWN, pieceOf(BLACK, PAWN))
		assertEquals(BLACK_KNIGHT, pieceOf(BLACK, KNIGHT))
		assertEquals(BLACK_BISHOP, pieceOf(BLACK, BISHOP))
		assertEquals(BLACK_ROOK, pieceOf(BLACK, ROOK))
		assertEquals(BLACK_QUEEN, pieceOf(BLACK, QUEEN))
		assertEquals(BLACK_KING, pieceOf(BLACK, KING))
	}

	@Test
	fun `pieceColorOf should return the piece color`() {
		assertEquals(WHITE, pieceColorOf(WHITE_PAWN))
		assertEquals(WHITE, pieceColorOf(WHITE_KNIGHT))
		assertEquals(WHITE, pieceColorOf(WHITE_BISHOP))
		assertEquals(WHITE, pieceColorOf(WHITE_ROOK))
		assertEquals(WHITE, pieceColorOf(WHITE_QUEEN))
		assertEquals(WHITE, pieceColorOf(WHITE_KING))
		assertEquals(BLACK, pieceColorOf(BLACK_PAWN))
		assertEquals(BLACK, pieceColorOf(BLACK_KNIGHT))
		assertEquals(BLACK, pieceColorOf(BLACK_BISHOP))
		assertEquals(BLACK, pieceColorOf(BLACK_ROOK))
		assertEquals(BLACK, pieceColorOf(BLACK_QUEEN))
		assertEquals(BLACK, pieceColorOf(BLACK_KING))
	}

	@Test
	fun `pieceTypeOf should return the piece type`() {
		assertEquals(PAWN, pieceTypeOf(WHITE_PAWN))
		assertEquals(KNIGHT, pieceTypeOf(WHITE_KNIGHT))
		assertEquals(BISHOP, pieceTypeOf(WHITE_BISHOP))
		assertEquals(ROOK, pieceTypeOf(WHITE_ROOK))
		assertEquals(QUEEN, pieceTypeOf(WHITE_QUEEN))
		assertEquals(KING, pieceTypeOf(WHITE_KING))
		assertEquals(PAWN, pieceTypeOf(BLACK_PAWN))
		assertEquals(KNIGHT, pieceTypeOf(BLACK_KNIGHT))
		assertEquals(BISHOP, pieceTypeOf(BLACK_BISHOP))
		assertEquals(ROOK, pieceTypeOf(BLACK_ROOK))
		assertEquals(QUEEN, pieceTypeOf(BLACK_QUEEN))
		assertEquals(KING, pieceTypeOf(BLACK_KING))
	}
}
