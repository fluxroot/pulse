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

class CastlingTypeTest {
	@Test
	fun `castlingTypes array should be valid`() {
		for (castlingType in castlingTypes.indices) {
			assertEquals(castlingTypes[castlingType], castlingType)
		}
	}

	@Test
	fun `isValidCastlingType should return true if castlingType is valid false otherwise`() {
		assertTrue(isValidCastlingType(KINGSIDE))
		assertTrue(isValidCastlingType(QUEENSIDE))

		assertFalse(isValidCastlingType(NO_CASTLING_TYPE))
	}
}
