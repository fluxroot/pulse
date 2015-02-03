/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.pulse.Castling.BLACK_KINGSIDE;
import static com.fluxchess.pulse.Castling.BLACK_QUEENSIDE;
import static com.fluxchess.pulse.Castling.NOCASTLING;
import static com.fluxchess.pulse.Castling.WHITE_KINGSIDE;
import static com.fluxchess.pulse.Castling.WHITE_QUEENSIDE;
import static com.fluxchess.pulse.Color.BLACK;
import static com.fluxchess.pulse.Color.WHITE;
import static com.fluxchess.pulse.Piece.BLACK_BISHOP;
import static com.fluxchess.pulse.Piece.BLACK_KING;
import static com.fluxchess.pulse.Piece.BLACK_KNIGHT;
import static com.fluxchess.pulse.Piece.BLACK_PAWN;
import static com.fluxchess.pulse.Piece.BLACK_QUEEN;
import static com.fluxchess.pulse.Piece.BLACK_ROOK;
import static com.fluxchess.pulse.Piece.WHITE_BISHOP;
import static com.fluxchess.pulse.Piece.WHITE_KING;
import static com.fluxchess.pulse.Piece.WHITE_KNIGHT;
import static com.fluxchess.pulse.Piece.WHITE_PAWN;
import static com.fluxchess.pulse.Piece.WHITE_QUEEN;
import static com.fluxchess.pulse.Piece.WHITE_ROOK;
import static com.fluxchess.pulse.PieceType.BISHOP_VALUE;
import static com.fluxchess.pulse.PieceType.KING_VALUE;
import static com.fluxchess.pulse.PieceType.KNIGHT_VALUE;
import static com.fluxchess.pulse.PieceType.PAWN_VALUE;
import static com.fluxchess.pulse.PieceType.QUEEN_VALUE;
import static com.fluxchess.pulse.PieceType.ROOK_VALUE;
import static com.fluxchess.pulse.Rank.r2;
import static com.fluxchess.pulse.Rank.r7;
import static com.fluxchess.pulse.Square.NOSQUARE;
import static com.fluxchess.pulse.Square.a1;
import static com.fluxchess.pulse.Square.a8;
import static com.fluxchess.pulse.Square.b1;
import static com.fluxchess.pulse.Square.b8;
import static com.fluxchess.pulse.Square.c1;
import static com.fluxchess.pulse.Square.c8;
import static com.fluxchess.pulse.Square.d1;
import static com.fluxchess.pulse.Square.d8;
import static com.fluxchess.pulse.Square.e1;
import static com.fluxchess.pulse.Square.e8;
import static com.fluxchess.pulse.Square.f1;
import static com.fluxchess.pulse.Square.f8;
import static com.fluxchess.pulse.Square.g1;
import static com.fluxchess.pulse.Square.g8;
import static com.fluxchess.pulse.Square.h1;
import static com.fluxchess.pulse.Square.h8;
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
      assertThat(position.board[Square.valueOf(file, r2)], is(WHITE_PAWN));
      assertThat(position.board[Square.valueOf(file, r7)], is(BLACK_PAWN));
    }

    // Test knights
    assertThat(position.board[b1], is(WHITE_KNIGHT));
    assertThat(position.board[g1], is(WHITE_KNIGHT));
    assertThat(position.board[b8], is(BLACK_KNIGHT));
    assertThat(position.board[g8], is(BLACK_KNIGHT));

    // Test bishops
    assertThat(position.board[c1], is(WHITE_BISHOP));
    assertThat(position.board[f1], is(WHITE_BISHOP));
    assertThat(position.board[c8], is(BLACK_BISHOP));
    assertThat(position.board[f8], is(BLACK_BISHOP));

    // Test rooks
    assertThat(position.board[a1], is(WHITE_ROOK));
    assertThat(position.board[h1], is(WHITE_ROOK));
    assertThat(position.board[a8], is(BLACK_ROOK));
    assertThat(position.board[h8], is(BLACK_ROOK));

    // Test queens
    assertThat(position.board[d1], is(WHITE_QUEEN));
    assertThat(position.board[d8], is(BLACK_QUEEN));

    // Test kings
    assertThat(position.board[e1], is(WHITE_KING));
    assertThat(position.board[e8], is(BLACK_KING));

    assertThat(position.material[WHITE], is((8 * PAWN_VALUE)
        + (2 * KNIGHT_VALUE)
        + (2 * BISHOP_VALUE)
        + (2 * ROOK_VALUE)
        + QUEEN_VALUE
        + KING_VALUE));
    assertThat(position.material[BLACK], is((8 * PAWN_VALUE)
        + (2 * KNIGHT_VALUE)
        + (2 * BISHOP_VALUE)
        + (2 * ROOK_VALUE)
        + QUEEN_VALUE
        + KING_VALUE));

    // Test castling
    assertThat(position.castlingRights & WHITE_KINGSIDE, is(not(NOCASTLING)));
    assertThat(position.castlingRights & WHITE_QUEENSIDE, is(not(NOCASTLING)));
    assertThat(position.castlingRights & BLACK_KINGSIDE, is(not(NOCASTLING)));
    assertThat(position.castlingRights & BLACK_QUEENSIDE, is(not(NOCASTLING)));

    // Test en passant
    assertThat(position.enPassantSquare, is(NOSQUARE));

    // Test active color
    assertThat(position.activeColor, is(WHITE));

    // Test half move clock
    assertThat(position.halfmoveClock, is(0));

    // Test full move number
    assertThat(position.getFullmoveNumber(), is(1));
  }

}
