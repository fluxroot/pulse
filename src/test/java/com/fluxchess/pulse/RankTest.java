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

import com.fluxchess.jcpi.models.GenericRank;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.junit.Assert.*;

public class RankTest {

  @Test
  public void testUtilityClass() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Rank.class);
  }

  @Test
  public void testValues() {
    for (GenericRank genericRank : GenericRank.values()) {
      assertEquals(genericRank, Rank.toGenericRank(Rank.valueOf(genericRank)));
      assertEquals(genericRank.ordinal(), Rank.valueOf(genericRank));
      assertEquals(Rank.valueOf(genericRank), Rank.values[Rank.valueOf(genericRank)]);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericRank() {
    Rank.toGenericRank(Rank.NORANK);
  }

  @Test
  public void testIsValid() {
    for (int rank : Rank.values) {
      assertTrue(Rank.isValid(rank));
      assertEquals(rank, rank & Rank.MASK);
    }

    assertFalse(Rank.isValid(Rank.NORANK));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsValid() {
    Rank.isValid(-1);
  }

}
