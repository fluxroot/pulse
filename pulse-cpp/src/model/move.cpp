// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "move.h"

namespace pulse::move {
int valueOf(int type, int originSquare, int targetSquare, int originPiece, int targetPiece, int promotion) {
	int move = 0;

	// Encode type
	move |= type << TYPE_SHIFT;

	// Encode origin square
	move |= originSquare << ORIGIN_SQUARE_SHIFT;

	// Encode target square
	move |= targetSquare << TARGET_SQUARE_SHIFT;

	// Encode origin piece
	move |= originPiece << ORIGIN_PIECE_SHIFT;

	// Encode target piece
	move |= targetPiece << TARGET_PIECE_SHIFT;

	// Encode promotion
	move |= promotion << PROMOTION_SHIFT;

	return move;
}

int getType(int move) {
	return (move & TYPE_MASK) >> TYPE_SHIFT;
}

int getOriginSquare(int move) {
	return (move & ORIGIN_SQUARE_MASK) >> ORIGIN_SQUARE_SHIFT;
}

int getTargetSquare(int move) {
	return (move & TARGET_SQUARE_MASK) >> TARGET_SQUARE_SHIFT;
}

int getOriginPiece(int move) {
	return (move & ORIGIN_PIECE_MASK) >> ORIGIN_PIECE_SHIFT;
}

int getTargetPiece(int move) {
	return (move & TARGET_PIECE_MASK) >> TARGET_PIECE_SHIFT;
}

int getPromotion(int move) {
	return (move & PROMOTION_MASK) >> PROMOTION_SHIFT;
}
}
