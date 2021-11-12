/*
 * Copyright (C) 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SquareTest {

	@Test
	void testValues() {
		for (int rank : Rank.values) {
			for (int file : File.values) {
				int square = Square.valueOf(file, rank);

				assertThat(Square.getFile(square)).isEqualTo(file);
				assertThat(Square.getRank(square)).isEqualTo(rank);
			}
		}
	}
}
