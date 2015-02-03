/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.pulse.Color.BLACK;
import static com.fluxchess.pulse.Color.NOCOLOR;
import static com.fluxchess.pulse.Color.WHITE;
import static com.fluxchess.pulse.Color.opposite;
import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ColorTest {

  @Test
  public void testUtilityClass()
      throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Color.class);
  }

  @Test
  public void testValues() {
    for (int color : Color.values) {
      assertThat(Color.values[color], is(color));
    }
  }

  @Test
  public void testIsValid() {
    for (int color : Color.values) {
      assertThat(Color.isValid(color), is(true));
    }

    assertThat(Color.isValid(NOCOLOR), is(false));
  }

  @Test
  public void testOpposite() {
    assertThat(opposite(BLACK), is(WHITE));
    assertThat(opposite(WHITE), is(BLACK));
  }

}
