/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package kotlin.com.fluxchess.pulse.kotlin

import com.fluxchess.pulse.kotlin.fileOf
import com.fluxchess.pulse.kotlin.files
import com.fluxchess.pulse.kotlin.rankOf
import com.fluxchess.pulse.kotlin.ranks
import com.fluxchess.pulse.kotlin.squareOf
import com.fluxchess.pulse.kotlin.squares
import kotlin.test.Test
import kotlin.test.assertEquals

class SquareTest {
	@Test
	fun `Square array should be valid`() {
		for (rank in ranks.indices) {
			for (file in files.indices) {
				assertEquals(squares[rank * ranks.size + file], squareOf(file, rank))
			}
		}
	}

	@Test
	fun `When creating a square it should save and return file and rank correctly`() {
		ranks.forEach { rank ->
			files.forEach { file ->
				val square = squareOf(file, rank)
				assertEquals(file, fileOf(square))
				assertEquals(rank, rankOf(square))
			}
		}
	}
}
