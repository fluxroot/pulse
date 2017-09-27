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

public class CastlingTest {

	@Test
	public void testUtilityClass() throws Exception {
		assertUtilityClassWellDefined(Castling.class);
	}

	@Test
	public void testValueOf() {
		assertThat(Castling.valueOf(Color.WHITE, CastlingType.KINGSIDE), is(Castling.WHITE_KINGSIDE));
		assertThat(Castling.valueOf(Color.WHITE, CastlingType.QUEENSIDE), is(Castling.WHITE_QUEENSIDE));
		assertThat(Castling.valueOf(Color.BLACK, CastlingType.KINGSIDE), is(Castling.BLACK_KINGSIDE));
		assertThat(Castling.valueOf(Color.BLACK, CastlingType.QUEENSIDE), is(Castling.BLACK_QUEENSIDE));
	}
}
