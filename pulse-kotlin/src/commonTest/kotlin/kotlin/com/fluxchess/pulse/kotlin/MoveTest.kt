/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package kotlin.com.fluxchess.pulse.kotlin

import com.fluxchess.pulse.kotlin.A7
import com.fluxchess.pulse.kotlin.B8
import com.fluxchess.pulse.kotlin.BLACK_QUEEN
import com.fluxchess.pulse.kotlin.KNIGHT
import com.fluxchess.pulse.kotlin.PAWN_PROMOTION_MOVE
import com.fluxchess.pulse.kotlin.WHITE_PAWN
import com.fluxchess.pulse.kotlin.moveOf
import com.fluxchess.pulse.kotlin.moveTypeOf
import com.fluxchess.pulse.kotlin.originPieceOf
import com.fluxchess.pulse.kotlin.originSquareOf
import com.fluxchess.pulse.kotlin.promotionPieceOf
import com.fluxchess.pulse.kotlin.targetPieceOf
import com.fluxchess.pulse.kotlin.targetSquareOf
import kotlin.test.Test
import kotlin.test.assertEquals

class MoveTest {
	@Test
	fun `Move should be correctly created`() {
		val move = moveOf(PAWN_PROMOTION_MOVE, A7, B8, WHITE_PAWN, BLACK_QUEEN, KNIGHT)

		assertEquals(PAWN_PROMOTION_MOVE, moveTypeOf(move))
		assertEquals(A7, originSquareOf(move))
		assertEquals(B8, targetSquareOf(move))
		assertEquals(WHITE_PAWN, originPieceOf(move))
		assertEquals(BLACK_QUEEN, targetPieceOf(move))
		assertEquals(KNIGHT, promotionPieceOf(move))
	}
}
