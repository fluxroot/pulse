/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "square.h"
#include "file.h"
#include "rank.h"

#include <cassert>

namespace pulse {

const std::array<int, Square::VALUES_SIZE> Square::values = {
  a1, b1, c1, d1, e1, f1, g1, h1,
  a2, b2, c2, d2, e2, f2, g2, h2,
  a3, b3, c3, d3, e3, f3, g3, h3,
  a4, b4, c4, d4, e4, f4, g4, h4,
  a5, b5, c5, d5, e5, f5, g5, h5,
  a6, b6, c6, d6, e6, f6, g6, h6,
  a7, b7, c7, d7, e7, f7, g7, h7,
  a8, b8, c8, d8, e8, f8, g8, h8
};

const std::vector<std::vector<int>> Square::pawnDirections = {
    { N, NE, NW }, // Color::WHITE
    { S, SE, SW }  // Color::BLACK
};
const std::vector<int> Square::knightDirections = {
  N + N + E,
  N + N + W,
  N + E + E,
  N + W + W,
  S + S + E,
  S + S + W,
  S + E + E,
  S + W + W
};
const std::vector<int> Square::bishopDirections = {
  NE, NW, SE, SW
};
const std::vector<int> Square::rookDirections = {
  N, E, S, W
};
const std::vector<int> Square::queenDirections = {
  N, E, S, W,
  NE, NW, SE, SW
};
const std::vector<int> Square::kingDirections = {
  N, E, S, W,
  NE, NW, SE, SW
};

bool Square::isValid(int square) {
  return (square & 0x88) == 0;
}

int Square::valueOf(int file, int rank) {
  assert(File::isValid(file));
  assert(Rank::isValid(rank));

  int square = (rank << 4) + file;
  assert(isValid(square));

  return square;
}

int Square::getFile(int square) {
  assert(isValid(square));

  int file = square & 0xF;
  assert(File::isValid(file));

  return file;
}

int Square::getRank(int square) {
  assert(isValid(square));

  int rank = square >> 4;
  assert(Rank::isValid(rank));

  return rank;
}

}
