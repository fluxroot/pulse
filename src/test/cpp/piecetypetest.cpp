/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "piecetype.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(piecetypetest, testValues) {
  for (auto piecetype : PieceType::values) {
    EXPECT_EQ(piecetype, PieceType::values[piecetype]);
  }
}

TEST(piecetypetest, testIsValid) {
  for (auto piecetype : PieceType::values) {
    EXPECT_TRUE(PieceType::isValid(piecetype));
  }

  EXPECT_FALSE(PieceType::isValid(PieceType::NOPIECETYPE));
}

TEST(piecetypetest, testIsValidPromotion) {
  EXPECT_TRUE(PieceType::isValidPromotion(PieceType::KNIGHT));
  EXPECT_TRUE(PieceType::isValidPromotion(PieceType::BISHOP));
  EXPECT_TRUE(PieceType::isValidPromotion(PieceType::ROOK));
  EXPECT_TRUE(PieceType::isValidPromotion(PieceType::QUEEN));
  EXPECT_FALSE(PieceType::isValidPromotion(PieceType::PAWN));
  EXPECT_FALSE(PieceType::isValidPromotion(PieceType::KING));
  EXPECT_FALSE(PieceType::isValidPromotion(PieceType::NOPIECETYPE));
}

TEST(piecetypetest, testIsSliding) {
  EXPECT_TRUE(PieceType::isSliding(PieceType::BISHOP));
  EXPECT_TRUE(PieceType::isSliding(PieceType::ROOK));
  EXPECT_TRUE(PieceType::isSliding(PieceType::QUEEN));
  EXPECT_FALSE(PieceType::isSliding(PieceType::PAWN));
  EXPECT_FALSE(PieceType::isSliding(PieceType::KNIGHT));
  EXPECT_FALSE(PieceType::isSliding(PieceType::KING));
}
