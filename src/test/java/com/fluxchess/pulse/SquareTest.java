/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SquareTest {

	@Test
	public void testUtilityClass() throws Exception {
		assertUtilityClassWellDefined(Square.class);
	}

	@Test
	public void testValues() {
		for (int rank : Rank.values) {
			for (int file : File.values) {
				int square = Square.valueOf(file, rank);

				assertThat(Square.getFile(square), is(file));
				assertThat(Square.getRank(square), is(rank));
			}
		}
	}
}
