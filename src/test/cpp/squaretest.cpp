/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "square.h"
#include "file.h"
#include "rank.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(squaretest, testValues) {
	for (auto rank : rank::values) {
		for (auto file : file::values) {
			int square = square::valueOf(file, rank);

			EXPECT_EQ(file, square::getFile(square));
			EXPECT_EQ(rank, square::getRank(square));
		}
	}
}
