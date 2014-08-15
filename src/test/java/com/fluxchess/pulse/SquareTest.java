/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericPosition;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.junit.Assert.*;

public class SquareTest {

  @Test
  public void testUtilityClass() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Square.class);
  }

  @Test
  public void testValues() {
    for (GenericPosition genericPosition : GenericPosition.values()) {
      int square = Square.valueOf(genericPosition);
      assertEquals(genericPosition, Square.toGenericPosition(square));

      int file = Square.getFile(square);
      assertEquals(genericPosition.file, File.toGenericFile(file));

      int rank = Square.getRank(square);
      assertEquals(genericPosition.rank, Rank.toGenericRank(rank));
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
