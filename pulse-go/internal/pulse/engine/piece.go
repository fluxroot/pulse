/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import "fmt"

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

func IsValidPiece(pc Piece) bool {
	switch pc {
	case WhitePawn, WhiteKnight, WhiteBishop, WhiteRook, WhiteQueen, WhiteKing, BlackPawn, BlackKnight, BlackBishop, BlackRook, BlackQueen, BlackKing:
		return true
	default:
		return false
	}
}

func PieceOf(col Color, pt PieceType) Piece {
	switch col {
	case White:
		switch pt {
		case Pawn:
			return WhitePawn
		case Knight:
			return WhiteKnight
		case Bishop:
			return WhiteBishop
		case Rook:
			return WhiteRook
		case Queen:
			return WhiteQueen
		case King:
			return WhiteKing
		default:
			panic(fmt.Sprintf("Invalid piece type: %v", pt))
		}
	case Black:
		switch pt {
		case Pawn:
			return BlackPawn
		case Knight:
			return BlackKnight
		case Bishop:
			return BlackBishop
		case Rook:
			return BlackRook
		case Queen:
			return BlackQueen
		case King:
			return BlackKing
		default:
			panic(fmt.Sprintf("Invalid piece type: %v", pt))
		}
	default:
		panic(fmt.Sprintf("Invalid color: %v", col))
	}
}

func PieceColorOf(pc Piece) Color {
	switch pc {
	case WhitePawn, WhiteKnight, WhiteBishop, WhiteRook, WhiteQueen, WhiteKing:
		return White
	case BlackPawn, BlackKnight, BlackBishop, BlackRook, BlackQueen, BlackKing:
		return Black
	default:
		panic(fmt.Sprintf("Invalid piece: %v", pc))
	}
}

func PieceTypeOf(pc Piece) PieceType {
	switch pc {
	case WhitePawn, BlackPawn:
		return Pawn
	case WhiteKnight, BlackKnight:
		return Knight
	case WhiteBishop, BlackBishop:
		return Bishop
	case WhiteRook, BlackRook:
		return Rook
	case WhiteQueen, BlackQueen:
		return Queen
	case WhiteKing, BlackKing:
		return King
	default:
		panic(fmt.Sprintf("Invalid piece: %v", pc))
	}
}
