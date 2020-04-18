/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "move.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(movetest, testCreation) {
	int move = move::valueOf(movetype::PAWNPROMOTION, Square::a7, Square::b8, Piece::WHITE_PAWN, Piece::BLACK_QUEEN,
			PieceType::KNIGHT);

	EXPECT_EQ(+movetype::PAWNPROMOTION, move::getType(move));
	EXPECT_EQ(+Square::a7, move::getOriginSquare(move));
	EXPECT_EQ(+Square::b8, move::getTargetSquare(move));
	EXPECT_EQ(+Piece::WHITE_PAWN, move::getOriginPiece(move));
	EXPECT_EQ(+Piece::BLACK_QUEEN, move::getTargetPiece(move));
	EXPECT_EQ(+PieceType::KNIGHT, move::getPromotion(move));
}

TEST(movetest, testPromotion) {
	int move = move::valueOf(movetype::PAWNPROMOTION, Square::b7, Square::c8, Piece::WHITE_PAWN, Piece::BLACK_QUEEN,
			PieceType::KNIGHT);

	EXPECT_EQ(+PieceType::KNIGHT, move::getPromotion(move));
}
