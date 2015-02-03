/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "bitboard.h"
#include "square.h"

#include <cassert>

namespace pulse {

const std::array<int, 64> Bitboard::lsbTable = {
   0, 47,  1, 56, 48, 27,  2, 60,
  57, 49, 41, 37, 28, 16,  3, 61,
  54, 58, 35, 52, 50, 42, 21, 44,
  38, 32, 29, 23, 17, 11,  4, 62,
  46, 55, 26, 59, 40, 36, 15, 53,
  34, 51, 20, 43, 31, 22, 10, 45,
  25, 39, 14, 33, 19, 30,  9, 24,
  13, 18,  8, 12,  7,  6,  5, 63
};

bool Bitboard::operator==(const Bitboard& bitboard) const {
  return this->squares == bitboard.squares;
}

bool Bitboard::operator!=(const Bitboard& bitboard) const {
  return !(*this == bitboard);
}

int Bitboard::numberOfTrailingZeros(uint64_t b) {
  assert(b != 0);

  return lsbTable[((b ^ (b - 1)) * DEBRUIJN64) >> 58];
}

int Bitboard::bitCount(uint64_t b) {
  b = b - ((b >> 1) & 0x5555555555555555ULL);
  b = (b & 0x3333333333333333ULL) + ((b >> 2) & 0x3333333333333333ULL);
  b = (b + (b >> 4)) & 0x0F0F0F0F0F0F0F0FULL;
  return (b * 0x0101010101010101ULL) >> 56;
}

int Bitboard::next(uint64_t squares) {
  return toX88Square(numberOfTrailingZeros(squares));
}

uint64_t Bitboard::remainder(uint64_t squares) {
  assert(squares != 0);

  return squares & (squares - 1);
}

int Bitboard::toX88Square(int square) {
  assert(square >= 0 && square < 64);

  return ((square & ~7) << 1) | (square & 7);
}

int Bitboard::toBitSquare(int square) {
  assert(Square::isValid(square));

  return ((square & ~7) >> 1) | (square & 7);
}

int Bitboard::size() {
  return bitCount(squares);
}

void Bitboard::add(int square) {
  assert(Square::isValid(square));
  assert((squares & (1ULL << toBitSquare(square))) == 0);

  squares |= 1ULL << toBitSquare(square);
}

void Bitboard::remove(int square) {
  assert(Square::isValid(square));
  assert((squares & (1ULL << toBitSquare(square))) != 0);

  squares &= ~(1ULL << toBitSquare(square));
}

}
