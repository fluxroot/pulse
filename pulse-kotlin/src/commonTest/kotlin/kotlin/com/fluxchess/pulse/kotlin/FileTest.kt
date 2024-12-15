/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package kotlin.com.fluxchess.pulse.kotlin

import com.fluxchess.pulse.kotlin.files
import kotlin.test.Test
import kotlin.test.assertEquals

class FileTest {
	@Test
	fun `Files array should be valid`() {
		for (file in files.indices) {
			assertEquals(files[file], file)
		}
	}
}
