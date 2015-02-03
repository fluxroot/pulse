/*
 * Copyright (C) 2013-2015 Phokham Nonava
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
  for (auto color : Color::values) {
    for (auto piecetype : PieceType::values) {
      int piece = Piece::valueOf(color, piecetype);

      EXPECT_EQ(piece, Piece::values[piece]);
    }
  }
}

TEST(piecetest, testIsValid) {
  for (auto piece : Piece::values) {
    EXPECT_TRUE(Piece::isValid(piece));
  }

  EXPECT_FALSE(Piece::isValid(Piece::NOPIECE));
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
  EXPECT_EQ(+Color::WHITE, Piece::getColor(Piece::WHITE_PAWN));
  EXPECT_EQ(+Color::BLACK, Piece::getColor(Piece::BLACK_PAWN));
  EXPECT_EQ(+Color::WHITE, Piece::getColor(Piece::WHITE_KNIGHT));
  EXPECT_EQ(+Color::BLACK, Piece::getColor(Piece::BLACK_KNIGHT));
  EXPECT_EQ(+Color::WHITE, Piece::getColor(Piece::WHITE_BISHOP));
  EXPECT_EQ(+Color::BLACK, Piece::getColor(Piece::BLACK_BISHOP));
  EXPECT_EQ(+Color::WHITE, Piece::getColor(Piece::WHITE_ROOK));
  EXPECT_EQ(+Color::BLACK, Piece::getColor(Piece::BLACK_ROOK));
  EXPECT_EQ(+Color::WHITE, Piece::getColor(Piece::WHITE_QUEEN));
  EXPECT_EQ(+Color::BLACK, Piece::getColor(Piece::BLACK_QUEEN));
  EXPECT_EQ(+Color::WHITE, Piece::getColor(Piece::WHITE_KING));
  EXPECT_EQ(+Color::BLACK, Piece::getColor(Piece::BLACK_KING));
}
