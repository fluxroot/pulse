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

class ColorTest {
	@Test
	fun `Colors array should be valid`() {
		for (color in colors.indices) {
			assertEquals(colors[color], color)
		}
	}

	@Test
	fun `isValidColor should return true if color is valid false otherwise`() {
		assertTrue(isValidColor(WHITE))
		assertTrue(isValidColor(BLACK))

		assertFalse(isValidColor(NO_COLOR))
	}

	@Test
	fun `oppositeOf should return the opposite color`() {
		assertEquals(BLACK, oppositeOf(WHITE))
		assertEquals(WHITE, oppositeOf(BLACK))
	}
}
