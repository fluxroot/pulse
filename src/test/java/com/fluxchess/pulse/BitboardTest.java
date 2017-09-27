/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Random;

import static com.fluxchess.pulse.Bitboard.add;
import static com.fluxchess.pulse.Square.a6;
import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
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
	public void testUtilityClass() throws Exception {
		assertUtilityClassWellDefined(Bitboard.class);
	}

	@Test
	public void shouldAddAllSquaresCorrectly() {
		long bitboard = 0;

		for (int x88square : pool) {
			bitboard = add(x88square, bitboard);
		}

		assertThat(bitboard, is(-1L));
	}

	@Test
	public void shouldRemoveAllSquaresCorrectly() {
		long bitboard = -1;

		for (int x88square : pool) {
			bitboard = Bitboard.remove(x88square, bitboard);
		}

		assertThat(bitboard, is(0L));
	}

	@Test
	public void shouldReturnTheNextSquare() {
		long bitboard = add(a6, 0);

		int square = Bitboard.next(bitboard);

		assertThat(square, is(a6));
	}

	@Test
	public void shouldReturnCorrectRemainder() {
		long bitboard = 0b1110100;

		long remainder = Bitboard.remainder(bitboard);

		assertThat(remainder, is(0b1110000L));
	}

	@Test
	public void shouldReturnCorrectSize() {
		long bitboard = 0b111;

		int size = Bitboard.size(bitboard);

		assertThat(size, is(3));
	}
}
