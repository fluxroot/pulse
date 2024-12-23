/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

internal typealias Value = Int

// Piece values as defined by Larry Kaufman
internal const val PAWN_VALUE: Value = 100
internal const val KNIGHT_VALUE: Value = 325
internal const val BISHOP_VALUE: Value = 325
internal const val ROOK_VALUE: Value = 500
internal const val QUEEN_VALUE: Value = 975
internal const val KING_VALUE: Value = 20000

internal const val INFINITE: Value = 200000
internal const val CHECKMATE: Value = 100000
internal const val CHECKMATE_THRESHOLD: Value = CHECKMATE - MAX_PLY
internal const val DRAW: Value = 0

internal const val NO_VALUE: Value = 300000
