/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package kotlin.com.fluxchess.pulse.kotlin

import com.fluxchess.pulse.kotlin.ranks
import kotlin.test.Test
import kotlin.test.assertEquals

class RankTest {
	@Test
	fun `Ranks array should be valid`() {
		for (rank in ranks.indices) {
			assertEquals(ranks[rank], rank)
		}
	}
}
