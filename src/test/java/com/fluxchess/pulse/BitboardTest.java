/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BitboardTest {

  private LinkedList<Integer> pool = null;

  @Before
  public void setUp() {
    Random random = new Random();
    pool = new LinkedList<>();

    while (pool.size() < Long.SIZE) {
      int value = random.nextInt(Long.SIZE);
      if (!pool.contains(Square.values[value])) {
        pool.add(Square.values[value]);
      }
    }
  }

  @Test
  public void testAdd() {
    Bitboard list = new Bitboard();

    for (int x88square : pool) {
      list.add(x88square);
    }

    assertThat(list.squares, is(-1L));
  }

  @Test
  public void testRemove() {
    Bitboard list = new Bitboard();
    list.squares = -1;

    for (int x88square : pool) {
      list.remove(x88square);
    }

    assertThat(list.squares, is(0L));
  }

}
