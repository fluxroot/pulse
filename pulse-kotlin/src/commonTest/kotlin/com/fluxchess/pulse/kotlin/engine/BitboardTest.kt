/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

import kotlin.test.Test
import kotlin.test.assertEquals

class BitboardTest {
	@Test
	fun `It should add all squares`() {
		var bitboard: Bitboard = 0uL
		shuffledSquares().forEach {
			bitboard = addSquare(it, bitboard)
		}
		assertEquals(ULong.MAX_VALUE, bitboard)
	}

	@Test
	fun `It should remove all squares`() {
		var bitboard: Bitboard = ULong.MAX_VALUE
		shuffledSquares().forEach {
			bitboard = removeSquare(it, bitboard)
		}
		assertEquals(0uL, bitboard)
	}

	@Test
	fun `It should return the next square`() {
		val bitboard: Bitboard = addSquare(A6, 0uL)
		val square = next(bitboard)
		assertEquals(A6, square)
	}

	@Test
	fun `It should return the remainder`() {
		val bitboard: Bitboard = 0b1110100uL
		val remainder = remainder(bitboard)
		assertEquals(0b1110000uL, remainder)
	}
}

private fun shuffledSquares(): List<Square> {
	return squares.indices.shuffled().map { squares[it] }
}
