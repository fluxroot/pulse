/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "castling.h"
#include "color.h"
#include "castlingtype.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(castlingtest, testValues) {
  for (auto color : Color::values) {
    for (auto castlingType : CastlingType::values) {
      int castling = Castling::valueOf(color, castlingType);

      EXPECT_EQ(castling, Castling::values[castling]);
    }
  }
}

TEST(castlingtest, testIsValid) {
  for (auto castling : Castling::values) {
    EXPECT_TRUE(Castling::isValid(castling));
  }

  EXPECT_FALSE(Castling::isValid(Castling::NOCASTLING));
}
