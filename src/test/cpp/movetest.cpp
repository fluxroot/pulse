/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "move.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(movetest, testCreation) {
  int move = Move::valueOf(MoveType::PAWNPROMOTION, Square::a7, Square::b8, Piece::WHITE_PAWN, Piece::BLACK_QUEEN, PieceType::KNIGHT);

  EXPECT_EQ(+MoveType::PAWNPROMOTION, Move::getType(move));
  EXPECT_EQ(+Square::a7, Move::getOriginSquare(move));
  EXPECT_EQ(+Square::b8, Move::getTargetSquare(move));
  EXPECT_EQ(+Piece::WHITE_PAWN, Move::getOriginPiece(move));
  EXPECT_EQ(+Piece::BLACK_QUEEN, Move::getTargetPiece(move));
  EXPECT_EQ(+PieceType::KNIGHT, Move::getPromotion(move));
}

TEST(movetest, testPromotion) {
  int move = Move::valueOf(MoveType::PAWNPROMOTION, Square::b7, Square::c8, Piece::WHITE_PAWN, Piece::BLACK_QUEEN, PieceType::KNIGHT);

  EXPECT_EQ(+PieceType::KNIGHT, Move::getPromotion(move));
}
