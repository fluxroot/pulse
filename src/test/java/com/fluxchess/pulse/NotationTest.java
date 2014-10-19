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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

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
      assertThat(position.board[Square.valueOf(file, Rank.r2)], is(Piece.WHITE_PAWN));
      assertThat(position.board[Square.valueOf(file, Rank.r7)], is(Piece.BLACK_PAWN));
    }

    // Test knights
    assertThat(position.board[Square.b1], is(Piece.WHITE_KNIGHT));
    assertThat(position.board[Square.g1], is(Piece.WHITE_KNIGHT));
    assertThat(position.board[Square.b8], is(Piece.BLACK_KNIGHT));
    assertThat(position.board[Square.g8], is(Piece.BLACK_KNIGHT));

    // Test bishops
    assertThat(position.board[Square.c1], is(Piece.WHITE_BISHOP));
    assertThat(position.board[Square.f1], is(Piece.WHITE_BISHOP));
    assertThat(position.board[Square.c8], is(Piece.BLACK_BISHOP));
    assertThat(position.board[Square.f8], is(Piece.BLACK_BISHOP));

    // Test rooks
    assertThat(position.board[Square.a1], is(Piece.WHITE_ROOK));
    assertThat(position.board[Square.h1], is(Piece.WHITE_ROOK));
    assertThat(position.board[Square.a8], is(Piece.BLACK_ROOK));
    assertThat(position.board[Square.h8], is(Piece.BLACK_ROOK));

    // Test queens
    assertThat(position.board[Square.d1], is(Piece.WHITE_QUEEN));
    assertThat(position.board[Square.d8], is(Piece.BLACK_QUEEN));

    // Test kings
    assertThat(position.board[Square.e1], is(Piece.WHITE_KING));
    assertThat(position.board[Square.e8], is(Piece.BLACK_KING));

    assertThat(position.material[Color.WHITE], is(8 * PieceType.PAWN_VALUE
            + 2 * PieceType.KNIGHT_VALUE
            + 2 * PieceType.BISHOP_VALUE
            + 2 * PieceType.ROOK_VALUE
            + PieceType.QUEEN_VALUE
            + PieceType.KING_VALUE));
    assertThat(position.material[Color.BLACK], is(8 * PieceType.PAWN_VALUE
            + 2 * PieceType.KNIGHT_VALUE
            + 2 * PieceType.BISHOP_VALUE
            + 2 * PieceType.ROOK_VALUE
            + PieceType.QUEEN_VALUE
            + PieceType.KING_VALUE));

    // Test castling
    assertThat(position.castlingRights & Castling.WHITE_KINGSIDE, is(not(Castling.NOCASTLING)));
    assertThat(position.castlingRights & Castling.WHITE_QUEENSIDE, is(not(Castling.NOCASTLING)));
    assertThat(position.castlingRights & Castling.BLACK_KINGSIDE, is(not(Castling.NOCASTLING)));
    assertThat(position.castlingRights & Castling.BLACK_QUEENSIDE, is(not(Castling.NOCASTLING)));

    // Test en passant
    assertThat(position.enPassantSquare, is(Square.NOSQUARE));

    // Test active color
    assertThat(position.activeColor, is(Color.WHITE));

    // Test half move clock
    assertThat(position.halfmoveClock, is(0));

    // Test full move number
    assertThat(position.getFullmoveNumber(), is(1));
  }

}
