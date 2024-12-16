/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin

typealias Castling = Int

const val WHITE_KINGSIDE: Castling = 1 // 1 shl 0
const val WHITE_QUEENSIDE: Castling = 1 shl 1
const val BLACK_KINGSIDE: Castling = 1 shl 2
const val BLACK_QUEENSIDE: Castling = 1 shl 3

const val NO_CASTLING: Castling = 0

val castlings = intArrayOf(
	WHITE_KINGSIDE, WHITE_QUEENSIDE,
	BLACK_KINGSIDE, BLACK_QUEENSIDE
)
