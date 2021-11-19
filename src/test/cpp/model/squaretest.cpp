// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "model/square.h"
#include "model/file.h"
#include "model/rank.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(squaretest, testValues) {
	for (auto rank: rank::values) {
		for (auto file: file::values) {
			int square = square::valueOf(file, rank);

			EXPECT_EQ(file, square::getFile(square));
			EXPECT_EQ(rank, square::getRank(square));
		}
	}
}
