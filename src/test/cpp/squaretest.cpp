/*
 * Copyright (C) 2013-2014 Phokham Nonava
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
  for (auto rank : Rank::values) {
    for (auto file : File::values) {
      std::string notation;
      notation += File::toNotation(file);
      notation += Rank::toNotation(rank);

      int square = Square::fromNotation(notation);

      EXPECT_EQ(file, Square::getFile(square));
      EXPECT_EQ(rank, Square::getRank(square));

      EXPECT_EQ(File::toNotation(file), Square::toNotation(square)[0]);
      EXPECT_EQ(Rank::toNotation(rank), Square::toNotation(square)[1]);
    }
  }

  EXPECT_EQ(+Square::NOSQUARE, Square::fromNotation("x0"));
}

TEST(squaretest, testIsValid) {
  for (auto square : Square::values) {
    EXPECT_TRUE(Square::isValid(square));
  }

  EXPECT_FALSE(Square::isValid(Square::NOSQUARE));
}
