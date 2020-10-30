/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include <exception>
#include "piecetype.h"

namespace pulse::piecetype {

bool isValidPromotion(int piecetype) {
	switch (piecetype) {
		case KNIGHT:
		case BISHOP:
		case ROOK:
		case QUEEN:
			return true;
		default:
			return false;
	}
}

bool isSliding(int piecetype) {
	switch (piecetype) {
		case BISHOP:
		case ROOK:
		case QUEEN:
			return true;
		case PAWN:
		case KNIGHT:
		case KING:
			return false;
		default:
			throw std::exception();
	}
}

int getValue(int piecetype) {
	switch (piecetype) {
		case PAWN:
			return PAWN_VALUE;
		case KNIGHT:
			return KNIGHT_VALUE;
		case BISHOP:
			return BISHOP_VALUE;
		case ROOK:
			return ROOK_VALUE;
		case QUEEN:
			return QUEEN_VALUE;
		case KING:
			return KING_VALUE;
		default:
			throw std::exception();
	}
}
}
