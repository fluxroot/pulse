/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "color.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(colortest, testValues) {
  for (auto color : Color::values) {
    EXPECT_EQ(color, Color::values[color]);
  }
}

TEST(colortest, testIsValid) {
  for (auto color : Color::values) {
    EXPECT_TRUE(Color::isValid(color));
    EXPECT_EQ(color, Color::values[color]);
  }

  EXPECT_FALSE(Color::isValid(Color::NOCOLOR));
}

TEST(colortest, testOpposite) {
  EXPECT_EQ(+Color::WHITE, Color::opposite(Color::BLACK));
  EXPECT_EQ(+Color::BLACK, Color::opposite(Color::WHITE));
}
