/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java.model;

import org.junit.jupiter.api.Test;

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
		assertThat(PieceType.isValidPromotion(PieceType.KNIGHT)).isEqualTo(true);
		assertThat(PieceType.isValidPromotion(PieceType.BISHOP)).isEqualTo(true);
		assertThat(PieceType.isValidPromotion(PieceType.ROOK)).isEqualTo(true);
		assertThat(PieceType.isValidPromotion(PieceType.QUEEN)).isEqualTo(true);
		assertThat(PieceType.isValidPromotion(PieceType.PAWN)).isEqualTo(false);
		assertThat(PieceType.isValidPromotion(PieceType.KING)).isEqualTo(false);
		assertThat(PieceType.isValidPromotion(PieceType.NOPIECETYPE)).isEqualTo(false);
	}

	@Test
	void testIsSliding() {
		assertThat(PieceType.isSliding(PieceType.BISHOP)).isEqualTo(true);
		assertThat(PieceType.isSliding(PieceType.ROOK)).isEqualTo(true);
		assertThat(PieceType.isSliding(PieceType.QUEEN)).isEqualTo(true);
		assertThat(PieceType.isSliding(PieceType.PAWN)).isEqualTo(false);
		assertThat(PieceType.isSliding(PieceType.KNIGHT)).isEqualTo(false);
		assertThat(PieceType.isSliding(PieceType.KING)).isEqualTo(false);
	}
}
