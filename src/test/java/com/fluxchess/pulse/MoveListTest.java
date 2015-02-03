/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static com.fluxchess.pulse.MoveList.MoveEntry;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MoveListTest {

  @Test
  public void test() {
    MoveList<MoveEntry> moveList = new MoveList<>(MoveEntry.class);

    assertThat(moveList.size, is(0));

    moveList.entries[moveList.size++].move = 1;
    assertThat(moveList.size, is(1));
  }

}
