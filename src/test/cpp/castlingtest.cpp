/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include <color.h>
#include <castlingtype.h>
#include "castling.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(castlingtest, testValueOf) {
	EXPECT_EQ(+castling::WHITE_KINGSIDE, castling::valueOf(Color::WHITE, castlingtype::KINGSIDE));
	EXPECT_EQ(+castling::WHITE_QUEENSIDE, castling::valueOf(Color::WHITE, castlingtype::QUEENSIDE));
	EXPECT_EQ(+castling::BLACK_KINGSIDE, castling::valueOf(Color::BLACK, castlingtype::KINGSIDE));
	EXPECT_EQ(+castling::BLACK_QUEENSIDE, castling::valueOf(Color::BLACK, castlingtype::QUEENSIDE));
}
