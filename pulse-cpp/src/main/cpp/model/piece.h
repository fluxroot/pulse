// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.
#pragma once

#include <array>

namespace pulse::piece {

constexpr int MASK = 0x1F;

constexpr int WHITE_PAWN = 0;
constexpr int WHITE_KNIGHT = 1;
constexpr int WHITE_BISHOP = 2;
constexpr int WHITE_ROOK = 3;
constexpr int WHITE_QUEEN = 4;
constexpr int WHITE_KING = 5;
constexpr int BLACK_PAWN = 6;
constexpr int BLACK_KNIGHT = 7;
constexpr int BLACK_BISHOP = 8;
constexpr int BLACK_ROOK = 9;
constexpr int BLACK_QUEEN = 10;
constexpr int BLACK_KING = 11;

constexpr int NOPIECE = 12;

constexpr int VALUES_SIZE = 12;
constexpr std::array<int, VALUES_SIZE> values = {
		WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING,
		BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING
};

bool isValid(int piece);

int valueOf(int color, int piecetype);

int getType(int piece);

int getColor(int piece);
}
