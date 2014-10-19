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

    assertThat(Piece.isValid(Piece.NOPIECE), is(false));
  }

  @Test
  public void testGetType() {
    assertThat(Piece.getType(Piece.WHITE_PAWN), is(PieceType.PAWN));
    assertThat(Piece.getType(Piece.BLACK_PAWN), is(PieceType.PAWN));
    assertThat(Piece.getType(Piece.WHITE_KNIGHT), is(PieceType.KNIGHT));
    assertThat(Piece.getType(Piece.BLACK_KNIGHT), is(PieceType.KNIGHT));
    assertThat(Piece.getType(Piece.WHITE_BISHOP), is(PieceType.BISHOP));
    assertThat(Piece.getType(Piece.BLACK_BISHOP), is(PieceType.BISHOP));
    assertThat(Piece.getType(Piece.WHITE_ROOK), is(PieceType.ROOK));
    assertThat(Piece.getType(Piece.BLACK_ROOK), is(PieceType.ROOK));
    assertThat(Piece.getType(Piece.WHITE_QUEEN), is(PieceType.QUEEN));
    assertThat(Piece.getType(Piece.BLACK_QUEEN), is(PieceType.QUEEN));
    assertThat(Piece.getType(Piece.WHITE_KING), is(PieceType.KING));
    assertThat(Piece.getType(Piece.BLACK_KING), is(PieceType.KING));
  }

  @Test
  public void testGetColor() {
    assertThat(Piece.getColor(Piece.WHITE_PAWN), is(Color.WHITE));
    assertThat(Piece.getColor(Piece.BLACK_PAWN), is(Color.BLACK));
    assertThat(Piece.getColor(Piece.WHITE_KNIGHT), is(Color.WHITE));
    assertThat(Piece.getColor(Piece.BLACK_KNIGHT), is(Color.BLACK));
    assertThat(Piece.getColor(Piece.WHITE_BISHOP), is(Color.WHITE));
    assertThat(Piece.getColor(Piece.BLACK_BISHOP), is(Color.BLACK));
    assertThat(Piece.getColor(Piece.WHITE_ROOK), is(Color.WHITE));
    assertThat(Piece.getColor(Piece.BLACK_ROOK), is(Color.BLACK));
    assertThat(Piece.getColor(Piece.WHITE_QUEEN), is(Color.WHITE));
    assertThat(Piece.getColor(Piece.BLACK_QUEEN), is(Color.BLACK));
    assertThat(Piece.getColor(Piece.WHITE_KING), is(Color.WHITE));
    assertThat(Piece.getColor(Piece.BLACK_KING), is(Color.BLACK));
  }

}
