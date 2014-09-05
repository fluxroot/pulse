/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotationTest {

  @Test
  public void testStandardPosition() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);

    // Test pawns
    for (int file : File.values) {
      assertEquals(Piece.WHITE_PAWN, position.board[Square.valueOf(file, Rank.r2)]);
      assertEquals(Piece.BLACK_PAWN, position.board[Square.valueOf(file, Rank.r7)]);
    }

    // Test knights
    assertEquals(Piece.WHITE_KNIGHT, position.board[Square.valueOf(File.b, Rank.r1)]);
    assertEquals(Piece.WHITE_KNIGHT, position.board[Square.valueOf(File.g, Rank.r1)]);
    assertEquals(Piece.BLACK_KNIGHT, position.board[Square.valueOf(File.b, Rank.r8)]);
    assertEquals(Piece.BLACK_KNIGHT, position.board[Square.valueOf(File.g, Rank.r8)]);

    // Test bishops
    assertEquals(Piece.WHITE_BISHOP, position.board[Square.valueOf(File.c, Rank.r1)]);
    assertEquals(Piece.WHITE_BISHOP, position.board[Square.valueOf(File.f, Rank.r1)]);
    assertEquals(Piece.BLACK_BISHOP, position.board[Square.valueOf(File.c, Rank.r8)]);
    assertEquals(Piece.BLACK_BISHOP, position.board[Square.valueOf(File.f, Rank.r8)]);

    // Test rooks
    assertEquals(Piece.WHITE_ROOK, position.board[Square.valueOf(File.a, Rank.r1)]);
    assertEquals(Piece.WHITE_ROOK, position.board[Square.valueOf(File.h, Rank.r1)]);
    assertEquals(Piece.BLACK_ROOK, position.board[Square.valueOf(File.a, Rank.r8)]);
    assertEquals(Piece.BLACK_ROOK, position.board[Square.valueOf(File.h, Rank.r8)]);

    // Test queens
    assertEquals(Piece.WHITE_QUEEN, position.board[Square.valueOf(File.d, Rank.r1)]);
    assertEquals(Piece.BLACK_QUEEN, position.board[Square.valueOf(File.d, Rank.r8)]);

    // Test kings
    assertEquals(Piece.WHITE_KING, position.board[Square.valueOf(File.e, Rank.r1)]);
    assertEquals(Piece.BLACK_KING, position.board[Square.valueOf(File.e, Rank.r8)]);

    assertEquals(8 * PieceType.PAWN_VALUE
            + 2 * PieceType.KNIGHT_VALUE
            + 2 * PieceType.BISHOP_VALUE
            + 2 * PieceType.ROOK_VALUE
            + PieceType.QUEEN_VALUE
            + PieceType.KING_VALUE,
        position.material[Color.WHITE]);
    assertEquals(8 * PieceType.PAWN_VALUE
            + 2 * PieceType.KNIGHT_VALUE
            + 2 * PieceType.BISHOP_VALUE
            + 2 * PieceType.ROOK_VALUE
            + PieceType.QUEEN_VALUE
            + PieceType.KING_VALUE,
        position.material[Color.BLACK]);

    // Test castling
    assertEquals(File.h, position.castlingRights[Castling.WHITE_KINGSIDE]);
    assertEquals(File.a, position.castlingRights[Castling.WHITE_QUEENSIDE]);
    assertEquals(File.h, position.castlingRights[Castling.BLACK_KINGSIDE]);
    assertEquals(File.a, position.castlingRights[Castling.BLACK_QUEENSIDE]);

    // Test en passant
    assertEquals(Square.NOSQUARE, position.enPassantSquare);

    // Test active color
    assertEquals(Color.WHITE, position.activeColor);

    // Test half move clock
    assertEquals(0, position.halfmoveClock);

    // Test full move number
    assertEquals(1, position.getFullmoveNumber());
  }

}
