/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "castling.h"
#include "color.h"
#include "castlingtype.h"

namespace pulse::castling {

int valueOf(int color, int castlingtype) {
	switch (color) {
		case Color::WHITE:
			switch (castlingtype) {
				case castlingtype::KINGSIDE:
					return WHITE_KINGSIDE;
				case castlingtype::QUEENSIDE:
					return WHITE_QUEENSIDE;
				default:
					throw std::exception();
			}
		case Color::BLACK:
			switch (castlingtype) {
				case castlingtype::KINGSIDE:
					return BLACK_KINGSIDE;
				case castlingtype::QUEENSIDE:
					return BLACK_QUEENSIDE;
				default:
					throw std::exception();
			}
		default:
			throw std::exception();
	}
}

int getType(int castling) {
	switch (castling) {
		case WHITE_KINGSIDE:
		case BLACK_KINGSIDE:
			return castlingtype::KINGSIDE;
		case WHITE_QUEENSIDE:
		case BLACK_QUEENSIDE:
			return castlingtype::QUEENSIDE;
		default:
			throw std::exception();
	}
}

int getColor(int castling) {
	switch (castling) {
		case WHITE_KINGSIDE:
		case WHITE_QUEENSIDE:
			return Color::WHITE;
		case BLACK_KINGSIDE:
		case BLACK_QUEENSIDE:
			return Color::BLACK;
		default:
			throw std::exception();
	}
}
}
