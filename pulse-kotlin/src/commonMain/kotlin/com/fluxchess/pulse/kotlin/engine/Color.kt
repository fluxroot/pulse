/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

typealias Color = Int

const val WHITE: Color = 0
const val BLACK: Color = 1

const val NO_COLOR: Color = 2

val colors = intArrayOf(WHITE, BLACK)

fun isValidColor(color: Color) = when (color) {
	WHITE, BLACK -> true
	else -> false
}

fun oppositeOf(color: Color): Color =
	when (color) {
		WHITE -> BLACK
		BLACK -> WHITE
		else -> error("Invalid color: $color")
	}
