/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
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
    for (int rank : Rank.values) {
      assertEquals(rank, Rank.values[rank]);
    }
  }

  @Test
  public void testIsValid() {
    for (int rank : Rank.values) {
      assertTrue(Rank.isValid(rank));
    }

    assertFalse(Rank.isValid(Rank.NORANK));
  }

}
