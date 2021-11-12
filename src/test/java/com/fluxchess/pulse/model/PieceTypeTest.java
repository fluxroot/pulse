/*
 * Copyright (C) 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

import org.junit.jupiter.api.Test;

import static com.fluxchess.pulse.model.PieceType.*;
import static org.assertj.core.api.Assertions.assertThat;

class PieceTypeTest {

	@Test
	void testValues() {
		for (int piecetype : PieceType.values) {
			assertThat(PieceType.values[piecetype]).isEqualTo(piecetype);
		}
	}

	@Test
	void testIsValidPromotion() {
		assertThat(PieceType.isValidPromotion(KNIGHT)).isEqualTo(true);
		assertThat(PieceType.isValidPromotion(BISHOP)).isEqualTo(true);
		assertThat(PieceType.isValidPromotion(ROOK)).isEqualTo(true);
		assertThat(PieceType.isValidPromotion(QUEEN)).isEqualTo(true);
		assertThat(PieceType.isValidPromotion(PAWN)).isEqualTo(false);
		assertThat(PieceType.isValidPromotion(KING)).isEqualTo(false);
		assertThat(PieceType.isValidPromotion(NOPIECETYPE)).isEqualTo(false);
	}

	@Test
	void testIsSliding() {
		assertThat(PieceType.isSliding(BISHOP)).isEqualTo(true);
		assertThat(PieceType.isSliding(ROOK)).isEqualTo(true);
		assertThat(PieceType.isSliding(QUEEN)).isEqualTo(true);
		assertThat(PieceType.isSliding(PAWN)).isEqualTo(false);
		assertThat(PieceType.isSliding(KNIGHT)).isEqualTo(false);
		assertThat(PieceType.isSliding(KING)).isEqualTo(false);
	}
}
