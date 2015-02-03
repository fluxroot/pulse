/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.pulse.Square.NOSQUARE;
import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SquareTest {

  @Test
  public void testUtilityClass()
      throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Square.class);
  }

  @Test
  public void testValues() {
    for (int rank : Rank.values) {
      for (int file : File.values) {
        int square = Square.valueOf(file, rank);

        assertThat(Square.getFile(square), is(file));
        assertThat(Square.getRank(square), is(rank));
      }
    }
  }

  @Test
  public void testIsValid() {
    for (int square : Square.values) {
      assertThat(Square.isValid(square), is(true));
    }

    assertThat(Square.isValid(NOSQUARE), is(false));
  }

}
