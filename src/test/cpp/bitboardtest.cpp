/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "bitboard.h"
#include "square.h"

#include "gtest/gtest.h"

#include <random>

using namespace pulse;

TEST(bitboardtest, testNumberOfTrailingZeros) {
  Bitboard b;
  int i = 0;
  for (auto square : Square::values) {
    b.add(square);

    EXPECT_EQ(i, Bitboard::numberOfTrailingZeros(b.squares));

    b.remove(square);
    ++i;
  }
}

TEST(bitboardtest, testBitCount) {
  std::default_random_engine generator;

  for (int i = 0; i < 1000; ++i) {
    uint64_t b = 0;
    int count = 0;

    int index = 0;
    while (true) {
      std::uniform_int_distribution<int> distribution(1, 4);
      index += distribution(generator);
      if (index < 64) {
        b |= 1ULL << index;
        ++count;
      } else {
        break;
      }
    }

    EXPECT_EQ(count, Bitboard::bitCount(b));
  }
}
