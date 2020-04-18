/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "piecetype.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(piecetypetest, testValues) {
	for (auto piecetype : piecetype::values) {
		EXPECT_EQ(piecetype, piecetype::values[piecetype]);
	}
}

TEST(piecetypetest, testIsValidPromotion) {
	EXPECT_TRUE(piecetype::isValidPromotion(piecetype::KNIGHT));
	EXPECT_TRUE(piecetype::isValidPromotion(piecetype::BISHOP));
	EXPECT_TRUE(piecetype::isValidPromotion(piecetype::ROOK));
	EXPECT_TRUE(piecetype::isValidPromotion(piecetype::QUEEN));
	EXPECT_FALSE(piecetype::isValidPromotion(piecetype::PAWN));
	EXPECT_FALSE(piecetype::isValidPromotion(piecetype::KING));
	EXPECT_FALSE(piecetype::isValidPromotion(piecetype::NOPIECETYPE));
}

TEST(piecetypetest, testIsSliding) {
	EXPECT_TRUE(piecetype::isSliding(piecetype::BISHOP));
	EXPECT_TRUE(piecetype::isSliding(piecetype::ROOK));
	EXPECT_TRUE(piecetype::isSliding(piecetype::QUEEN));
	EXPECT_FALSE(piecetype::isSliding(piecetype::PAWN));
	EXPECT_FALSE(piecetype::isSliding(piecetype::KNIGHT));
	EXPECT_FALSE(piecetype::isSliding(piecetype::KING));
}
