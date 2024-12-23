/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

type moveType = int

const (
	normalMove        moveType = 0
	pawnDoubleMove    moveType = 1
	pawnPromotionMove moveType = 2
	enPassantMove     moveType = 3
	castlingMove      moveType = 4

	noMoveType moveType = 5
)

var (
	moveTypes = [5]moveType{normalMove, pawnDoubleMove, pawnPromotionMove, enPassantMove, castlingMove}
)
