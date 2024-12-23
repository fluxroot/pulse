/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

import kotlin.test.Test
import kotlin.test.assertEquals

class MoveListTest {

	@Test
	fun `add should add a move to the move list`() {
		val moveList = MoveList()
		val move = moveOf(PAWN_PROMOTION_MOVE, A7, B8, WHITE_PAWN, BLACK_QUEEN, KNIGHT)

		moveList.add(move)

		assertEquals(1, moveList.size)
		assertEquals(move, moveList.entries[0].move)
	}

	@Test
	fun `rateByMVVLVA should rate a non-capturing move`() {
		val moveList = MoveList()
		moveList.add(moveOf(NORMAL_MOVE, D4, D5, WHITE_PAWN, NO_PIECE, NO_PIECE_TYPE))

		moveList.rateByMVVLVA()

		assertEquals(KING_VALUE / PAWN_VALUE, moveList.entries[0].value)
	}

	@Test
	fun `rateByMVVLVA should rate a capturing move`() {
		val moveList = MoveList()
		moveList.add(moveOf(NORMAL_MOVE, D1, G4, WHITE_QUEEN, BLACK_KNIGHT, NO_PIECE_TYPE))

		moveList.rateByMVVLVA()

		assertEquals(KING_VALUE / QUEEN_VALUE + 10 * KNIGHT_VALUE, moveList.entries[0].value)
	}

	@Test
	fun `sort should sort all moves`() {
		val moveList = MoveList()
		val move1 = moveOf(NORMAL_MOVE, D1, G4, WHITE_QUEEN, BLACK_KNIGHT, NO_PIECE_TYPE)
		val move2 = moveOf(NORMAL_MOVE, C1, G5, WHITE_BISHOP, BLACK_PAWN, NO_PIECE_TYPE)
		val move3 = moveOf(NORMAL_MOVE, F1, B5, WHITE_BISHOP, NO_PIECE, NO_PIECE_TYPE)
		val move4 = moveOf(NORMAL_MOVE, D4, D5, WHITE_PAWN, NO_PIECE, NO_PIECE_TYPE)
		moveList.add(move4)
		moveList.entries[moveList.size - 1].value = 1
		moveList.add(move3)
		moveList.entries[moveList.size - 1].value = 2
		moveList.add(move2)
		moveList.entries[moveList.size - 1].value = 3
		moveList.add(move1)
		moveList.entries[moveList.size - 1].value = 4

		moveList.sort()

		assertEquals(move1, moveList.entries[0].move)
		assertEquals(move2, moveList.entries[1].move)
		assertEquals(move3, moveList.entries[2].move)
		assertEquals(move4, moveList.entries[3].move)
	}
}
