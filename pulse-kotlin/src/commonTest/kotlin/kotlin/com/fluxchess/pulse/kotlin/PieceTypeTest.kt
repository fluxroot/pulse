/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package kotlin.com.fluxchess.pulse.kotlin

import com.fluxchess.pulse.kotlin.pieceTypes
import kotlin.test.Test
import kotlin.test.assertEquals

class PieceTypeTest {
	@Test
	fun `PieceTypes array should be valid`() {
		for (pieceType in pieceTypes.indices) {
			assertEquals(pieceTypes[pieceType], pieceType)
		}
	}
}
