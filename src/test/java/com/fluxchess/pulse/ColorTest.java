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

public class ColorTest {

  @Test
  public void testUtilityClass()
      throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Color.class);
  }

  @Test
  public void testValues() {
    for (int color : Color.values) {
      assertEquals(color, Color.values[color]);
    }
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

}
