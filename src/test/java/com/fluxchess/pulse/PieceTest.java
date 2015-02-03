/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.pulse.Color.BLACK;
import static com.fluxchess.pulse.Color.WHITE;
import static com.fluxchess.pulse.Piece.BLACK_BISHOP;
import static com.fluxchess.pulse.Piece.BLACK_KING;
import static com.fluxchess.pulse.Piece.BLACK_KNIGHT;
import static com.fluxchess.pulse.Piece.BLACK_PAWN;
import static com.fluxchess.pulse.Piece.BLACK_QUEEN;
import static com.fluxchess.pulse.Piece.BLACK_ROOK;
import static com.fluxchess.pulse.Piece.NOPIECE;
import static com.fluxchess.pulse.Piece.WHITE_BISHOP;
import static com.fluxchess.pulse.Piece.WHITE_KING;
import static com.fluxchess.pulse.Piece.WHITE_KNIGHT;
import static com.fluxchess.pulse.Piece.WHITE_PAWN;
import static com.fluxchess.pulse.Piece.WHITE_QUEEN;
import static com.fluxchess.pulse.Piece.WHITE_ROOK;
import static com.fluxchess.pulse.PieceType.BISHOP;
import static com.fluxchess.pulse.PieceType.KING;
import static com.fluxchess.pulse.PieceType.KNIGHT;
import static com.fluxchess.pulse.PieceType.PAWN;
import static com.fluxchess.pulse.PieceType.QUEEN;
import static com.fluxchess.pulse.PieceType.ROOK;
import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PieceTest {

  @Test
  public void testUtilityClass()
      throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Piece.class);
  }

  @Test
  public void testValues() {
    for (int color : Color.values) {
      for (int piecetype : PieceType.values) {
        int piece = Piece.valueOf(color, piecetype);

        assertThat(Piece.values[piece], is(piece));
      }
    }
  }

  @Test
  public void testIsValid() {
    for (int piece : Piece.values) {
      assertThat(Piece.isValid(piece), is(true));
    }

    assertThat(Piece.isValid(NOPIECE), is(false));
  }

  @Test
  public void testGetType() {
    assertThat(Piece.getType(WHITE_PAWN), is(PAWN));
    assertThat(Piece.getType(BLACK_PAWN), is(PAWN));
    assertThat(Piece.getType(WHITE_KNIGHT), is(KNIGHT));
    assertThat(Piece.getType(BLACK_KNIGHT), is(KNIGHT));
    assertThat(Piece.getType(WHITE_BISHOP), is(BISHOP));
    assertThat(Piece.getType(BLACK_BISHOP), is(BISHOP));
    assertThat(Piece.getType(WHITE_ROOK), is(ROOK));
    assertThat(Piece.getType(BLACK_ROOK), is(ROOK));
    assertThat(Piece.getType(WHITE_QUEEN), is(QUEEN));
    assertThat(Piece.getType(BLACK_QUEEN), is(QUEEN));
    assertThat(Piece.getType(WHITE_KING), is(KING));
    assertThat(Piece.getType(BLACK_KING), is(KING));
  }

  @Test
  public void testGetColor() {
    assertThat(Piece.getColor(WHITE_PAWN), is(WHITE));
    assertThat(Piece.getColor(BLACK_PAWN), is(BLACK));
    assertThat(Piece.getColor(WHITE_KNIGHT), is(WHITE));
    assertThat(Piece.getColor(BLACK_KNIGHT), is(BLACK));
    assertThat(Piece.getColor(WHITE_BISHOP), is(WHITE));
    assertThat(Piece.getColor(BLACK_BISHOP), is(BLACK));
    assertThat(Piece.getColor(WHITE_ROOK), is(WHITE));
    assertThat(Piece.getColor(BLACK_ROOK), is(BLACK));
    assertThat(Piece.getColor(WHITE_QUEEN), is(WHITE));
    assertThat(Piece.getColor(BLACK_QUEEN), is(BLACK));
    assertThat(Piece.getColor(WHITE_KING), is(WHITE));
    assertThat(Piece.getColor(BLACK_KING), is(BLACK));
  }

}
