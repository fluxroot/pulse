// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include <model/color.h>
#include <model/castlingtype.h>
#include "model/castling.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(castlingtest, testValueOf) {
	EXPECT_EQ(+castling::WHITE_KINGSIDE, castling::valueOf(color::WHITE, castlingtype::KINGSIDE));
	EXPECT_EQ(+castling::WHITE_QUEENSIDE, castling::valueOf(color::WHITE, castlingtype::QUEENSIDE));
	EXPECT_EQ(+castling::BLACK_KINGSIDE, castling::valueOf(color::BLACK, castlingtype::KINGSIDE));
	EXPECT_EQ(+castling::BLACK_QUEENSIDE, castling::valueOf(color::BLACK, castlingtype::QUEENSIDE));
}
