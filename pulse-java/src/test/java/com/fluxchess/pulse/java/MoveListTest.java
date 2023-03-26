/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoveListTest {

	@Test
	void test() {
		MoveList<MoveList.MoveEntry> moveList = new MoveList<>(MoveList.MoveEntry.class);

		assertThat(moveList.size).isEqualTo(0);

		moveList.entries[moveList.size++].move = 1;
		assertThat(moveList.size).isEqualTo(1);
	}
}
