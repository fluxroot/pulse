/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BoardTest {

  @Test
  public void testActiveColor() {
    Board board = Notation.toBoard(Notation.STANDARDBOARD);

    // Move white pawn
    int move = Move.valueOf(MoveType.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(Color.BLACK, board.activeColor);

    // Move black pawn
    move = Move.valueOf(MoveType.NORMAL, Square.b7, Square.b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(Color.WHITE, board.activeColor);
  }

  @Test
  public void testHalfMoveClock() {
    Board board = Notation.toBoard(Notation.STANDARDBOARD);

    // Move white pawn
    int move = Move.valueOf(MoveType.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(0, board.halfmoveClock);

    // Move black pawn
    move = Move.valueOf(MoveType.NORMAL, Square.b7, Square.b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    // Move white knight
    move = Move.valueOf(MoveType.NORMAL, Square.b1, Square.c3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(1, board.halfmoveClock);
  }

  @Test
  public void testFullMoveNumber() {
    Board board = Notation.toBoard(Notation.STANDARDBOARD);

    // Move white pawn
    int move = Move.valueOf(MoveType.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(1, board.getFullmoveNumber());

    // Move black pawn
    move = Move.valueOf(MoveType.NORMAL, Square.b7, Square.b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(2, board.getFullmoveNumber());
  }

  @Test
  public void testIsRepetition() {
    Board board = Notation.toBoard(Notation.STANDARDBOARD);

    // Move white knight
    int move = Move.valueOf(MoveType.NORMAL, Square.b1, Square.c3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    // Move black knight
    move = Move.valueOf(MoveType.NORMAL, Square.b8, Square.c6, Piece.BLACK_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    // Move white knight
    move = Move.valueOf(MoveType.NORMAL, Square.g1, Square.f3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    // Move black knight
    move = Move.valueOf(MoveType.NORMAL, Square.c6, Square.b8, Piece.BLACK_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    // Move white knight
    move = Move.valueOf(MoveType.NORMAL, Square.f3, Square.g1, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    assertTrue(board.isRepetition());
  }

  @Test
  public void testHasInsufficientMaterial() {
    Board board = Notation.toBoard("8/4k3/8/8/8/8/2K5/8 w - - 0 1");
    assertTrue(board.hasInsufficientMaterial());

    board = Notation.toBoard("8/4k3/8/2B5/8/8/2K5/8 b - - 0 1");
    assertTrue(board.hasInsufficientMaterial());

    board = Notation.toBoard("8/4k3/8/2B3n1/8/8/2K5/8 b - - 0 1");
    assertTrue(board.hasInsufficientMaterial());
  }

  @Test
  public void testNormalMove() {
    Board board = Notation.toBoard(Notation.STANDARDBOARD);
    long zobristKey = board.zobristKey;

    int move = Move.valueOf(MoveType.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    board.undoMove(move);

    assertEquals(Notation.STANDARDBOARD, Notation.fromBoard(board));
    assertEquals(zobristKey, board.zobristKey);
  }

  @Test
  public void testPawnDoubleMove() {
    Board board = Notation.toBoard(Notation.STANDARDBOARD);
    long zobristKey = board.zobristKey;

    int move = Move.valueOf(MoveType.PAWNDOUBLE, Square.a2, Square.a4, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    assertEquals(Square.a3, board.enPassantSquare);

    board.undoMove(move);

    assertEquals(Notation.STANDARDBOARD, Notation.fromBoard(board));
    assertEquals(zobristKey, board.zobristKey);
  }

  @Test
  public void testPawnPromotionMove() {
    Board board = Notation.toBoard("8/P5k1/8/8/2K5/8/8/8 w - - 0 1");
    long zobristKey = board.zobristKey;

    int move = Move.valueOf(MoveType.PAWNPROMOTION, Square.a7, Square.a8, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.QUEEN);
    board.makeMove(move);

    assertEquals(Piece.WHITE_QUEEN, board.board[Square.a8]);

    board.undoMove(move);

    assertEquals("8/P5k1/8/8/2K5/8/8/8 w - - 0 1", Notation.fromBoard(board));
    assertEquals(zobristKey, board.zobristKey);
  }

  @Test
  public void testEnPassantMove() {
    Board board = Notation.toBoard("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1");
    long zobristKey = board.zobristKey;

    // Make en passant move
    int move = Move.valueOf(MoveType.ENPASSANT, Square.e4, Square.d3, Piece.BLACK_PAWN, Piece.WHITE_PAWN, PieceType.NOPIECETYPE);
    board.makeMove(move);

    assertEquals(Piece.NOPIECE, board.board[Square.d4]);
    assertEquals(Piece.BLACK_PAWN, board.board[Square.d3]);
    assertEquals(Square.NOSQUARE, board.enPassantSquare);

    board.undoMove(move);

    assertEquals("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1", Notation.fromBoard(board));
    assertEquals(zobristKey, board.zobristKey);
  }

  @Test
  public void testCastlingMove() {
    Board board = Notation.toBoard("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
    long zobristKey = board.zobristKey;

    int move = Move.valueOf(MoveType.CASTLING, Square.e1, Square.c1, Piece.WHITE_KING, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    assertEquals(File.NOFILE, board.castlingRights[Castling.WHITE_QUEENSIDE]);

    board.undoMove(move);

    assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", Notation.fromBoard(board));
    assertEquals(zobristKey, board.zobristKey);

    board = Notation.toBoard("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
    zobristKey = board.zobristKey;

    move = Move.valueOf(MoveType.CASTLING, Square.e1, Square.g1, Piece.WHITE_KING, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    assertEquals(File.NOFILE, board.castlingRights[Castling.WHITE_KINGSIDE]);

    board.undoMove(move);

    assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", Notation.fromBoard(board));
    assertEquals(zobristKey, board.zobristKey);
  }

}
