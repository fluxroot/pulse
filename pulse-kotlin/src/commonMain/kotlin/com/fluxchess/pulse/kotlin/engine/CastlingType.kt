/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

typealias CastlingType = Int

const val KINGSIDE: CastlingType = 0
const val QUEENSIDE: CastlingType = 1

const val NO_CASTLING_TYPE: CastlingType = 2

val castlingTypes = intArrayOf(KINGSIDE, QUEENSIDE)

fun isValidCastlingType(castlingType: CastlingType): Boolean = when (castlingType) {
	KINGSIDE, QUEENSIDE -> true
	else -> false
}
