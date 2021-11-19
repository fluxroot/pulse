// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "model/rank.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(ranktest, testValues) {
	for (auto rank: rank::values) {
		EXPECT_EQ(rank, rank::values[rank]);
	}
}
