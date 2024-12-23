/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

internal typealias Direction = Int

internal const val NORTH: Direction = 16
internal const val EAST: Direction = 1
internal const val SOUTH: Direction = -16
internal const val WEST: Direction = -1
internal const val NORTH_EAST: Direction = NORTH + EAST
internal const val SOUTH_EAST: Direction = SOUTH + EAST
internal const val SOUTH_WEST: Direction = SOUTH + WEST
internal const val NORTH_WEST: Direction = NORTH + WEST

internal val pawnMoveDirections: Array<Direction> = arrayOf(
	NORTH, // WHITE
	SOUTH, // BLACK
)

internal val pawnCapturingDirections: Array<Array<Direction>> = arrayOf(
	arrayOf(NORTH_EAST, NORTH_WEST), // WHITE
	arrayOf(SOUTH_EAST, SOUTH_WEST), // BLACK
)

internal val knightDirections: Array<Direction> = arrayOf(
	NORTH + NORTH + EAST,
	NORTH + NORTH + WEST,
	NORTH + EAST + EAST,
	NORTH + WEST + WEST,
	SOUTH + SOUTH + EAST,
	SOUTH + SOUTH + WEST,
	SOUTH + EAST + EAST,
	SOUTH + WEST + WEST,
)

internal val bishopDirections: Array<Direction> = arrayOf(
	NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST,
)

internal val rookDirections: Array<Direction> = arrayOf(
	NORTH, EAST, SOUTH, WEST,
)

internal val queenDirections: Array<Direction> = arrayOf(
	NORTH, EAST, SOUTH, WEST,
	NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST,
)

internal val kingDirections: Array<Direction> = arrayOf(
	NORTH, EAST, SOUTH, WEST,
	NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST,
)
