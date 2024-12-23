/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

type value = int

const (
	// Piece values as defined by Larry Kaufman
	pawnValue   value = 100
	knightValue value = 325
	bishopValue value = 325
	rookValue   value = 500
	queenValue  value = 975
	kingValue   value = 20000

	infinite           value = 200000
	checkmate          value = 100000
	checkmateThreshold value = checkmate - MaxPly
	draw               value = 0

	noValue value = 300000
)
