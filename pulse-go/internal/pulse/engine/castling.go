/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import "fmt"

type Castling = int

const (
	WhiteKingside  Castling = 1 // 1 << 0
	WhiteQueenside Castling = 1 << 1
	BlackKingside  Castling = 1 << 2
	BlackQueenside Castling = 1 << 3

	NoCastling Castling = 0
)

var (
	Castlings = [4]Castling{
		WhiteKingside, WhiteQueenside,
		BlackKingside, BlackQueenside,
	}
)

func IsValidCastling(cast Castling) bool {
	switch cast {
	case WhiteKingside, WhiteQueenside, BlackKingside, BlackQueenside:
		return true
	default:
		return false
	}
}

func CastlingOf(col Color, ct CastlingType) Castling {
	switch col {
	case White:
		switch ct {
		case Kingside:
			return WhiteKingside
		case Queenside:
			return WhiteQueenside
		default:
			panic(fmt.Sprintf("Invalid castling type: %v", ct))
		}
	case Black:
		switch ct {
		case Kingside:
			return BlackKingside
		case Queenside:
			return BlackQueenside
		default:
			panic(fmt.Sprintf("Invalid castling type: %v", ct))
		}
	default:
		panic(fmt.Sprintf("Invalid color: %v", col))
	}
}

func CastlingColorOf(cast Castling) Color {
	switch cast {
	case WhiteKingside, WhiteQueenside:
		return White
	case BlackKingside, BlackQueenside:
		return Black
	default:
		panic(fmt.Sprintf("Invalid castling: %v", cast))
	}
}

func CastlingTypeOf(cast Castling) CastlingType {
	switch cast {
	case WhiteKingside, BlackKingside:
		return Kingside
	case WhiteQueenside, BlackQueenside:
		return Queenside
	default:
		panic(fmt.Sprintf("Invalid castling: %v", cast))
	}
}
