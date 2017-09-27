/*
 * Copyright (C) 2013-2016 Phokham Nonava
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
	EXPECT_EQ(+Castling::WHITE_KINGSIDE, Castling::valueOf(Color::WHITE, CastlingType::KINGSIDE));
	EXPECT_EQ(+Castling::WHITE_QUEENSIDE, Castling::valueOf(Color::WHITE, CastlingType::QUEENSIDE));
	EXPECT_EQ(+Castling::BLACK_KINGSIDE, Castling::valueOf(Color::BLACK, CastlingType::KINGSIDE));
	EXPECT_EQ(+Castling::BLACK_QUEENSIDE, Castling::valueOf(Color::BLACK, CastlingType::QUEENSIDE));
}
