/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SquareTest {
	@Test
	fun `squares array should be valid`() {
		for (rank in ranks.indices) {
			for (file in files.indices) {
				assertEquals(squares[rank * ranks.size + file], squareOf(file, rank))
				assertTrue(isValidSquare(squareOf(file, rank)))
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
