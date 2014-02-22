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

import com.fluxchess.jcpi.models.GenericColor;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.junit.Assert.*;

public class ColorTest {

  @Test
  public void testUtilityClass() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Color.class);
  }

  @Test
  public void testValues() {
    for (GenericColor genericColor : GenericColor.values()) {
      assertEquals(genericColor, Color.toGenericColor(Color.valueOf(genericColor)));
      assertEquals(genericColor.ordinal(), Color.valueOf(genericColor));
      assertEquals(Color.valueOf(genericColor), Color.values[Color.valueOf(genericColor)]);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericColor() {
    Color.toGenericColor(Color.NOCOLOR);
  }

  @Test
  public void testIsValid() {
    for (int color : Color.values) {
      assertTrue(Color.isValid(color));
      assertEquals(color, color & Color.MASK);
    }

    assertFalse(Color.isValid(Color.NOCOLOR));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsValid() {
    Color.isValid(-1);
  }

  @Test
  public void testOpposite() {
    assertEquals(Color.WHITE, Color.opposite(Color.BLACK));
    assertEquals(Color.BLACK, Color.opposite(Color.WHITE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidOpposite() {
    Color.opposite(Color.NOCOLOR);
  }

}
