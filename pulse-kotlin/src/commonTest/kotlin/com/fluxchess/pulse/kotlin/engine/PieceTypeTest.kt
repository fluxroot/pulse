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

class PieceTypeTest {
	@Test
	fun `PieceTypes array should be valid`() {
		for (pieceType in pieceTypes.indices) {
			assertEquals(pieceTypes[pieceType], pieceType)
		}
	}

	@Test
	fun `isValidPieceType should return true if pieceType is valid false otherwise`() {
		assertTrue(isValidPieceType(PAWN))
		assertTrue(isValidPieceType(KNIGHT))
		assertTrue(isValidPieceType(BISHOP))
		assertTrue(isValidPieceType(ROOK))
		assertTrue(isValidPieceType(QUEEN))
		assertTrue(isValidPieceType(KING))

		assertFalse(isValidPieceType(NO_PIECE_TYPE))
	}

	@Test
	fun `isSliding should return true when the pieceType is sliding`() {
		assertTrue(isSliding(BISHOP))
		assertTrue(isSliding(ROOK))
		assertTrue(isSliding(QUEEN))
		assertFalse(isSliding(PAWN))
		assertFalse(isSliding(KNIGHT))
		assertFalse(isSliding(KING))
	}

	@Test
	fun `pieceTypeValueOf should return the piece type value`() {
		assertEquals(PAWN_VALUE, pieceTypeValueOf(PAWN))
		assertEquals(KNIGHT_VALUE, pieceTypeValueOf(KNIGHT))
		assertEquals(BISHOP_VALUE, pieceTypeValueOf(BISHOP))
		assertEquals(ROOK_VALUE, pieceTypeValueOf(ROOK))
		assertEquals(QUEEN_VALUE, pieceTypeValueOf(QUEEN))
		assertEquals(KING_VALUE, pieceTypeValueOf(KING))
	}
}
