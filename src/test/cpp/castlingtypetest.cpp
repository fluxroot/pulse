/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "castlingtype.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(castlingtypetest, testValues) {
  for (auto castlingType : CastlingType::values) {
    EXPECT_EQ(castlingType, CastlingType::values[castlingType]);
  }
}
