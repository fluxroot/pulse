/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

typealias Move = Int

//  0 -  2: move type (required)
//  3 -  9: origin square (required)
// 10 - 16: target square (required)
// 17 - 21: origin piece (required)
// 22 - 26: target piece (optional)
// 27 - 29: promotion (optional)
internal const val MOVE_TYPE_SHIFT = 0
internal const val MOVE_TYPE_MASK = 0x7 shl MOVE_TYPE_SHIFT
internal const val ORIGIN_SQUARE_SHIFT = 3
internal const val ORIGIN_SQUARE_MASK = 0x7F shl ORIGIN_SQUARE_SHIFT
internal const val TARGET_SQUARE_SHIFT = 10
internal const val TARGET_SQUARE_MASK = 0x7F shl TARGET_SQUARE_SHIFT
internal const val ORIGIN_PIECE_SHIFT = 17
internal const val ORIGIN_PIECE_MASK = 0x1F shl ORIGIN_PIECE_SHIFT
internal const val TARGET_PIECE_SHIFT = 22
internal const val TARGET_PIECE_MASK = 0x1F shl TARGET_PIECE_SHIFT
internal const val PROMOTION_SHIFT = 27
internal const val PROMOTION_MASK = 0x7 shl PROMOTION_SHIFT

const val NO_MOVE: Move = (NO_MOVE_TYPE shl MOVE_TYPE_SHIFT) or
	(NO_SQUARE shl ORIGIN_SQUARE_SHIFT) or
	(NO_SQUARE shl TARGET_SQUARE_SHIFT) or
	(NO_PIECE shl ORIGIN_PIECE_SHIFT) or
	(NO_PIECE shl TARGET_PIECE_SHIFT) or
	(NO_PIECE_TYPE shl PROMOTION_SHIFT)

internal fun moveOf(
	moveType: MoveType,
	originSquare: Square,
	targetSquare: Square,
	originPiece: Piece,
	targetPiece: Piece,
	promotion: PieceType,
): Move = (moveType shl MOVE_TYPE_SHIFT) or
	(originSquare shl ORIGIN_SQUARE_SHIFT) or
	(targetSquare shl TARGET_SQUARE_SHIFT) or
	(originPiece shl ORIGIN_PIECE_SHIFT) or
	(targetPiece shl TARGET_PIECE_SHIFT) or
	(promotion shl PROMOTION_SHIFT)

internal fun moveTypeOf(move: Move): MoveType = (move and MOVE_TYPE_MASK) ushr MOVE_TYPE_SHIFT

fun originSquareOf(move: Move): Square = (move and ORIGIN_SQUARE_MASK) ushr ORIGIN_SQUARE_SHIFT

fun targetSquareOf(move: Move): Square = (move and TARGET_SQUARE_MASK) ushr TARGET_SQUARE_SHIFT

fun originPieceOf(move: Move): Piece = (move and ORIGIN_PIECE_MASK) ushr ORIGIN_PIECE_SHIFT

fun targetPieceOf(move: Move): Piece = (move and TARGET_PIECE_MASK) ushr TARGET_PIECE_SHIFT

fun promotionOf(move: Move): PieceType = (move and PROMOTION_MASK) ushr PROMOTION_SHIFT
