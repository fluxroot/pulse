/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "model/piece.h"
#include "model/color.h"
#include "model/piecetype.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(piecetest, testValues) {
	for (auto piece: piece::values) {
		EXPECT_EQ(piece, piece::values[piece]);
	}
}

TEST(piecetest, testValueOf) {
	EXPECT_EQ(+piece::WHITE_PAWN, piece::valueOf(color::WHITE, piecetype::PAWN));
	EXPECT_EQ(+piece::WHITE_KNIGHT, piece::valueOf(color::WHITE, piecetype::KNIGHT));
	EXPECT_EQ(+piece::WHITE_BISHOP, piece::valueOf(color::WHITE, piecetype::BISHOP));
	EXPECT_EQ(+piece::WHITE_ROOK, piece::valueOf(color::WHITE, piecetype::ROOK));
	EXPECT_EQ(+piece::WHITE_QUEEN, piece::valueOf(color::WHITE, piecetype::QUEEN));
	EXPECT_EQ(+piece::WHITE_KING, piece::valueOf(color::WHITE, piecetype::KING));
	EXPECT_EQ(+piece::BLACK_PAWN, piece::valueOf(color::BLACK, piecetype::PAWN));
	EXPECT_EQ(+piece::BLACK_KNIGHT, piece::valueOf(color::BLACK, piecetype::KNIGHT));
	EXPECT_EQ(+piece::BLACK_BISHOP, piece::valueOf(color::BLACK, piecetype::BISHOP));
	EXPECT_EQ(+piece::BLACK_ROOK, piece::valueOf(color::BLACK, piecetype::ROOK));
	EXPECT_EQ(+piece::BLACK_QUEEN, piece::valueOf(color::BLACK, piecetype::QUEEN));
	EXPECT_EQ(+piece::BLACK_KING, piece::valueOf(color::BLACK, piecetype::KING));
}

TEST(piecetest, testGetType) {
	EXPECT_EQ(+piecetype::PAWN, piece::getType(piece::WHITE_PAWN));
	EXPECT_EQ(+piecetype::PAWN, piece::getType(piece::BLACK_PAWN));
	EXPECT_EQ(+piecetype::KNIGHT, piece::getType(piece::WHITE_KNIGHT));
	EXPECT_EQ(+piecetype::KNIGHT, piece::getType(piece::BLACK_KNIGHT));
	EXPECT_EQ(+piecetype::BISHOP, piece::getType(piece::WHITE_BISHOP));
	EXPECT_EQ(+piecetype::BISHOP, piece::getType(piece::BLACK_BISHOP));
	EXPECT_EQ(+piecetype::ROOK, piece::getType(piece::WHITE_ROOK));
	EXPECT_EQ(+piecetype::ROOK, piece::getType(piece::BLACK_ROOK));
	EXPECT_EQ(+piecetype::QUEEN, piece::getType(piece::WHITE_QUEEN));
	EXPECT_EQ(+piecetype::QUEEN, piece::getType(piece::BLACK_QUEEN));
	EXPECT_EQ(+piecetype::KING, piece::getType(piece::WHITE_KING));
	EXPECT_EQ(+piecetype::KING, piece::getType(piece::BLACK_KING));
}

TEST(piecetest, testGetColor) {
	EXPECT_EQ(+color::WHITE, piece::getColor(piece::WHITE_PAWN));
	EXPECT_EQ(+color::BLACK, piece::getColor(piece::BLACK_PAWN));
	EXPECT_EQ(+color::WHITE, piece::getColor(piece::WHITE_KNIGHT));
	EXPECT_EQ(+color::BLACK, piece::getColor(piece::BLACK_KNIGHT));
	EXPECT_EQ(+color::WHITE, piece::getColor(piece::WHITE_BISHOP));
	EXPECT_EQ(+color::BLACK, piece::getColor(piece::BLACK_BISHOP));
	EXPECT_EQ(+color::WHITE, piece::getColor(piece::WHITE_ROOK));
	EXPECT_EQ(+color::BLACK, piece::getColor(piece::BLACK_ROOK));
	EXPECT_EQ(+color::WHITE, piece::getColor(piece::WHITE_QUEEN));
	EXPECT_EQ(+color::BLACK, piece::getColor(piece::BLACK_QUEEN));
	EXPECT_EQ(+color::WHITE, piece::getColor(piece::WHITE_KING));
	EXPECT_EQ(+color::BLACK, piece::getColor(piece::BLACK_KING));
}
