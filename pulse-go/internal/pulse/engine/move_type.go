/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

type MoveType = int

const (
	NormalMove        MoveType = 0
	PawnDoubleMove    MoveType = 1
	PawnPromotionMove MoveType = 2
	EnPassantMove     MoveType = 3
	CastlingMove      MoveType = 4

	NoMoveType MoveType = 5
)

var (
	MoveTypes = [5]MoveType{NormalMove, PawnDoubleMove, PawnPromotionMove, EnPassantMove, CastlingMove}
)
