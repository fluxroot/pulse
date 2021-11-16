/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "model/castlingtype.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(castlingtypetest, testValues) {
	for (auto castlingtype: castlingtype::values) {
		EXPECT_EQ(castlingtype, castlingtype::values[castlingtype]);
	}
}
