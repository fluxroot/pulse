/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.uci

interface Reader {
	fun readln(): String?
}

class StandardInputReader : Reader {
	override fun readln(): String? {
		return readlnOrNull()
	}
}
