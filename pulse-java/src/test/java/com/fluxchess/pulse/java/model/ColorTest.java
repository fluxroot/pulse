/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java.model;

import org.junit.jupiter.api.Test;

import static com.fluxchess.pulse.java.model.Color.BLACK;
import static com.fluxchess.pulse.java.model.Color.WHITE;
import static com.fluxchess.pulse.java.model.Color.opposite;
import static org.assertj.core.api.Assertions.assertThat;

class ColorTest {

	@Test
	void testValues() {
		for (int color : Color.values) {
			assertThat(Color.values[color]).isEqualTo(color);
		}
	}

	@Test
	void testOpposite() {
		assertThat(opposite(BLACK)).isEqualTo(WHITE);
		assertThat(opposite(WHITE)).isEqualTo(BLACK);
	}
}
