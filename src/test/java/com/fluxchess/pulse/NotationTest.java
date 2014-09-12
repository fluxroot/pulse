/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class NotationTest {

  @Test
  public void testUtilityClass()
      throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Notation.class);
  }

  @Test
  public void testStandardPosition() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);

    // Test pawns
    for (int file : File.values) {
      assertEquals(Piece.WHITE_PAWN, position.board[Square.valueOf(file, Rank.r2)]);
      assertEquals(Piece.BLACK_PAWN, position.board[Square.valueOf(file, Rank.r7)]);
    }

    // Test knights
    assertEquals(Piece.WHITE_KNIGHT, position.board[Square.b1]);
    assertEquals(Piece.WHITE_KNIGHT, position.board[Square.g1]);
    assertEquals(Piece.BLACK_KNIGHT, position.board[Square.b8]);
    assertEquals(Piece.BLACK_KNIGHT, position.board[Square.g8]);

    // Test bishops
    assertEquals(Piece.WHITE_BISHOP, position.board[Square.c1]);
    assertEquals(Piece.WHITE_BISHOP, position.board[Square.f1]);
    assertEquals(Piece.BLACK_BISHOP, position.board[Square.c8]);
    assertEquals(Piece.BLACK_BISHOP, position.board[Square.f8]);

    // Test rooks
    assertEquals(Piece.WHITE_ROOK, position.board[Square.a1]);
    assertEquals(Piece.WHITE_ROOK, position.board[Square.h1]);
    assertEquals(Piece.BLACK_ROOK, position.board[Square.a8]);
    assertEquals(Piece.BLACK_ROOK, position.board[Square.h8]);

    // Test queens
    assertEquals(Piece.WHITE_QUEEN, position.board[Square.d1]);
    assertEquals(Piece.BLACK_QUEEN, position.board[Square.d8]);

    // Test kings
    assertEquals(Piece.WHITE_KING, position.board[Square.e1]);
    assertEquals(Piece.BLACK_KING, position.board[Square.e8]);

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
    assertNotEquals(Castling.NOCASTLING, position.castlingRights & Castling.WHITE_KINGSIDE);
    assertNotEquals(Castling.NOCASTLING, position.castlingRights & Castling.WHITE_QUEENSIDE);
    assertNotEquals(Castling.NOCASTLING, position.castlingRights & Castling.BLACK_KINGSIDE);
    assertNotEquals(Castling.NOCASTLING, position.castlingRights & Castling.BLACK_QUEENSIDE);

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
