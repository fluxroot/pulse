/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin

typealias Direction = Int

const val NORTH: Direction = 16
const val EAST: Direction = 1
const val SOUTH: Direction = -16
const val WEST: Direction = -1
const val NORTH_EAST: Direction = NORTH + EAST
const val SOUTH_EAST: Direction = SOUTH + EAST
const val SOUTH_WEST: Direction = SOUTH + WEST
const val NORTH_WEST: Direction = NORTH + WEST

val pawnMoveDirections: Array<Direction> = arrayOf(
	NORTH, // WHITE
	SOUTH, // BLACK
)

val pawnCapturingDirections: Array<Array<Direction>> = arrayOf(
	arrayOf(NORTH_EAST, NORTH_WEST), // WHITE
	arrayOf(SOUTH_EAST, SOUTH_WEST), // BLACK
)

val knightDirections: Array<Direction> = arrayOf(
	NORTH + NORTH + EAST,
	NORTH + NORTH + WEST,
	NORTH + EAST + EAST,
	NORTH + WEST + WEST,
	SOUTH + SOUTH + EAST,
	SOUTH + SOUTH + WEST,
	SOUTH + EAST + EAST,
	SOUTH + WEST + WEST,
)

val bishopDirections: Array<Direction> = arrayOf(
	NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST,
)

val rookDirections: Array<Direction> = arrayOf(
	NORTH, EAST, SOUTH, WEST,
)

val queenDirections: Array<Direction> = arrayOf(
	NORTH, EAST, SOUTH, WEST,
	NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST,
)

val kingDirections: Array<Direction> = arrayOf(
	NORTH, EAST, SOUTH, WEST,
	NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST,
)
