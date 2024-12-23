/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import "fmt"

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

func IsValidPieceType(pt PieceType) bool {
	switch pt {
	case Pawn, Knight, Bishop, Rook, Queen, King:
		return true
	default:
		return false
	}
}

func isSliding(pt PieceType) bool {
	switch pt {
	case Bishop, Rook, Queen:
		return true
	case Pawn, Knight, King:
		return false
	default:
		panic(fmt.Sprintf("Invalid piece type: %v", pt))
	}
}

func pieceTypeValueOf(pt PieceType) value {
	switch pt {
	case Pawn:
		return pawnValue
	case Knight:
		return knightValue
	case Bishop:
		return bishopValue
	case Rook:
		return rookValue
	case Queen:
		return queenValue
	case King:
		return kingValue
	default:
		panic(fmt.Sprintf("Invalid piece type: %v", pt))
	}
}
