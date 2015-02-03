/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.pulse.Rank.NORANK;
import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RankTest {

  @Test
  public void testUtilityClass()
      throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Rank.class);
  }

  @Test
  public void testValues() {
    for (int rank : Rank.values) {
      assertThat(Rank.values[rank], is(rank));
    }
  }

  @Test
  public void testIsValid() {
    for (int rank : Rank.values) {
      assertThat(Rank.isValid(rank), is(true));
    }

    assertThat(Rank.isValid(NORANK), is(false));
  }

}
