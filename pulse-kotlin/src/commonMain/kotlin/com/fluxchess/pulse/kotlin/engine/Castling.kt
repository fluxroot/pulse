/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

typealias Castling = Int

const val WHITE_KINGSIDE: Castling = 1 // 1 shl 0
const val WHITE_QUEENSIDE: Castling = 1 shl 1
const val BLACK_KINGSIDE: Castling = 1 shl 2
const val BLACK_QUEENSIDE: Castling = 1 shl 3

const val NO_CASTLING: Castling = 0

val castlings = intArrayOf(
	WHITE_KINGSIDE, WHITE_QUEENSIDE,
	BLACK_KINGSIDE, BLACK_QUEENSIDE,
)

fun isValidCastling(castling: Castling): Boolean = when (castling) {
	WHITE_KINGSIDE, WHITE_QUEENSIDE, BLACK_KINGSIDE, BLACK_QUEENSIDE -> true
	else -> false
}

fun castlingOf(color: Color, castlingType: CastlingType): Castling = when (color) {
	WHITE -> when (castlingType) {
		KINGSIDE -> WHITE_KINGSIDE
		QUEENSIDE -> WHITE_QUEENSIDE
		else -> error("Invalid castling type: $castlingType")
	}

	BLACK -> when (castlingType) {
		KINGSIDE -> BLACK_KINGSIDE
		QUEENSIDE -> BLACK_QUEENSIDE
		else -> error("Invalid castling type: $castlingType")
	}

	else -> error("Invalid color: $color")
}

fun castlingColorOf(castling: Castling): Color = when (castling) {
	WHITE_KINGSIDE, WHITE_QUEENSIDE -> WHITE
	BLACK_KINGSIDE, BLACK_QUEENSIDE -> BLACK
	else -> error("Invalid castling: $castling")
}

fun castlingTypeOf(castling: Castling): Color = when (castling) {
	WHITE_KINGSIDE, BLACK_KINGSIDE -> KINGSIDE
	WHITE_QUEENSIDE, BLACK_QUEENSIDE -> QUEENSIDE
	else -> error("Invalid castling: $castling")
}
