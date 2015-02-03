/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "notation.h"
#include "position.h"
#include "castlingtype.h"
#include "file.h"
#include "rank.h"
#include "piecetype.h"
#include "move.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(positiontest, testEquals) {
  // Standard position test
  Position position1(Notation::toPosition(Notation::STANDARDPOSITION));
  Position position2(Notation::toPosition(Notation::STANDARDPOSITION));

  // reflexive test
  EXPECT_EQ(position1, position1);

  // symmetric test
  EXPECT_EQ(position1, position2);
  EXPECT_EQ(position2, position1);

  // FEN test
  Position position3(Notation::toPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
  EXPECT_EQ(position1, position3);

  Position position4(Notation::toPosition("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1"));
  Position position5(Notation::toPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1"));
  Position position6(Notation::toPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1"));
  Position position7(Notation::toPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1 1"));
  Position position8(Notation::toPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 2"));

  EXPECT_NE(position1, position4);
  EXPECT_NE(position1, position5);
  EXPECT_NE(position1, position6);
  EXPECT_NE(position1, position7);
  EXPECT_NE(position1, position8);
}

TEST(positiontest, testToString) {
  std::string fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

  Position position(Notation::toPosition(fen));

  EXPECT_EQ(fen, Notation::fromPosition(position));
}

TEST(positiontest, testActiveColor) {
  Position position(Notation::toPosition(Notation::STANDARDPOSITION));

  // Move white pawn
  int move = Move::valueOf(MoveType::NORMAL, Square::a2, Square::a3, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);
  EXPECT_EQ(+Color::BLACK, position.activeColor);

  // Move black pawn
  move = Move::valueOf(MoveType::NORMAL, Square::b7, Square::b6, Piece::BLACK_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);
  EXPECT_EQ(+Color::WHITE, position.activeColor);
}

TEST(positiontest, testHalfMoveClock) {
  Position position(Notation::toPosition(Notation::STANDARDPOSITION));

  // Move white pawn
  int move = Move::valueOf(MoveType::NORMAL, Square::a2, Square::a3, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);
  EXPECT_EQ(0, position.halfmoveClock);

  // Move black pawn
  move = Move::valueOf(MoveType::NORMAL, Square::b7, Square::b6, Piece::BLACK_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);

  // Move white knight
  move = Move::valueOf(MoveType::NORMAL, Square::b1, Square::c3, Piece::WHITE_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);
  EXPECT_EQ(1, position.halfmoveClock);
}

TEST(positiontest, testFullMoveNumber) {
  Position position(Notation::toPosition(Notation::STANDARDPOSITION));

  // Move white pawn
  int move = Move::valueOf(MoveType::NORMAL, Square::a2, Square::a3, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);
  EXPECT_EQ(1, position.getFullmoveNumber());

  // Move black pawn
  move = Move::valueOf(MoveType::NORMAL, Square::b7, Square::b6, Piece::BLACK_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);
  EXPECT_EQ(2, position.getFullmoveNumber());
}

TEST(positiontest, testIsRepetition) {
  Position position(Notation::toPosition(Notation::STANDARDPOSITION));

  // Move white knight
  int move = Move::valueOf(MoveType::NORMAL, Square::b1, Square::c3, Piece::WHITE_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);

  // Move black knight
  move = Move::valueOf(MoveType::NORMAL, Square::b8, Square::c6, Piece::BLACK_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);

  // Move white knight
  move = Move::valueOf(MoveType::NORMAL, Square::g1, Square::f3, Piece::WHITE_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);

  // Move black knight
  move = Move::valueOf(MoveType::NORMAL, Square::c6, Square::b8, Piece::BLACK_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);

  // Move white knight
  move = Move::valueOf(MoveType::NORMAL, Square::f3, Square::g1, Piece::WHITE_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);

  EXPECT_TRUE(position.isRepetition());
}

TEST(positiontest, testHasInsufficientMaterial) {
  Position position(Notation::toPosition("8/4k3/8/8/8/8/2K5/8 w - - 0 1"));
  EXPECT_TRUE(position.hasInsufficientMaterial());

  position = Notation::toPosition("8/4k3/8/2B5/8/8/2K5/8 b - - 0 1");
  EXPECT_TRUE(position.hasInsufficientMaterial());

  position = Notation::toPosition("8/4k3/8/2B3n1/8/8/2K5/8 b - - 0 1");
  EXPECT_TRUE(position.hasInsufficientMaterial());
}

TEST(positiontest, testNormalMove) {
  Position position(Notation::toPosition(Notation::STANDARDPOSITION));
  uint64_t zobristKey = position.zobristKey;

  int move = Move::valueOf(MoveType::NORMAL, Square::a2, Square::a3, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);
  position.undoMove(move);

  EXPECT_EQ(Notation::STANDARDPOSITION, Notation::fromPosition(position));
  EXPECT_EQ(zobristKey, position.zobristKey);
}

TEST(positiontest, testPawnDoubleMove) {
  Position position(Notation::toPosition(Notation::STANDARDPOSITION));
  uint64_t zobristKey = position.zobristKey;

  int move = Move::valueOf(MoveType::PAWNDOUBLE, Square::a2, Square::a4, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);

  EXPECT_EQ(+Square::a3, position.enPassantSquare);

  position.undoMove(move);

  EXPECT_EQ(Notation::STANDARDPOSITION, Notation::fromPosition(position));
  EXPECT_EQ(zobristKey, position.zobristKey);
}

TEST(positiontest, testPawnPromotionMove) {
  Position position(Notation::toPosition("8/P5k1/8/8/2K5/8/8/8 w - - 0 1"));
  uint64_t zobristKey = position.zobristKey;

  int move = Move::valueOf(MoveType::PAWNPROMOTION, Square::a7, Square::a8, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::QUEEN);
  position.makeMove(move);

  EXPECT_EQ(+Piece::WHITE_QUEEN, position.board[Square::a8]);

  position.undoMove(move);

  EXPECT_EQ("8/P5k1/8/8/2K5/8/8/8 w - - 0 1", Notation::fromPosition(position));
  EXPECT_EQ(zobristKey, position.zobristKey);
}

TEST(positiontest, testEnPassantMove) {
  Position position(Notation::toPosition("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1"));
  uint64_t zobristKey = position.zobristKey;

  // Make en passant move
  int move = Move::valueOf(MoveType::ENPASSANT, Square::e4, Square::d3, Piece::BLACK_PAWN, Piece::WHITE_PAWN, PieceType::NOPIECETYPE);
  position.makeMove(move);

  EXPECT_EQ(+Piece::NOPIECE, position.board[Square::d4]);
  EXPECT_EQ(+Piece::BLACK_PAWN, position.board[Square::d3]);
  EXPECT_EQ(+Square::NOSQUARE, position.enPassantSquare);

  position.undoMove(move);

  EXPECT_EQ("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1", Notation::fromPosition(position));
  EXPECT_EQ(zobristKey, position.zobristKey);
}

TEST(positiontest, testCastlingMove) {
  Position position(Notation::toPosition("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1"));
  uint64_t zobristKey = position.zobristKey;

  int move = Move::valueOf(MoveType::CASTLING, Square::e1, Square::c1, Piece::WHITE_KING, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);

  EXPECT_EQ(+Castling::NOCASTLING, position.castlingRights & Castling::WHITE_QUEENSIDE);

  position.undoMove(move);

  EXPECT_EQ("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", Notation::fromPosition(position));
  EXPECT_EQ(zobristKey, position.zobristKey);

  position = Notation::toPosition("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
  zobristKey = position.zobristKey;

  move = Move::valueOf(MoveType::CASTLING, Square::e1, Square::g1, Piece::WHITE_KING, Piece::NOPIECE, PieceType::NOPIECETYPE);
  position.makeMove(move);

  EXPECT_EQ(+Castling::NOCASTLING, position.castlingRights & Castling::WHITE_KINGSIDE);

  position.undoMove(move);

  EXPECT_EQ("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", Notation::fromPosition(position));
  EXPECT_EQ(zobristKey, position.zobristKey);
}
