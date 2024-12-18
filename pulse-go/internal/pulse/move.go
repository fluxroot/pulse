/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type Move = int

const (
	//  0 -  2: move type (required)
	//  3 -  9: origin square (required)
	// 10 - 16: target square (required)
	// 17 - 21: origin piece (required)
	// 22 - 26: target piece (optional)
	// 27 - 29: promotion (optional)
	moveTypeShift     = 0
	moveTypeMask      = 0x7 << moveTypeShift
	originSquareShift = 3
	originSquareMask  = 0x7F << originSquareShift
	targetSquareShift = 10
	targetSquareMask  = 0x7F << targetSquareShift
	originPieceShift  = 17
	originPieceMask   = 0x1F << originPieceShift
	targetPieceShift  = 22
	targetPieceMask   = 0x1F << targetPieceShift
	promotionShift    = 27
	promotionMask     = 0x7 << promotionShift

	NoMove Move = (NoMoveType << moveTypeShift) |
		(NoSquare << originSquareShift) |
		(NoSquare << targetSquareShift) |
		(NoPiece << originPieceShift) |
		(NoPiece << targetPieceShift) |
		(NoPieceType << promotionShift)
)

func MoveOf(moveType MoveType, originSquare Square, targetSquare Square, originPiece Piece, targetPiece Piece, promotion PieceType) Move {
	return (moveType << moveTypeShift) |
		(originSquare << originSquareShift) |
		(targetSquare << targetSquareShift) |
		(originPiece << originPieceShift) |
		(targetPiece << targetPieceShift) |
		(promotion << promotionShift)
}

func MoveTypeOf(move Move) MoveType {
	return (move & moveTypeMask) >> moveTypeShift
}

func OriginSquareOf(move Move) Square {
	return (move & originSquareMask) >> originSquareShift
}

func TargetSquareOf(move Move) Square {
	return (move & targetSquareMask) >> targetSquareShift
}

func OriginPieceOf(move Move) Piece {
	return (move & originPieceMask) >> originPieceShift
}

func TargetPieceOf(move Move) Piece {
	return (move & targetPieceMask) >> targetPieceShift
}

func PromotionOf(move Move) PieceType {
	return (move & promotionMask) >> promotionShift
}
