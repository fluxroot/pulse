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

class FileTest {
	@Test
	fun `files array should be valid`() {
		for (file in files.indices) {
			assertEquals(files[file], file)
		}
	}

	@Test
	fun `isValidFile should return true if file is valid false otherwise`() {
		assertTrue(isValidFile(FILE_A))
		assertTrue(isValidFile(FILE_B))
		assertTrue(isValidFile(FILE_C))
		assertTrue(isValidFile(FILE_D))
		assertTrue(isValidFile(FILE_E))
		assertTrue(isValidFile(FILE_F))
		assertTrue(isValidFile(FILE_G))
		assertTrue(isValidFile(FILE_H))

		assertFalse(isValidFile(NO_FILE))
	}
}
