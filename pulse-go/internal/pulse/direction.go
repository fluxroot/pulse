/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type Direction = int

const (
	North     Direction = 16
	East      Direction = 1
	South     Direction = -16
	West      Direction = -1
	NorthEast Direction = North + East
	SouthEast Direction = South + East
	SouthWest Direction = South + West
	NorthWest Direction = North + West
)

var (
	pawnMoveDirections = []Direction{
		North, // WHITE
		South, // BLACK
	}

	pawnCapturingDirections = [][]Direction{
		{NorthEast, NorthWest}, // WHITE
		{SouthEast, SouthWest}, // BLACK
	}

	knightDirections = []Direction{
		North + North + East,
		North + North + West,
		North + East + East,
		North + West + West,
		South + South + East,
		South + South + West,
		South + East + East,
		South + West + West,
	}

	bishopDirections = []Direction{
		NorthEast, NorthWest, SouthEast, SouthWest,
	}

	rookDirections = []Direction{
		North, East, South, West,
	}

	queenDirections = []Direction{
		North, East, South, West,
		NorthEast, NorthWest, SouthEast, SouthWest,
	}

	kingDirections = []Direction{
		North, East, South, West,
		NorthEast, NorthWest, SouthEast, SouthWest,
	}
)
