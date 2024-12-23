/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

type Rank = int

const (
	Rank1 Rank = 0
	Rank2 Rank = 1
	Rank3 Rank = 2
	Rank4 Rank = 3
	Rank5 Rank = 4
	Rank6 Rank = 5
	Rank7 Rank = 6
	Rank8 Rank = 7

	NoRank Rank = 8
)

var (
	Ranks = [8]Rank{Rank1, Rank2, Rank3, Rank4, Rank5, Rank6, Rank7, Rank8}
)

func IsValidRank(r Rank) bool {
	switch r {
	case Rank1, Rank2, Rank3, Rank4, Rank5, Rank6, Rank7, Rank8:
		return true
	default:
		return false
	}
}
