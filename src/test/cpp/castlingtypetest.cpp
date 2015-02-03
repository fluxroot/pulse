/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "castlingtype.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(castlingtypetest, testValues) {
  for (auto castlingtype : CastlingType::values) {
    EXPECT_EQ(castlingtype, CastlingType::values[castlingtype]);
  }
}
