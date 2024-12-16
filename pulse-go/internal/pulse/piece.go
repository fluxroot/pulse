/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type Piece = int

const (
	WhitePawn   Piece = 0
	WhiteKnight Piece = 1
	WhiteBishop Piece = 2
	WhiteRook   Piece = 3
	WhiteQueen  Piece = 4
	WhiteKing   Piece = 5
	BlackPawn   Piece = 6
	BlackKnight Piece = 7
	BlackBishop Piece = 8
	BlackRook   Piece = 9
	BlackQueen  Piece = 10
	BlackKing   Piece = 11

	NoPiece Piece = 12
)

var (
	Pieces = [12]Piece{
		WhitePawn, WhiteKnight, WhiteBishop, WhiteRook, WhiteQueen, WhiteKing,
		BlackPawn, BlackKnight, BlackBishop, BlackRook, BlackQueen, BlackKing,
	}
)
