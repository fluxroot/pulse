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

class RankTest {
	@Test
	fun `ranks array should be valid`() {
		for (rank in ranks.indices) {
			assertEquals(ranks[rank], rank)
		}
	}

	@Test
	fun `isValidRank should return true if rank is valid false otherwise`() {
		assertTrue(isValidRank(RANK_1))
		assertTrue(isValidRank(RANK_2))
		assertTrue(isValidRank(RANK_3))
		assertTrue(isValidRank(RANK_4))
		assertTrue(isValidRank(RANK_5))
		assertTrue(isValidRank(RANK_6))
		assertTrue(isValidRank(RANK_7))
		assertTrue(isValidRank(RANK_8))

		assertFalse(isValidRank(NO_RANK))
	}
}
