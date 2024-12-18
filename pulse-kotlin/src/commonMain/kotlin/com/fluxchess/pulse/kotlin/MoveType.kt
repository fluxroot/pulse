/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin

typealias MoveType = Int

const val NORMAL_MOVE: MoveType = 0
const val PAWN_DOUBLE_MOVE: MoveType = 1
const val PAWN_PROMOTION_MOVE: MoveType = 2
const val EN_PASSANT_MOVE: MoveType = 3
const val CASTLING_MOVE: MoveType = 4

const val NO_MOVE_TYPE: MoveType = 5

val moveTypes = intArrayOf(NORMAL_MOVE, PAWN_DOUBLE_MOVE, PAWN_PROMOTION_MOVE, EN_PASSANT_MOVE, CASTLING_MOVE)
