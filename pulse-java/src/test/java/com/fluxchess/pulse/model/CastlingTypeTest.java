/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CastlingTypeTest {

	@Test
	void testValues() {
		for (int castlingtype : CastlingType.values) {
			assertThat(CastlingType.values[castlingtype]).isEqualTo(castlingtype);
		}
	}
}
