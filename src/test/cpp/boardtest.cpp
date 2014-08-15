/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "board.h"
#include "castlingtype.h"
#include "file.h"
#include "rank.h"
#include "piecetype.h"
#include "move.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(boardtest, testConstructorFromId) {
  Board board(Board::STANDARDBOARD);

  // Test pawns
  for (auto file : File::values) {
    EXPECT_EQ(+Piece::WHITE_PAWN, board.board[Square::valueOf(file, Rank::r2)]);
    EXPECT_EQ(+Piece::BLACK_PAWN, board.board[Square::valueOf(file, Rank::r7)]);
  }

  // Test knights
  EXPECT_EQ(+Piece::WHITE_KNIGHT, board.board[Square::valueOf(File::b, Rank::r1)]);
  EXPECT_EQ(+Piece::WHITE_KNIGHT, board.board[Square::valueOf(File::g, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_KNIGHT, board.board[Square::valueOf(File::b, Rank::r8)]);
  EXPECT_EQ(+Piece::BLACK_KNIGHT, board.board[Square::valueOf(File::g, Rank::r8)]);

  // Test bishops
  EXPECT_EQ(+Piece::WHITE_BISHOP, board.board[Square::valueOf(File::c, Rank::r1)]);
  EXPECT_EQ(+Piece::WHITE_BISHOP, board.board[Square::valueOf(File::f, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_BISHOP, board.board[Square::valueOf(File::c, Rank::r8)]);
  EXPECT_EQ(+Piece::BLACK_BISHOP, board.board[Square::valueOf(File::f, Rank::r8)]);

  // Test rooks
  EXPECT_EQ(+Piece::WHITE_ROOK, board.board[Square::valueOf(File::a, Rank::r1)]);
  EXPECT_EQ(+Piece::WHITE_ROOK, board.board[Square::valueOf(File::h, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_ROOK, board.board[Square::valueOf(File::a, Rank::r8)]);
  EXPECT_EQ(+Piece::BLACK_ROOK, board.board[Square::valueOf(File::h, Rank::r8)]);

  // Test queens
  EXPECT_EQ(+Piece::WHITE_QUEEN, board.board[Square::valueOf(File::d, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_QUEEN, board.board[Square::valueOf(File::d, Rank::r8)]);

  // Test kings
  EXPECT_EQ(+Piece::WHITE_KING, board.board[Square::valueOf(File::e, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_KING, board.board[Square::valueOf(File::e, Rank::r8)]);

  EXPECT_EQ(8 * PieceType::PAWN_VALUE
    + 2 * PieceType::KNIGHT_VALUE
    + 2 * PieceType::BISHOP_VALUE
    + 2 * PieceType::ROOK_VALUE
    + PieceType::QUEEN_VALUE
    + PieceType::KING_VALUE,
    board.material[Color::WHITE]);
  EXPECT_EQ(8 * PieceType::PAWN_VALUE
    + 2 * PieceType::KNIGHT_VALUE
    + 2 * PieceType::BISHOP_VALUE
    + 2 * PieceType::ROOK_VALUE
    + PieceType::QUEEN_VALUE
    + PieceType::KING_VALUE,
    board.material[Color::BLACK]);

  // Test castling
  EXPECT_EQ(+File::h, board.castlingRights[Castling::WHITE_KINGSIDE]);
  EXPECT_EQ(+File::a, board.castlingRights[Castling::WHITE_QUEENSIDE]);
  EXPECT_EQ(+File::h, board.castlingRights[Castling::BLACK_KINGSIDE]);
  EXPECT_EQ(+File::a, board.castlingRights[Castling::BLACK_QUEENSIDE]);

  // Test en passant
  EXPECT_EQ(+Square::NOSQUARE, board.enPassantSquare);

  // Test active color
  EXPECT_EQ(+Color::WHITE, board.activeColor);

  // Test half move clock
  EXPECT_EQ(0, board.halfmoveClock);

  // Test full move number
  EXPECT_EQ(1, board.getFullmoveNumber());
}

TEST(boardtest, testConstructorFromFen) {
  Board board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

  // Test pawns
  for (auto file : File::values) {
    EXPECT_EQ(+Piece::WHITE_PAWN, board.board[Square::valueOf(file, Rank::r2)]);
    EXPECT_EQ(+Piece::BLACK_PAWN, board.board[Square::valueOf(file, Rank::r7)]);
  }

  // Test knights
  EXPECT_EQ(+Piece::WHITE_KNIGHT, board.board[Square::valueOf(File::b, Rank::r1)]);
  EXPECT_EQ(+Piece::WHITE_KNIGHT, board.board[Square::valueOf(File::g, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_KNIGHT, board.board[Square::valueOf(File::b, Rank::r8)]);
  EXPECT_EQ(+Piece::BLACK_KNIGHT, board.board[Square::valueOf(File::g, Rank::r8)]);

  // Test bishops
  EXPECT_EQ(+Piece::WHITE_BISHOP, board.board[Square::valueOf(File::c, Rank::r1)]);
  EXPECT_EQ(+Piece::WHITE_BISHOP, board.board[Square::valueOf(File::f, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_BISHOP, board.board[Square::valueOf(File::c, Rank::r8)]);
  EXPECT_EQ(+Piece::BLACK_BISHOP, board.board[Square::valueOf(File::f, Rank::r8)]);

  // Test rooks
  EXPECT_EQ(+Piece::WHITE_ROOK, board.board[Square::valueOf(File::a, Rank::r1)]);
  EXPECT_EQ(+Piece::WHITE_ROOK, board.board[Square::valueOf(File::h, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_ROOK, board.board[Square::valueOf(File::a, Rank::r8)]);
  EXPECT_EQ(+Piece::BLACK_ROOK, board.board[Square::valueOf(File::h, Rank::r8)]);

  // Test queens
  EXPECT_EQ(+Piece::WHITE_QUEEN, board.board[Square::valueOf(File::d, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_QUEEN, board.board[Square::valueOf(File::d, Rank::r8)]);

  // Test kings
  EXPECT_EQ(+Piece::WHITE_KING, board.board[Square::valueOf(File::e, Rank::r1)]);
  EXPECT_EQ(+Piece::BLACK_KING, board.board[Square::valueOf(File::e, Rank::r8)]);

  EXPECT_EQ(8 * PieceType::PAWN_VALUE
    + 2 * PieceType::KNIGHT_VALUE
    + 2 * PieceType::BISHOP_VALUE
    + 2 * PieceType::ROOK_VALUE
    + PieceType::QUEEN_VALUE
    + PieceType::KING_VALUE,
    board.material[Color::WHITE]);
  EXPECT_EQ(8 * PieceType::PAWN_VALUE
    + 2 * PieceType::KNIGHT_VALUE
    + 2 * PieceType::BISHOP_VALUE
    + 2 * PieceType::ROOK_VALUE
    + PieceType::QUEEN_VALUE
    + PieceType::KING_VALUE,
    board.material[Color::BLACK]);

  // Test castling
  EXPECT_EQ(+File::h, board.castlingRights[Castling::WHITE_KINGSIDE]);
  EXPECT_EQ(+File::a, board.castlingRights[Castling::WHITE_QUEENSIDE]);
  EXPECT_EQ(+File::h, board.castlingRights[Castling::BLACK_KINGSIDE]);
  EXPECT_EQ(+File::a, board.castlingRights[Castling::BLACK_QUEENSIDE]);

  // Test en passant
  EXPECT_EQ(+Square::NOSQUARE, board.enPassantSquare);

  // Test active color
  EXPECT_EQ(+Color::WHITE, board.activeColor);

  // Test half move clock
  EXPECT_EQ(0, board.halfmoveClock);

  // Test full move number
  EXPECT_EQ(1, board.getFullmoveNumber());
}

TEST(boardtest, testEquals) {
  // Standard board test
  Board board1(Board::STANDARDBOARD);
  Board board2(Board::STANDARDBOARD);

  // reflexive test
  EXPECT_EQ(board1, board1);

  // symmetric test
  EXPECT_EQ(board1, board2);
  EXPECT_EQ(board2, board1);

  // FEN test
  Board board3("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
  EXPECT_EQ(board1, board3);

  Board board4("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
  Board board5("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
  Board board6("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1");
  Board board7("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1 1");
  Board board8("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 2");

  EXPECT_NE(board1, board4);
  EXPECT_NE(board1, board5);
  EXPECT_NE(board1, board6);
  EXPECT_NE(board1, board7);
  EXPECT_NE(board1, board8);
}

TEST(boardtest, testToString) {
  std::string fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

  Board board(fen);

  EXPECT_EQ(fen, board.toString());
}

TEST(boardtest, testActiveColor) {
  Board board(Board::STANDARDBOARD);

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
  Board board(Board::STANDARDBOARD);

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
  Board board(Board::STANDARDBOARD);

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
  Board board(Board::STANDARDBOARD);

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
  Board board("8/4k3/8/8/8/8/2K5/8 w - - 0 1");
  EXPECT_TRUE(board.hasInsufficientMaterial());

  board = Board("8/4k3/8/2B5/8/8/2K5/8 b - - 0 1");
  EXPECT_TRUE(board.hasInsufficientMaterial());

  board = Board("8/4k3/8/2B3n1/8/8/2K5/8 b - - 0 1");
  EXPECT_TRUE(board.hasInsufficientMaterial());
}

TEST(boardtest, testNormalMove) {
  Board standardBoard(Board::STANDARDBOARD);
  Board board(Board::STANDARDBOARD);
  uint64_t zobristKey = board.zobristKey;

  int move = Move::valueOf(MoveType::NORMAL, Square::a2, Square::a3, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);
  board.undoMove(move);

  EXPECT_EQ(standardBoard, board);
  EXPECT_EQ(zobristKey, board.zobristKey);
}

TEST(boardtest, testPawnDoubleMove) {
  Board standardBoard(Board::STANDARDBOARD);
  Board board(Board::STANDARDBOARD);
  uint64_t zobristKey = board.zobristKey;

  int move = Move::valueOf(MoveType::PAWNDOUBLE, Square::a2, Square::a4, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  EXPECT_EQ(+Square::a3, board.enPassantSquare);

  board.undoMove(move);

  EXPECT_EQ(standardBoard, board);
  EXPECT_EQ(zobristKey, board.zobristKey);
}

TEST(boardtest, testPawnPromotionMove) {
  Board board("8/P5k1/8/8/2K5/8/8/8 w - - 0 1");
  uint64_t zobristKey = board.zobristKey;

  int move = Move::valueOf(MoveType::PAWNPROMOTION, Square::a7, Square::a8, Piece::WHITE_PAWN, Piece::NOPIECE, PieceType::QUEEN);
  board.makeMove(move);

  EXPECT_EQ(+Piece::WHITE_QUEEN, board.board[Square::a8]);

  board.undoMove(move);

  EXPECT_EQ("8/P5k1/8/8/2K5/8/8/8 w - - 0 1", board.toString());
  EXPECT_EQ(zobristKey, board.zobristKey);
}

TEST(boardtest, testEnPassantMove) {
  Board board("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1");
  uint64_t zobristKey = board.zobristKey;

  // Make en passant move
  int move = Move::valueOf(MoveType::ENPASSANT, Square::e4, Square::d3, Piece::BLACK_PAWN, Piece::WHITE_PAWN, PieceType::NOPIECETYPE);
  board.makeMove(move);

  EXPECT_EQ(+Piece::NOPIECE, board.board[Square::d4]);
  EXPECT_EQ(+Piece::BLACK_PAWN, board.board[Square::d3]);
  EXPECT_EQ(+Square::NOSQUARE, board.enPassantSquare);

  board.undoMove(move);

  EXPECT_EQ("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1", board.toString());
  EXPECT_EQ(zobristKey, board.zobristKey);
}

TEST(boardtest, testCastlingMove) {
  Board board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
  uint64_t zobristKey = board.zobristKey;

  int move = Move::valueOf(MoveType::CASTLING, Square::e1, Square::c1, Piece::WHITE_KING, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  EXPECT_EQ(+File::NOFILE, board.castlingRights[Castling::WHITE_QUEENSIDE]);

  board.undoMove(move);

  EXPECT_EQ("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", board.toString());
  EXPECT_EQ(zobristKey, board.zobristKey);

  board = Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
  zobristKey = board.zobristKey;

  move = Move::valueOf(MoveType::CASTLING, Square::e1, Square::g1, Piece::WHITE_KING, Piece::NOPIECE, PieceType::NOPIECETYPE);
  board.makeMove(move);

  EXPECT_EQ(+File::NOFILE, board.castlingRights[Castling::WHITE_KINGSIDE]);

  board.undoMove(move);

  EXPECT_EQ("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", board.toString());
  EXPECT_EQ(zobristKey, board.zobristKey);
}
