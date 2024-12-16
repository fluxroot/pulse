/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package kotlin.com.fluxchess.pulse.kotlin

import com.fluxchess.pulse.kotlin.colors
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {
	@Test
	fun `Colors array should be valid`() {
		for (color in colors.indices) {
			assertEquals(colors[color], color)
		}
	}
}
