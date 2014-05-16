/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
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
    }

    assertFalse(Color.isValid(Color.NOCOLOR));
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
