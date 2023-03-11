// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include <exception>
#include "piece.h"
#include "color.h"
#include "piecetype.h"

namespace pulse::piece {

bool isValid(int piece) {
	switch (piece) {
		case WHITE_PAWN:
		case WHITE_KNIGHT:
		case WHITE_BISHOP:
		case WHITE_ROOK:
		case WHITE_QUEEN:
		case WHITE_KING:
		case BLACK_PAWN:
		case BLACK_KNIGHT:
		case BLACK_BISHOP:
		case BLACK_ROOK:
		case BLACK_QUEEN:
		case BLACK_KING:
			return true;
		default:
			return false;
	}
}

int valueOf(int color, int piecetype) {
	switch (color) {
		case color::WHITE:
			switch (piecetype) {
				case piecetype::PAWN:
					return WHITE_PAWN;
				case piecetype::KNIGHT:
					return WHITE_KNIGHT;
				case piecetype::BISHOP:
					return WHITE_BISHOP;
				case piecetype::ROOK:
					return WHITE_ROOK;
				case piecetype::QUEEN:
					return WHITE_QUEEN;
				case piecetype::KING:
					return WHITE_KING;
				default:
					throw std::exception();
			}
		case color::BLACK:
			switch (piecetype) {
				case piecetype::PAWN:
					return BLACK_PAWN;
				case piecetype::KNIGHT:
					return BLACK_KNIGHT;
				case piecetype::BISHOP:
					return BLACK_BISHOP;
				case piecetype::ROOK:
					return BLACK_ROOK;
				case piecetype::QUEEN:
					return BLACK_QUEEN;
				case piecetype::KING:
					return BLACK_KING;
				default:
					throw std::exception();
			}
		default:
			throw std::exception();
	}
}

int getType(int piece) {
	switch (piece) {
		case WHITE_PAWN:
		case BLACK_PAWN:
			return piecetype::PAWN;
		case WHITE_KNIGHT:
		case BLACK_KNIGHT:
			return piecetype::KNIGHT;
		case WHITE_BISHOP:
		case BLACK_BISHOP:
			return piecetype::BISHOP;
		case WHITE_ROOK:
		case BLACK_ROOK:
			return piecetype::ROOK;
		case WHITE_QUEEN:
		case BLACK_QUEEN:
			return piecetype::QUEEN;
		case WHITE_KING:
		case BLACK_KING:
			return piecetype::KING;
		default:
			throw std::exception();
	}
}

int getColor(int piece) {
	switch (piece) {
		case WHITE_PAWN:
		case WHITE_KNIGHT:
		case WHITE_BISHOP:
		case WHITE_ROOK:
		case WHITE_QUEEN:
		case WHITE_KING:
			return color::WHITE;
		case BLACK_PAWN:
		case BLACK_KNIGHT:
		case BLACK_BISHOP:
		case BLACK_ROOK:
		case BLACK_QUEEN:
		case BLACK_KING:
			return color::BLACK;
		default:
			throw std::exception();
	}
}
}
