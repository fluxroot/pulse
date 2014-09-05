/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "castling.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(castlingtest, testIsValid) {
  for (auto castling : Castling::values) {
    EXPECT_TRUE(Castling::isValid(castling));
  }

  EXPECT_FALSE(Castling::isValid(Castling::NOCASTLING));
}
