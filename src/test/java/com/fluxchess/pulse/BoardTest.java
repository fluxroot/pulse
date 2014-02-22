/*
 * Copyright 2013-2014 the original author or authors.
 *
 * This file is part of Pulse Chess.
 *
 * Pulse Chess is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pulse Chess is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pulse Chess.  If not, see <http://www.gnu.org/licenses/>.
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

    // Test castling
    for (GenericColor genericColor : GenericColor.values()) {
      for (GenericCastling genericCastling : GenericCastling.values()) {
        GenericFile genericFile = genericBoard.getCastling(genericColor, genericCastling);
        int file = board.colorCastling[Color.valueOf(genericColor)][Castling.valueOf(genericCastling)];
        if (genericFile == null) {
          assertEquals(File.NOFILE, file);
        } else {
          assertEquals(genericFile, File.toGenericFile(file));
        }
      }
    }

    // Test en passant
    if (genericBoard.getEnPassant() == null) {
      assertEquals(Square.NOSQUARE, board.enPassant);
    } else {
      assertEquals(genericBoard.getEnPassant(), Square.toGenericPosition(board.enPassant));
    }

    // Test active color
    assertEquals(genericBoard.getActiveColor(), Color.toGenericColor(board.activeColor));

    // Test half move clock
    assertEquals(genericBoard.getHalfMoveClock(), board.halfMoveClock);

    // Test full move number
    assertEquals(genericBoard.getFullMoveNumber(), board.getFullMoveNumber());
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
    int move = Move.valueOf(Move.Type.NORMAL, Square.a2, Square.a3, Piece.WHITEPAWN, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);
    assertEquals(Color.BLACK, board.activeColor);

    // Move black pawn
    move = Move.valueOf(Move.Type.NORMAL, Square.b7, Square.b6, Piece.BLACKPAWN, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);
    assertEquals(Color.WHITE, board.activeColor);
  }

  @Test
  public void testHalfMoveClock() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);

    // Move white pawn
    int move = Move.valueOf(Move.Type.NORMAL, Square.a2, Square.a3, Piece.WHITEPAWN, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);
    assertEquals(0, board.halfMoveClock);

    // Move black pawn
    move = Move.valueOf(Move.Type.NORMAL, Square.b7, Square.b6, Piece.BLACKPAWN, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);

    // Move white knight
    move = Move.valueOf(Move.Type.NORMAL, Square.b1, Square.c3, Piece.WHITEKNIGHT, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);
    assertEquals(1, board.halfMoveClock);
  }

  @Test
  public void testFullMoveNumber() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);

    // Move white pawn
    int move = Move.valueOf(Move.Type.NORMAL, Square.a2, Square.a3, Piece.WHITEPAWN, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);
    assertEquals(1, board.getFullMoveNumber());

    // Move black pawn
    move = Move.valueOf(Move.Type.NORMAL, Square.b7, Square.b6, Piece.BLACKPAWN, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);
    assertEquals(2, board.getFullMoveNumber());
  }

  @Test
  public void testIsRepetition() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);

    // Move white knight
    int move = Move.valueOf(Move.Type.NORMAL, Square.b1, Square.c3, Piece.WHITEKNIGHT, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);

    // Move black knight
    move = Move.valueOf(Move.Type.NORMAL, Square.b8, Square.c6, Piece.BLACKKNIGHT, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);

    // Move white knight
    move = Move.valueOf(Move.Type.NORMAL, Square.g1, Square.f3, Piece.WHITEKNIGHT, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);

    // Move black knight
    move = Move.valueOf(Move.Type.NORMAL, Square.c6, Square.b8, Piece.BLACKKNIGHT, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);

    // Move white knight
    move = Move.valueOf(Move.Type.NORMAL, Square.f3, Square.g1, Piece.WHITEKNIGHT, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);

    assertTrue(board.isRepetition());
  }

  @Test
  public void testNormalMove() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);
    long zobristCode = board.zobristCode;

    int move = Move.valueOf(Move.Type.NORMAL, Square.a2, Square.a3, Piece.WHITEPAWN, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);
    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristCode, board.zobristCode);
  }

  @Test
  public void testPawnDoubleMove() {
    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);
    long zobristCode = board.zobristCode;

    int move = Move.valueOf(Move.Type.PAWNDOUBLE, Square.a2, Square.a4, Piece.WHITEPAWN, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);

    assertEquals(Square.a3, board.enPassant);

    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristCode, board.zobristCode);
  }

  @Test
  public void testPawnPromotionMove() throws IllegalNotationException {
    GenericBoard genericBoard = new GenericBoard("8/P5k1/8/8/2K5/8/8/8 w - - 0 1");
    Board board = new Board(genericBoard);
    long zobristCode = board.zobristCode;

    int move = Move.valueOf(Move.Type.PAWNPROMOTION, Square.a7, Square.a8, Piece.WHITEPAWN, Piece.NOPIECE, Piece.Type.QUEEN);
    board.makeMove(move);

    assertEquals(Piece.WHITEQUEEN, board.board[Square.a8]);

    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristCode, board.zobristCode);
  }

  @Test
  public void testEnPassantMove() throws IllegalNotationException {
    GenericBoard genericBoard = new GenericBoard("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1");
    Board board = new Board(genericBoard);
    long zobristCode = board.zobristCode;

    // Make en passant move
    int move = Move.valueOf(Move.Type.ENPASSANT, Square.e4, Square.d3, Piece.BLACKPAWN, Piece.WHITEPAWN, Piece.Type.NOTYPE);
    board.makeMove(move);

    assertEquals(Piece.NOPIECE, board.board[Square.d4]);
    assertEquals(Piece.BLACKPAWN, board.board[Square.d3]);
    assertEquals(Square.NOSQUARE, board.enPassant);

    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristCode, board.zobristCode);
  }

  @Test
  public void testCastlingMove() throws IllegalNotationException {
    GenericBoard genericBoard = new GenericBoard("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
    Board board = new Board(genericBoard);
    long zobristCode = board.zobristCode;

    int move = Move.valueOf(Move.Type.CASTLING, Square.e1, Square.c1, Piece.WHITEKING, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);

    assertEquals(File.NOFILE, board.colorCastling[Color.WHITE][Castling.QUEENSIDE]);

    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristCode, board.zobristCode);

    genericBoard = new GenericBoard("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
    board = new Board(genericBoard);
    zobristCode = board.zobristCode;

    move = Move.valueOf(Move.Type.CASTLING, Square.e1, Square.g1, Piece.WHITEKING, Piece.NOPIECE, Piece.Type.NOTYPE);
    board.makeMove(move);

    assertEquals(File.NOFILE, board.colorCastling[Color.WHITE][Castling.KINGSIDE]);

    board.undoMove(move);

    assertEquals(genericBoard, board.toGenericBoard());
    assertEquals(zobristCode, board.zobristCode);
  }

}
