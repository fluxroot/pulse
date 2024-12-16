/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package kotlin.com.fluxchess.pulse.kotlin

import com.fluxchess.pulse.kotlin.castlingTypes
import kotlin.test.Test
import kotlin.test.assertEquals

class CastlingTypeTest {
	@Test
	fun `CastlingTypes array should be valid`() {
		for (castlingType in castlingTypes.indices) {
			assertEquals(castlingTypes[castlingType], castlingType)
		}
	}
}
