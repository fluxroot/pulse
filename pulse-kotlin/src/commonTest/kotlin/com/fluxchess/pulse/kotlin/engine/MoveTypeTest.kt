/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

import kotlin.test.Test
import kotlin.test.assertEquals

class MoveTypeTest {
	@Test
	fun `MoveTypes array should be valid`() {
		for (moveType in moveTypes.indices) {
			assertEquals(moveTypes[moveType], moveType)
		}
	}
}
