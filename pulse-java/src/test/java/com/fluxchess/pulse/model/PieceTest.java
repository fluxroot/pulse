/*
 * Copyright 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

import org.junit.jupiter.api.Test;

import static com.fluxchess.pulse.model.Color.BLACK;
import static com.fluxchess.pulse.model.Color.WHITE;
import static com.fluxchess.pulse.model.Piece.*;
import static com.fluxchess.pulse.model.PieceType.*;
import static org.assertj.core.api.Assertions.assertThat;

class PieceTest {

	@Test
	void testValues() {
		for (int piece : Piece.values) {
			assertThat(Piece.values[piece]).isEqualTo(piece);
		}
	}

	@Test
	void testValueOf() {
		assertThat(Piece.valueOf(WHITE, PAWN)).isEqualTo(WHITE_PAWN);
		assertThat(Piece.valueOf(WHITE, KNIGHT)).isEqualTo(WHITE_KNIGHT);
		assertThat(Piece.valueOf(WHITE, BISHOP)).isEqualTo(WHITE_BISHOP);
		assertThat(Piece.valueOf(WHITE, ROOK)).isEqualTo(WHITE_ROOK);
		assertThat(Piece.valueOf(WHITE, QUEEN)).isEqualTo(WHITE_QUEEN);
		assertThat(Piece.valueOf(WHITE, KING)).isEqualTo(WHITE_KING);
		assertThat(Piece.valueOf(BLACK, PAWN)).isEqualTo(BLACK_PAWN);
		assertThat(Piece.valueOf(BLACK, KNIGHT)).isEqualTo(BLACK_KNIGHT);
		assertThat(Piece.valueOf(BLACK, BISHOP)).isEqualTo(BLACK_BISHOP);
		assertThat(Piece.valueOf(BLACK, ROOK)).isEqualTo(BLACK_ROOK);
		assertThat(Piece.valueOf(BLACK, QUEEN)).isEqualTo(BLACK_QUEEN);
		assertThat(Piece.valueOf(BLACK, KING)).isEqualTo(BLACK_KING);
	}

	@Test
	void testGetType() {
		assertThat(Piece.getType(WHITE_PAWN)).isEqualTo(PAWN);
		assertThat(Piece.getType(BLACK_PAWN)).isEqualTo(PAWN);
		assertThat(Piece.getType(WHITE_KNIGHT)).isEqualTo(KNIGHT);
		assertThat(Piece.getType(BLACK_KNIGHT)).isEqualTo(KNIGHT);
		assertThat(Piece.getType(WHITE_BISHOP)).isEqualTo(BISHOP);
		assertThat(Piece.getType(BLACK_BISHOP)).isEqualTo(BISHOP);
		assertThat(Piece.getType(WHITE_ROOK)).isEqualTo(ROOK);
		assertThat(Piece.getType(BLACK_ROOK)).isEqualTo(ROOK);
		assertThat(Piece.getType(WHITE_QUEEN)).isEqualTo(QUEEN);
		assertThat(Piece.getType(BLACK_QUEEN)).isEqualTo(QUEEN);
		assertThat(Piece.getType(WHITE_KING)).isEqualTo(KING);
		assertThat(Piece.getType(BLACK_KING)).isEqualTo(KING);
	}

	@Test
	void testGetColor() {
		assertThat(Piece.getColor(WHITE_PAWN)).isEqualTo(WHITE);
		assertThat(Piece.getColor(BLACK_PAWN)).isEqualTo(BLACK);
		assertThat(Piece.getColor(WHITE_KNIGHT)).isEqualTo(WHITE);
		assertThat(Piece.getColor(BLACK_KNIGHT)).isEqualTo(BLACK);
		assertThat(Piece.getColor(WHITE_BISHOP)).isEqualTo(WHITE);
		assertThat(Piece.getColor(BLACK_BISHOP)).isEqualTo(BLACK);
		assertThat(Piece.getColor(WHITE_ROOK)).isEqualTo(WHITE);
		assertThat(Piece.getColor(BLACK_ROOK)).isEqualTo(BLACK);
		assertThat(Piece.getColor(WHITE_QUEEN)).isEqualTo(WHITE);
		assertThat(Piece.getColor(BLACK_QUEEN)).isEqualTo(BLACK);
		assertThat(Piece.getColor(WHITE_KING)).isEqualTo(WHITE);
		assertThat(Piece.getColor(BLACK_KING)).isEqualTo(BLACK);
	}
}
