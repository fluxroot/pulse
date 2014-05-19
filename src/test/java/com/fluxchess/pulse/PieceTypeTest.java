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
    assertUtilityClassWellDefined(Piece.Type.class);
  }

  @Test
  public void testValues() {
    for (int pieceType : Piece.Type.values) {
      assertTrue(Piece.Type.isValid(pieceType));
      assertEquals(pieceType, Piece.Type.values[pieceType]);
    }

    assertFalse(Piece.Type.isValid(Piece.Type.NOPIECETYPE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericChessman() {
    Piece.Type.toGenericChessman(Piece.Type.NOPIECETYPE);
  }

  @Test
  public void testIsValidPromotion() {
    assertTrue(Piece.Type.isValidPromotion(Piece.Type.KNIGHT));
    assertTrue(Piece.Type.isValidPromotion(Piece.Type.BISHOP));
    assertTrue(Piece.Type.isValidPromotion(Piece.Type.ROOK));
    assertTrue(Piece.Type.isValidPromotion(Piece.Type.QUEEN));
    assertFalse(Piece.Type.isValidPromotion(Piece.Type.PAWN));
    assertFalse(Piece.Type.isValidPromotion(Piece.Type.KING));
    assertFalse(Piece.Type.isValidPromotion(Piece.Type.NOPIECETYPE));
  }

  @Test
  public void testIsSliding() {
    assertTrue(Piece.Type.isSliding(Piece.Type.BISHOP));
    assertTrue(Piece.Type.isSliding(Piece.Type.ROOK));
    assertTrue(Piece.Type.isSliding(Piece.Type.QUEEN));
    assertFalse(Piece.Type.isSliding(Piece.Type.PAWN));
    assertFalse(Piece.Type.isSliding(Piece.Type.KNIGHT));
    assertFalse(Piece.Type.isSliding(Piece.Type.KING));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsSliding() {
    Piece.Type.isSliding(Piece.Type.NOPIECETYPE);
  }

}
