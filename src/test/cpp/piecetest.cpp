/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "piece.h"
#include "color.h"
#include "piecetype.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(piecetest, testValues) {
	for (auto piece : piece::values) {
		EXPECT_EQ(piece, piece::values[piece]);
	}
}

TEST(piecetest, testValueOf) {
	EXPECT_EQ(+piece::WHITE_PAWN, piece::valueOf(color::WHITE, PieceType::PAWN));
	EXPECT_EQ(+piece::WHITE_KNIGHT, piece::valueOf(color::WHITE, PieceType::KNIGHT));
	EXPECT_EQ(+piece::WHITE_BISHOP, piece::valueOf(color::WHITE, PieceType::BISHOP));
	EXPECT_EQ(+piece::WHITE_ROOK, piece::valueOf(color::WHITE, PieceType::ROOK));
	EXPECT_EQ(+piece::WHITE_QUEEN, piece::valueOf(color::WHITE, PieceType::QUEEN));
	EXPECT_EQ(+piece::WHITE_KING, piece::valueOf(color::WHITE, PieceType::KING));
	EXPECT_EQ(+piece::BLACK_PAWN, piece::valueOf(color::BLACK, PieceType::PAWN));
	EXPECT_EQ(+piece::BLACK_KNIGHT, piece::valueOf(color::BLACK, PieceType::KNIGHT));
	EXPECT_EQ(+piece::BLACK_BISHOP, piece::valueOf(color::BLACK, PieceType::BISHOP));
	EXPECT_EQ(+piece::BLACK_ROOK, piece::valueOf(color::BLACK, PieceType::ROOK));
	EXPECT_EQ(+piece::BLACK_QUEEN, piece::valueOf(color::BLACK, PieceType::QUEEN));
	EXPECT_EQ(+piece::BLACK_KING, piece::valueOf(color::BLACK, PieceType::KING));
}

TEST(piecetest, testGetType) {
	EXPECT_EQ(+PieceType::PAWN, piece::getType(piece::WHITE_PAWN));
	EXPECT_EQ(+PieceType::PAWN, piece::getType(piece::BLACK_PAWN));
	EXPECT_EQ(+PieceType::KNIGHT, piece::getType(piece::WHITE_KNIGHT));
	EXPECT_EQ(+PieceType::KNIGHT, piece::getType(piece::BLACK_KNIGHT));
	EXPECT_EQ(+PieceType::BISHOP, piece::getType(piece::WHITE_BISHOP));
	EXPECT_EQ(+PieceType::BISHOP, piece::getType(piece::BLACK_BISHOP));
	EXPECT_EQ(+PieceType::ROOK, piece::getType(piece::WHITE_ROOK));
	EXPECT_EQ(+PieceType::ROOK, piece::getType(piece::BLACK_ROOK));
	EXPECT_EQ(+PieceType::QUEEN, piece::getType(piece::WHITE_QUEEN));
	EXPECT_EQ(+PieceType::QUEEN, piece::getType(piece::BLACK_QUEEN));
	EXPECT_EQ(+PieceType::KING, piece::getType(piece::WHITE_KING));
	EXPECT_EQ(+PieceType::KING, piece::getType(piece::BLACK_KING));
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
