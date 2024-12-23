/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

import kotlin.test.Test
import kotlin.test.assertEquals

class MoveTest {
	@Test
	fun `moveOf should return the move`() {
		val move = moveOf(PAWN_PROMOTION_MOVE, A7, B8, WHITE_PAWN, BLACK_QUEEN, KNIGHT)

		assertEquals(PAWN_PROMOTION_MOVE, moveTypeOf(move))
		assertEquals(A7, originSquareOf(move))
		assertEquals(B8, targetSquareOf(move))
		assertEquals(WHITE_PAWN, originPieceOf(move))
		assertEquals(BLACK_QUEEN, targetPieceOf(move))
		assertEquals(KNIGHT, promotionOf(move))
	}
}
