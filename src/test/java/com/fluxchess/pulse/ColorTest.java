/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static com.fluxchess.pulse.Color.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ColorTest {

	@Test
	public void testValues() {
		for (int color : Color.values) {
			assertThat(Color.values[color], is(color));
		}
	}

	@Test
	public void testOpposite() {
		assertThat(opposite(BLACK), is(WHITE));
		assertThat(opposite(WHITE), is(BLACK));
	}
}
