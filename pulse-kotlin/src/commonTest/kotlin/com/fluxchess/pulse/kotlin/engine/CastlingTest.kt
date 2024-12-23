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

class CastlingTest {
	@Test
	fun `isValidCastling should return true if castling is valid false otherwise`() {
		assertTrue(isValidCastling(WHITE_KINGSIDE))
		assertTrue(isValidCastling(WHITE_QUEENSIDE))
		assertTrue(isValidCastling(BLACK_KINGSIDE))
		assertTrue(isValidCastling(BLACK_QUEENSIDE))

		assertFalse(isValidCastling(NO_CASTLING))
	}

	@Test
	fun `castlingOf should return the castling`() {
		assertEquals(WHITE_KINGSIDE, castlingOf(WHITE, KINGSIDE))
		assertEquals(WHITE_QUEENSIDE, castlingOf(WHITE, QUEENSIDE))
		assertEquals(BLACK_KINGSIDE, castlingOf(BLACK, KINGSIDE))
		assertEquals(BLACK_QUEENSIDE, castlingOf(BLACK, QUEENSIDE))
	}

	@Test
	fun `castlingColorOf should return the castling color`() {
		assertEquals(WHITE, castlingColorOf(WHITE_KINGSIDE))
		assertEquals(WHITE, castlingColorOf(WHITE_QUEENSIDE))
		assertEquals(BLACK, castlingColorOf(BLACK_KINGSIDE))
		assertEquals(BLACK, castlingColorOf(BLACK_QUEENSIDE))
	}

	@Test
	fun `castlingTypeOf should return the castling type`() {
		assertEquals(KINGSIDE, castlingTypeOf(WHITE_KINGSIDE))
		assertEquals(QUEENSIDE, castlingTypeOf(WHITE_QUEENSIDE))
		assertEquals(KINGSIDE, castlingTypeOf(BLACK_KINGSIDE))
		assertEquals(QUEENSIDE, castlingTypeOf(BLACK_QUEENSIDE))
	}
}
