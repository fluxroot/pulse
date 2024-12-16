/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package kotlin.com.fluxchess.pulse.kotlin

import com.fluxchess.pulse.kotlin.pieces
import kotlin.test.Test
import kotlin.test.assertEquals

class PieceTest {
	@Test
	fun `Pieces array should be valid`() {
		for (piece in pieces.indices) {
			assertEquals(pieces[piece], piece)
		}
	}
}
