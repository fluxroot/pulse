/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type PieceType = int

const (
	Pawn   PieceType = 0
	Knight PieceType = 1
	Bishop PieceType = 2
	Rook   PieceType = 3
	Queen  PieceType = 4
	King   PieceType = 5

	NoPieceType PieceType = 6
)

var (
	PieceTypes = [6]PieceType{Pawn, Knight, Bishop, Rook, Queen, King}
)
