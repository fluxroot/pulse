/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

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

	NoMove Move = (noMoveType << moveTypeShift) |
		(NoSquare << originSquareShift) |
		(NoSquare << targetSquareShift) |
		(NoPiece << originPieceShift) |
		(NoPiece << targetPieceShift) |
		(NoPieceType << promotionShift)
)

func moveOf(mt moveType, originSq Square, targetSq Square, originPc Piece, targetPc Piece, promotion PieceType) Move {
	return (mt << moveTypeShift) |
		(originSq << originSquareShift) |
		(targetSq << targetSquareShift) |
		(originPc << originPieceShift) |
		(targetPc << targetPieceShift) |
		(promotion << promotionShift)
}

func moveTypeOf(m Move) moveType {
	return (m & moveTypeMask) >> moveTypeShift
}

func OriginSquareOf(m Move) Square {
	return (m & originSquareMask) >> originSquareShift
}

func TargetSquareOf(m Move) Square {
	return (m & targetSquareMask) >> targetSquareShift
}

func OriginPieceOf(m Move) Piece {
	return (m & originPieceMask) >> originPieceShift
}

func TargetPieceOf(m Move) Piece {
	return (m & targetPieceMask) >> targetPieceShift
}

func PromotionOf(m Move) PieceType {
	return (m & promotionMask) >> promotionShift
}
