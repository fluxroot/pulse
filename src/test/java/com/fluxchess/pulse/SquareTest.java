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
      assertTrue(Square.isValid(square));
      assertEquals(genericPosition, Square.toGenericPosition(square));

      int file = Square.getFile(square);
      assertTrue(File.isValid(file));
      assertEquals(genericPosition.file.ordinal(), file);
      assertEquals(genericPosition.file, File.toGenericFile(file));

      int rank = Square.getRank(square);
      assertTrue(Rank.isValid(rank));
      assertEquals(genericPosition.rank.ordinal(), rank);
      assertEquals(genericPosition.rank, Rank.toGenericRank(rank));
    }

    assertFalse(Square.isValid(Square.NOSQUARE));
  }

  @Test
  public void testX88Squares() {
    int bitSquare = 0;
    for (int x88Square : Square.values) {
      assertEquals(bitSquare, Square.toBitSquare(x88Square));
      assertEquals(x88Square, Square.toX88Square(bitSquare));
      ++bitSquare;
    }
  }

}
