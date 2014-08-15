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
import static org.junit.Assert.*;

public class PieceTypeTest {

  @Test
  public void testUtilityClass() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(PieceType.class);
  }

  @Test
  public void testValues() {
    for (int pieceType : PieceType.values) {
      assertTrue(PieceType.isValid(pieceType));
      assertEquals(pieceType, PieceType.values[pieceType]);
    }

    assertFalse(PieceType.isValid(PieceType.NOPIECETYPE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericChessman() {
    PieceType.toGenericChessman(PieceType.NOPIECETYPE);
  }

  @Test
  public void testIsValidPromotion() {
    assertTrue(PieceType.isValidPromotion(PieceType.KNIGHT));
    assertTrue(PieceType.isValidPromotion(PieceType.BISHOP));
    assertTrue(PieceType.isValidPromotion(PieceType.ROOK));
    assertTrue(PieceType.isValidPromotion(PieceType.QUEEN));
    assertFalse(PieceType.isValidPromotion(PieceType.PAWN));
    assertFalse(PieceType.isValidPromotion(PieceType.KING));
    assertFalse(PieceType.isValidPromotion(PieceType.NOPIECETYPE));
  }

  @Test
  public void testIsSliding() {
    assertTrue(PieceType.isSliding(PieceType.BISHOP));
    assertTrue(PieceType.isSliding(PieceType.ROOK));
    assertTrue(PieceType.isSliding(PieceType.QUEEN));
    assertFalse(PieceType.isSliding(PieceType.PAWN));
    assertFalse(PieceType.isSliding(PieceType.KNIGHT));
    assertFalse(PieceType.isSliding(PieceType.KING));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsSliding() {
    PieceType.isSliding(PieceType.NOPIECETYPE);
  }

}
