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

        assertEquals(file, Square.getFile(square));
        assertEquals(rank, Square.getRank(square));
      }
    }
  }

  @Test
  public void testIsValid() {
    for (int square : Square.values) {
      assertTrue(Square.isValid(square));
    }

    assertFalse(Square.isValid(Square.NOSQUARE));
  }

}
