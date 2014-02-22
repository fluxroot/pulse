/*
 * Copyright 2013-2014 the original author or authors.
 *
 * This file is part of Pulse Chess.
 *
 * Pulse Chess is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pulse Chess is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pulse Chess.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericChessman;
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
    for (GenericChessman genericChessman : GenericChessman.values()) {
      assertEquals(genericChessman, Piece.Type.toGenericChessman(Piece.Type.valueOf(genericChessman)));
      assertEquals(genericChessman.ordinal(), Piece.Type.valueOf(genericChessman));
      assertEquals(Piece.Type.valueOf(genericChessman), Piece.Type.values[Piece.Type.valueOf(genericChessman)]);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericChessman() {
    Piece.Type.toGenericChessman(Piece.Type.NOTYPE);
  }

  @Test
  public void testPromotionValues() {
    for (GenericChessman genericChessman : GenericChessman.promotions) {
      assertEquals(genericChessman, Piece.Type.toGenericChessman(Piece.Type.valueOfPromotion(genericChessman)));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidValueOfPromotion() {
    Piece.Type.valueOfPromotion(GenericChessman.PAWN);
  }

  @Test
  public void testIsValid() {
    for (int chessman : Piece.Type.values) {
      assertTrue(Piece.Type.isValid(chessman));
      assertEquals(chessman, chessman & Piece.Type.MASK);
    }

    assertFalse(Piece.Type.isValid(Piece.Type.NOTYPE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsValid() {
    Piece.Type.isValid(-1);
  }

  @Test
  public void testIsValidPromotion() {
    for (int chessman : Piece.Type.promotions) {
      assertTrue(Piece.Type.isValidPromotion(chessman));
      assertEquals(chessman, chessman & Piece.Type.MASK);
    }

    assertFalse(Piece.Type.isValidPromotion(Piece.Type.PAWN));
    assertFalse(Piece.Type.isValidPromotion(Piece.Type.KING));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsValidPromotion() {
    Piece.Type.isValidPromotion(Piece.Type.NOTYPE);
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
    Piece.Type.isSliding(Piece.Type.NOTYPE);
  }

}
