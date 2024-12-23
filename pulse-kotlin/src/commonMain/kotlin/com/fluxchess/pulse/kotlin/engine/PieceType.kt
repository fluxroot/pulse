/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

typealias PieceType = Int

const val PAWN: PieceType = 0
const val KNIGHT: PieceType = 1
const val BISHOP: PieceType = 2
const val ROOK: PieceType = 3
const val QUEEN: PieceType = 4
const val KING: PieceType = 5

const val NO_PIECE_TYPE: PieceType = 6

val pieceTypes = intArrayOf(PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING)

fun isValidPieceType(pieceType: PieceType): Boolean = when (pieceType) {
	PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING -> true
	else -> false
}

internal fun isSliding(pieceType: PieceType): Boolean = when (pieceType) {
	BISHOP, ROOK, QUEEN -> true
	PAWN, KNIGHT, KING -> false
	else -> error("Invalid piece type: $pieceType")
}

internal fun pieceTypeValueOf(pieceType: PieceType): Value = when (pieceType) {
	PAWN -> PAWN_VALUE
	KNIGHT -> KNIGHT_VALUE
	BISHOP -> BISHOP_VALUE
	ROOK -> ROOK_VALUE
	QUEEN -> QUEEN_VALUE
	KING -> KING_VALUE
	else -> error("Invalid piece type: $pieceType")
}
