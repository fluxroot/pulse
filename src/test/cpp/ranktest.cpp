/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "rank.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(ranktest, testValues) {
  for (auto rank : Rank::values) {
    EXPECT_EQ(rank, Rank::values[rank]);
  }
}

TEST(ranktest, testIsValid) {
  for (auto rank : Rank::values) {
    EXPECT_TRUE(Rank::isValid(rank));
  }

  EXPECT_FALSE(Rank::isValid(Rank::NORANK));
}
