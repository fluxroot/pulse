/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BoardTest {

  @Test
  public void testConstructor() {
    // Setup a new board
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);

    // Test pieces setup
    for (GenericPosition genericPosition : GenericPosition.values()) {
      GenericPiece genericPiece = genericBoard.getPiece(genericPosition);
      int piece = board.board[Square.valueOf(genericPosition)];
      if (genericPiece == null) {
        assertEquals(Piece.NOPIECE, piece);
      } else {
        assertEquals(genericPiece, Piece.toGenericPiece(piece));
      }
    }

    assertEquals(8 * Evaluation.PAWN_VALUE
        + 2 * Evaluation.KNIGHT_VALUE
        + 2 * Evaluation.BISHOP_VALUE
        + 2 * Evaluation.ROOK_VALUE
        + Evaluation.QUEEN_VALUE
        + Evaluation.KING_VALUE,
        board.material[Color.WHITE]);
    assertEquals(8 * Evaluation.PAWN_VALUE
        + 2 * Evaluation.KNIGHT_VALUE
        + 2 * Evaluation.BISHOP_VALUE
        + 2 * Evaluation.ROOK_VALUE
        + Evaluation.QUEEN_VALUE
        + Evaluation.KING_VALUE,
        board.material[Color.BLACK]);

    // Test castling
    for (int color : Color.values) {
      for (int castlingType : CastlingType.values) {
        GenericFile genericFile = genericBoard.getCastling(Color.toGenericColor(color), CastlingType.toGenericCastling(castlingType));
        int file = board.castlingRights[Castling.valueOf(color, castlingType)];
        if (genericFile == null) {
          assertEquals(File.NOFILE, file);
        } else {
          assertEquals(genericFile, File.toGenericFile(file));
        }
      }
    }

    // Test en passant
    if (genericBoard.getEnPassant() == null) {
      assertEquals(Square.NOSQUARE, board.enPassantSquare);
    } else {
      assertEquals(genericBoard.getEnPassant(), Square.toGenericPosition(board.enPassantSquare));
    }

    // Test active color
    assertEquals(genericBoard.getActiveColor(), Color.toGenericColor(board.activeColor));

    // Test half move clock
    assertEquals(genericBoard.getHalfMoveClock(), board.halfmoveClock);

    // Test full move number
    assertEquals(genericBoard.getFullMoveNumber(), board.getFullmoveNumber());
  }

  @Test
  public void testToGenericBoard() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);

    assertEquals(genericBoard, board.toGenericBoard());
  }

  @Test
  public void testToString() throws IllegalNotationException {
    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    GenericBoard genericBoard = new GenericBoard(fen);
    Board board = new Board(genericBoard);

    assertEquals(fen, board.toString());
  }

  @Test
  public void testActiveColor() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);

    // Move white pawn
    int move = Move.valueOf(Move.Type.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(Color.BLACK, board.activeColor);

    // Move black pawn
    move = Move.valueOf(Move.Type.NORMAL, Square.b7, Square.b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(Color.WHITE, board.activeColor);
  }

  @Test
  public void testHalfMoveClock() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);

    // Move white pawn
    int move = Move.valueOf(Move.Type.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(0, board.halfmoveClock);

    // Move black pawn
    move = Move.valueOf(Move.Type.NORMAL, Square.b7, Square.b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    // Move white knight
    move = Move.valueOf(Move.Type.NORMAL, Square.b1, Square.c3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(1, board.halfmoveClock);
  }

  @Test
  public void testFullMoveNumber() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);

    // Move white pawn
    int move = Move.valueOf(Move.Type.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(1, board.getFullmoveNumber());

    // Move black pawn
    move = Move.valueOf(Move.Type.NORMAL, Square.b7, Square.b6, Piece.BLACK_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    assertEquals(2, board.getFullmoveNumber());
  }

  @Test
  public void testIsRepetition() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);

    // Move white knight
    int move = Move.valueOf(Move.Type.NORMAL, Square.b1, Square.c3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    // Move black knight
    move = Move.valueOf(Move.Type.NORMAL, Square.b8, Square.c6, Piece.BLACK_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    // Move white knight
    move = Move.valueOf(Move.Type.NORMAL, Square.g1, Square.f3, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    // Move black knight
    move = Move.valueOf(Move.Type.NORMAL, Square.c6, Square.b8, Piece.BLACK_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    // Move white knight
    move = Move.valueOf(Move.Type.NORMAL, Square.f3, Square.g1, Piece.WHITE_KNIGHT, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    assertTrue(board.isRepetition());
  }

  @Test
  public void testHasInsufficientMaterial() throws IllegalNotationException {
    GenericBoard genericBoard = new GenericBoard("8/4k3/8/8/8/8/2K5/8 w - - 0 1");
    Board board = new Board(genericBoard);
    assertTrue(board.hasInsufficientMaterial());

    genericBoard = new GenericBoard("8/4k3/8/2B5/8/8/2K5/8 b - - 0 1");
    board = new Board(genericBoard);
    assertTrue(board.hasInsufficientMaterial());

    genericBoard = new GenericBoard("8/4k3/8/2B3n1/8/8/2K5/8 b - - 0 1");
    board = new Board(genericBoard);
    assertTrue(board.hasInsufficientMaterial());
  }

  @Test
  public void testNormalMove() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);
    long zobristKey = board.zobristKey;

    int move = Move.valueOf(Move.Type.NORMAL, Square.a2, Square.a3, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);
    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristKey, board.zobristKey);
  }

  @Test
  public void testPawnDoubleMove() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);
    long zobristKey = board.zobristKey;

    int move = Move.valueOf(Move.Type.PAWNDOUBLE, Square.a2, Square.a4, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    assertEquals(Square.a3, board.enPassantSquare);

    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristKey, board.zobristKey);
  }

  @Test
  public void testPawnPromotionMove() throws IllegalNotationException {
    GenericBoard genericBoard = new GenericBoard("8/P5k1/8/8/2K5/8/8/8 w - - 0 1");
    Board board = new Board(genericBoard);
    long zobristKey = board.zobristKey;

    int move = Move.valueOf(Move.Type.PAWNPROMOTION, Square.a7, Square.a8, Piece.WHITE_PAWN, Piece.NOPIECE, PieceType.QUEEN);
    board.makeMove(move);

    assertEquals(Piece.WHITE_QUEEN, board.board[Square.a8]);

    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristKey, board.zobristKey);
  }

  @Test
  public void testEnPassantMove() throws IllegalNotationException {
    GenericBoard genericBoard = new GenericBoard("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1");
    Board board = new Board(genericBoard);
    long zobristKey = board.zobristKey;

    // Make en passant move
    int move = Move.valueOf(Move.Type.ENPASSANT, Square.e4, Square.d3, Piece.BLACK_PAWN, Piece.WHITE_PAWN, PieceType.NOPIECETYPE);
    board.makeMove(move);

    assertEquals(Piece.NOPIECE, board.board[Square.d4]);
    assertEquals(Piece.BLACK_PAWN, board.board[Square.d3]);
    assertEquals(Square.NOSQUARE, board.enPassantSquare);

    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristKey, board.zobristKey);
  }

  @Test
  public void testCastlingMove() throws IllegalNotationException {
    GenericBoard genericBoard = new GenericBoard("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
    Board board = new Board(genericBoard);
    long zobristKey = board.zobristKey;

    int move = Move.valueOf(Move.Type.CASTLING, Square.e1, Square.c1, Piece.WHITE_KING, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    assertEquals(File.NOFILE, board.castlingRights[Castling.WHITE_QUEENSIDE]);

    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristKey, board.zobristKey);

    genericBoard = new GenericBoard("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
    board = new Board(genericBoard);
    zobristKey = board.zobristKey;

    move = Move.valueOf(Move.Type.CASTLING, Square.e1, Square.g1, Piece.WHITE_KING, Piece.NOPIECE, PieceType.NOPIECETYPE);
    board.makeMove(move);

    assertEquals(File.NOFILE, board.castlingRights[Castling.WHITE_KINGSIDE]);

    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristKey, board.zobristKey);
  }

}
