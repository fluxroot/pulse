/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "square.h"
#include "file.h"
#include "rank.h"

#include <cassert>

namespace pulse {

const std::array<int, Square::SIZE> Square::values = {
  a1, b1, c1, d1, e1, f1, g1, h1,
  a2, b2, c2, d2, e2, f2, g2, h2,
  a3, b3, c3, d3, e3, f3, g3, h3,
  a4, b4, c4, d4, e4, f4, g4, h4,
  a5, b5, c5, d5, e5, f5, g5, h5,
  a6, b6, c6, d6, e6, f6, g6, h6,
  a7, b7, c7, d7, e7, f7, g7, h7,
  a8, b8, c8, d8, e8, f8, g8, h8
};

bool Square::isValid(int square) {
  return (square & 0x88) == 0;
}

int Square::fromNotation(const std::string& notation) {
  int file = File::fromNotation(notation[0]);
  int rank = Rank::fromNotation(notation[1]);

  if (file != File::NOFILE && rank != Rank::NORANK) {
    int square = (rank << 4) + file;
    assert(isValid(square));

    return square;
  } else {
    return Square::NOSQUARE;
  }
}

std::string Square::toNotation(int square) {
  assert(isValid(square));

  std::string notation;
  notation += File::toNotation(getFile(square));
  notation += Rank::toNotation(getRank(square));

  return notation;
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
