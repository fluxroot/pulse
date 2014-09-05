/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "notation.h"
#include "board.h"
#include "castlingtype.h"
#include "file.h"
#include "rank.h"
#include "piecetype.h"
#include "move.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(boardtest, testEquals) {
  // Standard board test
  Board board1(Notation::toBoard(Notation::STANDARDBOARD));
  Board board2(Notation::toBoard(Notation::STANDARDBOARD));

  // reflexive test
  EXPECT_EQ(board1, board1);

  // symmetric test
  EXPECT_EQ(board1, board2);
  EXPECT_EQ(board2, board1);

  // FEN test
  Board board3(Notation::toBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
  EXPECT_EQ(board1, board3);

  Board board4(Notation::toBoard("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1"));
  Board board5(Notation::toBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1"));
  Board board6(Notation::toBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1"));
  Board board7(Notation::toBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1 1"));
  Board board8(Notation::toBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 2"));

  EXPECT_NE(board1, board4);
  EXPECT_NE(board1, board5);
  EXPECT_NE(board1, board6);
  EXPECT_NE(board1, board7);
  EXPECT_NE(board1, board8);
}

TEST(boardtest, testToString) {
  std::string fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

  Board board(Notation::toBoard(fen));

  EXPECT_EQ(fen, Notation::fromBoard(board));
}

TEST(boardtest, testActiveColor) {
  Board board(Notation::toBoard(Notation::STANDARDBOARD));

  // Move white pawn
  int move = Move::valueOf(MoveType::NORMAL, Square::a2, Square::a3, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);
  EXPECT_EQ(+Color::BLACK, board.activeColor);

  // Move black pawn
  move = Move::valueOf(MoveType::NORMAL, Square::b7, Square::b6, Piece::BLACK_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);
  EXPECT_EQ(+Color::WHITE, board.activeColor);
}

TEST(boardtest, testHalfMoveClock) {
  Board board(Notation::toBoard(Notation::STANDARDBOARD));

  // Move white pawn
  int move = Move::valueOf(MoveType::NORMAL, Square::a2, Square::a3, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);
  EXPECT_EQ(0, board.halfmoveClock);

  // Move black pawn
  move = Move::valueOf(MoveType::NORMAL, Square::b7, Square::b6, Piece::BLACK_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  // Move white knight
  move = Move::valueOf(MoveType::NORMAL, Square::b1, Square::c3, Piece::WHITE_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);
  EXPECT_EQ(1, board.halfmoveClock);
}

TEST(boardtest, testFullMoveNumber) {
  Board board(Notation::toBoard(Notation::STANDARDBOARD));

  // Move white pawn
  int move = Move::valueOf(MoveType::NORMAL, Square::a2, Square::a3, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);
  EXPECT_EQ(1, board.getFullmoveNumber());

  // Move black pawn
  move = Move::valueOf(MoveType::NORMAL, Square::b7, Square::b6, Piece::BLACK_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);
  EXPECT_EQ(2, board.getFullmoveNumber());
}

TEST(boardtest, testIsRepetition) {
  Board board(Notation::toBoard(Notation::STANDARDBOARD));

  // Move white knight
  int move = Move::valueOf(MoveType::NORMAL, Square::b1, Square::c3, Piece::WHITE_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  // Move black knight
  move = Move::valueOf(MoveType::NORMAL, Square::b8, Square::c6, Piece::BLACK_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  // Move white knight
  move = Move::valueOf(MoveType::NORMAL, Square::g1, Square::f3, Piece::WHITE_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  // Move black knight
  move = Move::valueOf(MoveType::NORMAL, Square::c6, Square::b8, Piece::BLACK_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  // Move white knight
  move = Move::valueOf(MoveType::NORMAL, Square::f3, Square::g1, Piece::WHITE_KNIGHT, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  EXPECT_TRUE(board.isRepetition());
}

TEST(boardtest, testHasInsufficientMaterial) {
  Board board(Notation::toBoard("8/4k3/8/8/8/8/2K5/8 w - - 0 1"));
  EXPECT_TRUE(board.hasInsufficientMaterial());

  board = Notation::toBoard("8/4k3/8/2B5/8/8/2K5/8 b - - 0 1");
  EXPECT_TRUE(board.hasInsufficientMaterial());

  board = Notation::toBoard("8/4k3/8/2B3n1/8/8/2K5/8 b - - 0 1");
  EXPECT_TRUE(board.hasInsufficientMaterial());
}

TEST(boardtest, testNormalMove) {
  Board board(Notation::toBoard(Notation::STANDARDBOARD));
  uint64_t zobristKey = board.zobristKey;

  int move = Move::valueOf(MoveType::NORMAL, Square::a2, Square::a3, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);
  board.undoMove(move);

  EXPECT_EQ(Notation::STANDARDBOARD, Notation::fromBoard(board));
  EXPECT_EQ(zobristKey, board.zobristKey);
}

TEST(boardtest, testPawnDoubleMove) {
  Board board(Notation::toBoard(Notation::STANDARDBOARD));
  uint64_t zobristKey = board.zobristKey;

  int move = Move::valueOf(MoveType::PAWNDOUBLE, Square::a2, Square::a4, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  EXPECT_EQ(+Square::a3, board.enPassantSquare);

  board.undoMove(move);

  EXPECT_EQ(Notation::STANDARDBOARD, Notation::fromBoard(board));
  EXPECT_EQ(zobristKey, board.zobristKey);
}

TEST(boardtest, testPawnPromotionMove) {
  Board board(Notation::toBoard("8/P5k1/8/8/2K5/8/8/8 w - - 0 1"));
  uint64_t zobristKey = board.zobristKey;

  int move = Move::valueOf(MoveType::PAWNPROMOTION, Square::a7, Square::a8, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::QUEEN);
  board.makeMove(move);

  EXPECT_EQ(+Piece::WHITE_QUEEN, board.board[Square::a8]);

  board.undoMove(move);

  EXPECT_EQ("8/P5k1/8/8/2K5/8/8/8 w - - 0 1", Notation::fromBoard(board));
  EXPECT_EQ(zobristKey, board.zobristKey);
}

TEST(boardtest, testEnPassantMove) {
  Board board(Notation::toBoard("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1"));
  uint64_t zobristKey = board.zobristKey;

  // Make en passant move
  int move = Move::valueOf(MoveType::ENPASSANT, Square::e4, Square::d3, Piece::BLACK_PAWN, Piece::WHITE_PAWN, PieceType::NOPIECETYPE);
  board.makeMove(move);

  EXPECT_EQ(+Piece::NOPIECE, board.board[Square::d4]);
  EXPECT_EQ(+Piece::BLACK_PAWN, board.board[Square::d3]);
  EXPECT_EQ(+Square::NOSQUARE, board.enPassantSquare);

  board.undoMove(move);

  EXPECT_EQ("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1", Notation::fromBoard(board));
  EXPECT_EQ(zobristKey, board.zobristKey);
}

TEST(boardtest, testCastlingMove) {
  Board board(Notation::toBoard("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1"));
  uint64_t zobristKey = board.zobristKey;

  int move = Move::valueOf(MoveType::CASTLING, Square::e1, Square::c1, Piece::WHITE_KING, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  EXPECT_EQ(+File::NOFILE, board.castlingRights[Castling::WHITE_QUEENSIDE]);

  board.undoMove(move);

  EXPECT_EQ("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", Notation::fromBoard(board));
  EXPECT_EQ(zobristKey, board.zobristKey);

  board = Notation::toBoard("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
  zobristKey = board.zobristKey;

  move = Move::valueOf(MoveType::CASTLING, Square::e1, Square::g1, Piece::WHITE_KING, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  EXPECT_EQ(+File::NOFILE, board.castlingRights[Castling::WHITE_KINGSIDE]);

  board.undoMove(move);

  EXPECT_EQ("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", Notation::fromBoard(board));
  EXPECT_EQ(zobristKey, board.zobristKey);
}
