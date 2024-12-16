/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package kotlin.com.fluxchess.pulse.kotlin

import com.fluxchess.pulse.kotlin.castlings
import kotlin.test.Test
import kotlin.test.assertEquals

class CastlingTest {
	@Test
	fun `Castlings array should be valid`() {
		for (castling in castlings.indices) {
			assertEquals(castlings[castling], castling)
		}
	}
}
