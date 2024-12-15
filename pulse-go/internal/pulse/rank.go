/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type Rank = int

const (
	_1 Rank = 0
	_2 Rank = 1
	_3 Rank = 2
	_4 Rank = 3
	_5 Rank = 4
	_6 Rank = 5
	_7 Rank = 6
	_8 Rank = 7

	NoRank Rank = 8
)

var (
	Ranks = [8]Rank{_1, _2, _3, _4, _5, _6, _7, _8}
)
