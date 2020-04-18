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
	for (auto piece : Piece::values) {
		EXPECT_EQ(piece, Piece::values[piece]);
	}
}

TEST(piecetest, testValueOf) {
	EXPECT_EQ(+Piece::WHITE_PAWN, Piece::valueOf(color::WHITE, PieceType::PAWN));
	EXPECT_EQ(+Piece::WHITE_KNIGHT, Piece::valueOf(color::WHITE, PieceType::KNIGHT));
	EXPECT_EQ(+Piece::WHITE_BISHOP, Piece::valueOf(color::WHITE, PieceType::BISHOP));
	EXPECT_EQ(+Piece::WHITE_ROOK, Piece::valueOf(color::WHITE, PieceType::ROOK));
	EXPECT_EQ(+Piece::WHITE_QUEEN, Piece::valueOf(color::WHITE, PieceType::QUEEN));
	EXPECT_EQ(+Piece::WHITE_KING, Piece::valueOf(color::WHITE, PieceType::KING));
	EXPECT_EQ(+Piece::BLACK_PAWN, Piece::valueOf(color::BLACK, PieceType::PAWN));
	EXPECT_EQ(+Piece::BLACK_KNIGHT, Piece::valueOf(color::BLACK, PieceType::KNIGHT));
	EXPECT_EQ(+Piece::BLACK_BISHOP, Piece::valueOf(color::BLACK, PieceType::BISHOP));
	EXPECT_EQ(+Piece::BLACK_ROOK, Piece::valueOf(color::BLACK, PieceType::ROOK));
	EXPECT_EQ(+Piece::BLACK_QUEEN, Piece::valueOf(color::BLACK, PieceType::QUEEN));
	EXPECT_EQ(+Piece::BLACK_KING, Piece::valueOf(color::BLACK, PieceType::KING));
}

TEST(piecetest, testGetType) {
	EXPECT_EQ(+PieceType::PAWN, Piece::getType(Piece::WHITE_PAWN));
	EXPECT_EQ(+PieceType::PAWN, Piece::getType(Piece::BLACK_PAWN));
	EXPECT_EQ(+PieceType::KNIGHT, Piece::getType(Piece::WHITE_KNIGHT));
	EXPECT_EQ(+PieceType::KNIGHT, Piece::getType(Piece::BLACK_KNIGHT));
	EXPECT_EQ(+PieceType::BISHOP, Piece::getType(Piece::WHITE_BISHOP));
	EXPECT_EQ(+PieceType::BISHOP, Piece::getType(Piece::BLACK_BISHOP));
	EXPECT_EQ(+PieceType::ROOK, Piece::getType(Piece::WHITE_ROOK));
	EXPECT_EQ(+PieceType::ROOK, Piece::getType(Piece::BLACK_ROOK));
	EXPECT_EQ(+PieceType::QUEEN, Piece::getType(Piece::WHITE_QUEEN));
	EXPECT_EQ(+PieceType::QUEEN, Piece::getType(Piece::BLACK_QUEEN));
	EXPECT_EQ(+PieceType::KING, Piece::getType(Piece::WHITE_KING));
	EXPECT_EQ(+PieceType::KING, Piece::getType(Piece::BLACK_KING));
}

TEST(piecetest, testGetColor) {
	EXPECT_EQ(+color::WHITE, Piece::getColor(Piece::WHITE_PAWN));
	EXPECT_EQ(+color::BLACK, Piece::getColor(Piece::BLACK_PAWN));
	EXPECT_EQ(+color::WHITE, Piece::getColor(Piece::WHITE_KNIGHT));
	EXPECT_EQ(+color::BLACK, Piece::getColor(Piece::BLACK_KNIGHT));
	EXPECT_EQ(+color::WHITE, Piece::getColor(Piece::WHITE_BISHOP));
	EXPECT_EQ(+color::BLACK, Piece::getColor(Piece::BLACK_BISHOP));
	EXPECT_EQ(+color::WHITE, Piece::getColor(Piece::WHITE_ROOK));
	EXPECT_EQ(+color::BLACK, Piece::getColor(Piece::BLACK_ROOK));
	EXPECT_EQ(+color::WHITE, Piece::getColor(Piece::WHITE_QUEEN));
	EXPECT_EQ(+color::BLACK, Piece::getColor(Piece::BLACK_QUEEN));
	EXPECT_EQ(+color::WHITE, Piece::getColor(Piece::WHITE_KING));
	EXPECT_EQ(+color::BLACK, Piece::getColor(Piece::BLACK_KING));
}
