/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin

typealias Piece = Int

const val WHITE_PAWN: Piece = 0
const val WHITE_KNIGHT: Piece = 1
const val WHITE_BISHOP: Piece = 2
const val WHITE_ROOK: Piece = 3
const val WHITE_QUEEN: Piece = 4
const val WHITE_KING: Piece = 5
const val BLACK_PAWN: Piece = 6
const val BLACK_KNIGHT: Piece = 7
const val BLACK_BISHOP: Piece = 8
const val BLACK_ROOK: Piece = 9
const val BLACK_QUEEN: Piece = 10
const val BLACK_KING: Piece = 11

const val NO_PIECE: Piece = 12

val pieces = intArrayOf(
	WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING,
	BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING,
)
