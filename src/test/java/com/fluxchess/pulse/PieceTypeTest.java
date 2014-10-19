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

    assertThat(PieceType.isValid(PieceType.NOPIECETYPE), is(false));
  }

  @Test
  public void testIsValidPromotion() {
    assertThat(PieceType.isValidPromotion(PieceType.KNIGHT), is(true));
    assertThat(PieceType.isValidPromotion(PieceType.BISHOP), is(true));
    assertThat(PieceType.isValidPromotion(PieceType.ROOK), is(true));
    assertThat(PieceType.isValidPromotion(PieceType.QUEEN), is(true));
    assertThat(PieceType.isValidPromotion(PieceType.PAWN), is(false));
    assertThat(PieceType.isValidPromotion(PieceType.KING), is(false));
    assertThat(PieceType.isValidPromotion(PieceType.NOPIECETYPE), is(false));
  }

  @Test
  public void testIsSliding() {
    assertThat(PieceType.isSliding(PieceType.BISHOP), is(true));
    assertThat(PieceType.isSliding(PieceType.ROOK), is(true));
    assertThat(PieceType.isSliding(PieceType.QUEEN), is(true));
    assertThat(PieceType.isSliding(PieceType.PAWN), is(false));
    assertThat(PieceType.isSliding(PieceType.KNIGHT), is(false));
    assertThat(PieceType.isSliding(PieceType.KING), is(false));
  }

}
