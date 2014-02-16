/*
 * Copyright 2007-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
  public void testInvalidValueOf() {
    Piece.Type.valueOf(null);
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
