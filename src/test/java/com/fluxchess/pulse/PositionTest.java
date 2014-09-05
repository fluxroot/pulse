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

public class PositionTest {

  @Test
  public void testActiveColor() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);

    // Move white pawn
    int move = Move.valueOf(MoveType.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);
    assertEquals(Color.BLACK, position.activeColor);

    // Move black pawn
    move = Move.valueOf(MoveType.NORMAL, Square.b7, Square.b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);
    assertEquals(Color.WHITE, position.activeColor);
  }

  @Test
  public void testHalfMoveClock() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);

    // Move white pawn
    int move = Move.valueOf(MoveType.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);
    assertEquals(0, position.halfmoveClock);

    // Move black pawn
    move = Move.valueOf(MoveType.NORMAL, Square.b7, Square.b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);

    // Move white knight
    move = Move.valueOf(MoveType.NORMAL, Square.b1, Square.c3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);
    assertEquals(1, position.halfmoveClock);
  }

  @Test
  public void testFullMoveNumber() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);

    // Move white pawn
    int move = Move.valueOf(MoveType.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);
    assertEquals(1, position.getFullmoveNumber());

    // Move black pawn
    move = Move.valueOf(MoveType.NORMAL, Square.b7, Square.b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);
    assertEquals(2, position.getFullmoveNumber());
  }

  @Test
  public void testIsRepetition() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);

    // Move white knight
    int move = Move.valueOf(MoveType.NORMAL, Square.b1, Square.c3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);

    // Move black knight
    move = Move.valueOf(MoveType.NORMAL, Square.b8, Square.c6, Piece.BLACK_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);

    // Move white knight
    move = Move.valueOf(MoveType.NORMAL, Square.g1, Square.f3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);

    // Move black knight
    move = Move.valueOf(MoveType.NORMAL, Square.c6, Square.b8, Piece.BLACK_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);

    // Move white knight
    move = Move.valueOf(MoveType.NORMAL, Square.f3, Square.g1, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);

    assertTrue(position.isRepetition());
  }

  @Test
  public void testHasInsufficientMaterial() {
    Position position = Notation.toPosition("8/4k3/8/8/8/8/2K5/8 w - - 0 1");
    assertTrue(position.hasInsufficientMaterial());

    position = Notation.toPosition("8/4k3/8/2B5/8/8/2K5/8 b - - 0 1");
    assertTrue(position.hasInsufficientMaterial());

    position = Notation.toPosition("8/4k3/8/2B3n1/8/8/2K5/8 b - - 0 1");
    assertTrue(position.hasInsufficientMaterial());
  }

  @Test
  public void testNormalMove() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);
    long zobristKey = position.zobristKey;

    int move = Move.valueOf(MoveType.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);
    position.undoMove(move);

    assertEquals(Notation.STANDARDPOSITION, Notation.fromPosition(position));
    assertEquals(zobristKey, position.zobristKey);
  }

  @Test
  public void testPawnDoubleMove() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);
    long zobristKey = position.zobristKey;

    int move = Move.valueOf(MoveType.PAWNDOUBLE, Square.a2, Square.a4, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);

    assertEquals(Square.a3, position.enPassantSquare);

    position.undoMove(move);

    assertEquals(Notation.STANDARDPOSITION, Notation.fromPosition(position));
    assertEquals(zobristKey, position.zobristKey);
  }

  @Test
  public void testPawnPromotionMove() {
    Position position = Notation.toPosition("8/P5k1/8/8/2K5/8/8/8 w - - 0 1");
    long zobristKey = position.zobristKey;

    int move = Move.valueOf(MoveType.PAWNPROMOTION, Square.a7, Square.a8, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.QUEEN);
    position.makeMove(move);

    assertEquals(Piece.WHITE_QUEEN, position.board[Square.a8]);

    position.undoMove(move);

    assertEquals("8/P5k1/8/8/2K5/8/8/8 w - - 0 1", Notation.fromPosition(position));
    assertEquals(zobristKey, position.zobristKey);
  }

  @Test
  public void testEnPassantMove() {
    Position position = Notation.toPosition("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1");
    long zobristKey = position.zobristKey;

    // Make en passant move
    int move = Move.valueOf(MoveType.ENPASSANT, Square.e4, Square.d3, Piece.BLACK_PAWN, Piece.WHITE_PAWN, PieceType.NOPIECETYPE);
    position.makeMove(move);

    assertEquals(Piece.NOPIECE, position.board[Square.d4]);
    assertEquals(Piece.BLACK_PAWN, position.board[Square.d3]);
    assertEquals(Square.NOSQUARE, position.enPassantSquare);

    position.undoMove(move);

    assertEquals("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1", Notation.fromPosition(position));
    assertEquals(zobristKey, position.zobristKey);
  }

  @Test
  public void testCastlingMove() {
    Position position = Notation.toPosition("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
    long zobristKey = position.zobristKey;

    int move = Move.valueOf(MoveType.CASTLING, Square.e1, Square.c1, Piece.WHITE_KING, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);

    assertEquals(File.NOFILE, position.castlingRights[Castling.WHITE_QUEENSIDE]);

    position.undoMove(move);

    assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", Notation.fromPosition(position));
    assertEquals(zobristKey, position.zobristKey);

    position = Notation.toPosition("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
    zobristKey = position.zobristKey;

    move = Move.valueOf(MoveType.CASTLING, Square.e1, Square.g1, Piece.WHITE_KING, Piece.NOPIECE, PieceType.NOPIECETYPE);
    position.makeMove(move);

    assertEquals(File.NOFILE, position.castlingRights[Castling.WHITE_KINGSIDE]);

    position.undoMove(move);

    assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", Notation.fromPosition(position));
    assertEquals(zobristKey, position.zobristKey);
  }

}
