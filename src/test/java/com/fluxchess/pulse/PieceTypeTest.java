/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.pulse.PieceType.BISHOP;
import static com.fluxchess.pulse.PieceType.KING;
import static com.fluxchess.pulse.PieceType.KNIGHT;
import static com.fluxchess.pulse.PieceType.NOPIECETYPE;
import static com.fluxchess.pulse.PieceType.PAWN;
import static com.fluxchess.pulse.PieceType.QUEEN;
import static com.fluxchess.pulse.PieceType.ROOK;
import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PieceTypeTest {

  @Test
  public void testUtilityClass()
      throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(PieceType.class);
  }

  @Test
  public void testValues() {
    for (int piecetype : PieceType.values) {
      assertThat(PieceType.values[piecetype], is(piecetype));
    }
  }

  @Test
  public void testIsValid() {
    for (int piecetype : PieceType.values) {
      assertThat(PieceType.isValid(piecetype), is(true));
    }

    assertThat(PieceType.isValid(NOPIECETYPE), is(false));
  }

  @Test
  public void testIsValidPromotion() {
    assertThat(PieceType.isValidPromotion(KNIGHT), is(true));
    assertThat(PieceType.isValidPromotion(BISHOP), is(true));
    assertThat(PieceType.isValidPromotion(ROOK), is(true));
    assertThat(PieceType.isValidPromotion(QUEEN), is(true));
    assertThat(PieceType.isValidPromotion(PAWN), is(false));
    assertThat(PieceType.isValidPromotion(KING), is(false));
    assertThat(PieceType.isValidPromotion(NOPIECETYPE), is(false));
  }

  @Test
  public void testIsSliding() {
    assertThat(PieceType.isSliding(BISHOP), is(true));
    assertThat(PieceType.isSliding(ROOK), is(true));
    assertThat(PieceType.isSliding(QUEEN), is(true));
    assertThat(PieceType.isSliding(PAWN), is(false));
    assertThat(PieceType.isSliding(KNIGHT), is(false));
    assertThat(PieceType.isSliding(KING), is(false));
  }

}
