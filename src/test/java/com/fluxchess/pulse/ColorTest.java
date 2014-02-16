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
  public void testInvalidValueOf() {
    Color.valueOf(null);
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
