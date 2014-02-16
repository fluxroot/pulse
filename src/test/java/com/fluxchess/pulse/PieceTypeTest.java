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
    assertUtilityClassWellDefined(PieceType.class);
  }

  @Test
  public void testValues() {
    for (GenericChessman genericChessman : GenericChessman.values()) {
      assertEquals(genericChessman, PieceType.toGenericChessman(PieceType.valueOf(genericChessman)));
      assertEquals(genericChessman.ordinal(), PieceType.valueOf(genericChessman));
      assertEquals(PieceType.valueOf(genericChessman), PieceType.values[PieceType.valueOf(genericChessman)]);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidValueOf() {
    PieceType.valueOf(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericChessman() {
    PieceType.toGenericChessman(PieceType.NOCHESSMAN);
  }

  @Test
  public void testPromotionValues() {
    for (GenericChessman genericChessman : GenericChessman.promotions) {
      assertEquals(genericChessman, PieceType.toGenericChessman(PieceType.valueOfPromotion(genericChessman)));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidValueOfPromotion() {
    PieceType.valueOfPromotion(GenericChessman.PAWN);
  }

  @Test
  public void testIsValid() {
    for (int chessman : PieceType.values) {
      assertTrue(PieceType.isValid(chessman));
      assertEquals(chessman, chessman & PieceType.MASK);
    }

    assertFalse(PieceType.isValid(PieceType.NOCHESSMAN));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsValid() {
    PieceType.isValid(-1);
  }

  @Test
  public void testIsValidPromotion() {
    for (int chessman : PieceType.promotions) {
      assertTrue(PieceType.isValidPromotion(chessman));
      assertEquals(chessman, chessman & PieceType.MASK);
    }

    assertFalse(PieceType.isValidPromotion(PieceType.PAWN));
    assertFalse(PieceType.isValidPromotion(PieceType.KING));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsValidPromotion() {
    PieceType.isValidPromotion(PieceType.NOCHESSMAN);
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
    PieceType.isSliding(PieceType.NOCHESSMAN);
  }

}
