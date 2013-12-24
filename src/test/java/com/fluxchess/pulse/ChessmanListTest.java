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

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ChessmanListTest {

  private Random random = null;
  private LinkedList<Integer> pool = null;

  @Before
  public void setUp() {
    random = new Random();
    pool = new LinkedList<>();

    while (pool.size() < Long.SIZE) {
      int value = random.nextInt(Long.SIZE);
      if (!pool.contains(Position.values[value])) {
        pool.add(Position.values[value]);
      }
    }
  }

  @Test
  public void testX88Positions() {
    int bitposition = 0;
    for (int x88position : Position.values) {
      assertEquals(bitposition, ChessmanList.toBitPosition(x88position));
      assertEquals(x88position, ChessmanList.toX88Position(bitposition));
      ++bitposition;
    }
  }

  @Test
  public void testAdd() {
    ChessmanList list = new ChessmanList();

    for (int x88position : pool) {
      list.add(x88position);
    }

    assertEquals(-1, list.positions);
  }

  @Test
  public void testRemove() {
    ChessmanList list = new ChessmanList();
    list.positions = -1;

    for (int x88position : pool) {
      list.remove(x88position);
    }

    assertEquals(0, list.positions);
  }

}
