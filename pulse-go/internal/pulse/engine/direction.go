/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

type direction = int

const (
	north     direction = 16
	east      direction = 1
	south     direction = -16
	west      direction = -1
	northEast direction = north + east
	southEast direction = south + east
	southWest direction = south + west
	northWest direction = north + west
)

var (
	pawnMoveDirections = [2]direction{
		north, // WHITE
		south, // BLACK
	}

	pawnCapturingDirections = [2][2]direction{
		{northEast, northWest}, // WHITE
		{southEast, southWest}, // BLACK
	}

	knightDirections = [8]direction{
		north + north + east,
		north + north + west,
		north + east + east,
		north + west + west,
		south + south + east,
		south + south + west,
		south + east + east,
		south + west + west,
	}

	bishopDirections = [4]direction{
		northEast, northWest, southEast, southWest,
	}

	rookDirections = [4]direction{
		north, east, south, west,
	}

	queenDirections = [8]direction{
		north, east, south, west,
		northEast, northWest, southEast, southWest,
	}

	kingDirections = [8]direction{
		north, east, south, west,
		northEast, northWest, southEast, southWest,
	}
)
