// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.
#pragma once

#include <array>

namespace pulse::piecetype {

constexpr int MASK = 0x7;

constexpr int PAWN = 0;
constexpr int KNIGHT = 1;
constexpr int BISHOP = 2;
constexpr int ROOK = 3;
constexpr int QUEEN = 4;
constexpr int KING = 5;

constexpr int NOPIECETYPE = 6;

constexpr int VALUES_SIZE = 6;
constexpr std::array<int, VALUES_SIZE> values = {
		PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
};

// Piece values as defined by Larry Kaufman
constexpr int PAWN_VALUE = 100;
constexpr int KNIGHT_VALUE = 325;
constexpr int BISHOP_VALUE = 325;
constexpr int ROOK_VALUE = 500;
constexpr int QUEEN_VALUE = 975;
constexpr int KING_VALUE = 20000;

bool isValidPromotion(int piecetype);

bool isSliding(int piecetype);

int getValue(int piecetype);
}
