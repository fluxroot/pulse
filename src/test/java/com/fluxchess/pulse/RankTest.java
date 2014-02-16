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
  public void testInvalidValueOf() {
    Rank.valueOf(null);
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
