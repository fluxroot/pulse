// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "model/color.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(colortest, testValues) {
	for (auto color: color::values) {
		EXPECT_EQ(color, color::values[color]);
	}
}

TEST(colortest, testOpposite) {
	EXPECT_EQ(+color::WHITE, color::opposite(color::BLACK));
	EXPECT_EQ(+color::BLACK, color::opposite(color::WHITE));
}
