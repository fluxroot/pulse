/*
 * Copyright (C) 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

import org.junit.jupiter.api.Test;

import static com.fluxchess.pulse.model.Color.*;
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
