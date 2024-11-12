/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.uci

interface Writer {
	fun writeln(s: String)
}

class StandardOutputWriter : Writer {
	override fun writeln(s: String) {
		println(s)
	}
}
