/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "notation.h"
#include "file.h"
#include "rank.h"
#include "piecetype.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(notationtest, testStandardPosition) {
  Position position(Notation::toPosition(Notation::STANDARDPOSITION));

  // Test pawns
  for (auto file : File::values) {
    EXPECT_EQ(+Piece::WHITE_PAWN, position.board[Square::valueOf(file, Rank::r2)]);
    EXPECT_EQ(+Piece::BLACK_PAWN, position.board[Square::valueOf(file, Rank::r7)]);
  }

  // Test knights
  EXPECT_EQ(+Piece::WHITE_KNIGHT, position.board[Square::valueOf(File::b, Rank::r1)]);
  EXPECT_EQ(+Piece::WHITE_KNIGHT, position.board[Square::valueOf(File::g, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_KNIGHT, position.board[Square::valueOf(File::b, Rank::r8)]);
  EXPECT_EQ(+Piece::BLACK_KNIGHT, position.board[Square::valueOf(File::g, Rank::r8)]);

  // Test bishops
  EXPECT_EQ(+Piece::WHITE_BISHOP, position.board[Square::valueOf(File::c, Rank::r1)]);
  EXPECT_EQ(+Piece::WHITE_BISHOP, position.board[Square::valueOf(File::f, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_BISHOP, position.board[Square::valueOf(File::c, Rank::r8)]);
  EXPECT_EQ(+Piece::BLACK_BISHOP, position.board[Square::valueOf(File::f, Rank::r8)]);

  // Test rooks
  EXPECT_EQ(+Piece::WHITE_ROOK, position.board[Square::valueOf(File::a, Rank::r1)]);
  EXPECT_EQ(+Piece::WHITE_ROOK, position.board[Square::valueOf(File::h, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_ROOK, position.board[Square::valueOf(File::a, Rank::r8)]);
  EXPECT_EQ(+Piece::BLACK_ROOK, position.board[Square::valueOf(File::h, Rank::r8)]);

  // Test queens
  EXPECT_EQ(+Piece::WHITE_QUEEN, position.board[Square::valueOf(File::d, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_QUEEN, position.board[Square::valueOf(File::d, Rank::r8)]);

  // Test kings
  EXPECT_EQ(+Piece::WHITE_KING, position.board[Square::valueOf(File::e, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_KING, position.board[Square::valueOf(File::e, Rank::r8)]);

  EXPECT_EQ(8 * PieceType::PAWN_VALUE
    + 2 * PieceType::KNIGHT_VALUE
    + 2 * PieceType::BISHOP_VALUE
    + 2 * PieceType::ROOK_VALUE
    + PieceType::QUEEN_VALUE
    + PieceType::KING_VALUE,
    position.material[Color::WHITE]);
  EXPECT_EQ(8 * PieceType::PAWN_VALUE
    + 2 * PieceType::KNIGHT_VALUE
    + 2 * PieceType::BISHOP_VALUE
    + 2 * PieceType::ROOK_VALUE
    + PieceType::QUEEN_VALUE
    + PieceType::KING_VALUE,
    position.material[Color::BLACK]);

  // Test castling
  EXPECT_EQ(+File::h, position.castlingRights[Castling::WHITE_KINGSIDE]);
  EXPECT_EQ(+File::a, position.castlingRights[Castling::WHITE_QUEENSIDE]);
  EXPECT_EQ(+File::h, position.castlingRights[Castling::BLACK_KINGSIDE]);
  EXPECT_EQ(+File::a, position.castlingRights[Castling::BLACK_QUEENSIDE]);

  // Test en passant
  EXPECT_EQ(+Square::NOSQUARE, position.enPassantSquare);

  // Test active color
  EXPECT_EQ(+Color::WHITE, position.activeColor);

  // Test half move clock
  EXPECT_EQ(0, position.halfmoveClock);

  // Test full move number
  EXPECT_EQ(1, position.getFullmoveNumber());
}
