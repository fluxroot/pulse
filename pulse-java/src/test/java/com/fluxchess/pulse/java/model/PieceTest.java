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
		assertThat(Piece.valueOf(WHITE, PieceType.PAWN)).isEqualTo(Piece.WHITE_PAWN);
		assertThat(Piece.valueOf(WHITE, PieceType.KNIGHT)).isEqualTo(Piece.WHITE_KNIGHT);
		assertThat(Piece.valueOf(WHITE, PieceType.BISHOP)).isEqualTo(Piece.WHITE_BISHOP);
		assertThat(Piece.valueOf(WHITE, PieceType.ROOK)).isEqualTo(Piece.WHITE_ROOK);
		assertThat(Piece.valueOf(WHITE, PieceType.QUEEN)).isEqualTo(Piece.WHITE_QUEEN);
		assertThat(Piece.valueOf(WHITE, PieceType.KING)).isEqualTo(Piece.WHITE_KING);
		assertThat(Piece.valueOf(BLACK, PieceType.PAWN)).isEqualTo(Piece.BLACK_PAWN);
		assertThat(Piece.valueOf(BLACK, PieceType.KNIGHT)).isEqualTo(Piece.BLACK_KNIGHT);
		assertThat(Piece.valueOf(BLACK, PieceType.BISHOP)).isEqualTo(Piece.BLACK_BISHOP);
		assertThat(Piece.valueOf(BLACK, PieceType.ROOK)).isEqualTo(Piece.BLACK_ROOK);
		assertThat(Piece.valueOf(BLACK, PieceType.QUEEN)).isEqualTo(Piece.BLACK_QUEEN);
		assertThat(Piece.valueOf(BLACK, PieceType.KING)).isEqualTo(Piece.BLACK_KING);
	}

	@Test
	void testGetType() {
		assertThat(Piece.getType(Piece.WHITE_PAWN)).isEqualTo(PieceType.PAWN);
		assertThat(Piece.getType(Piece.BLACK_PAWN)).isEqualTo(PieceType.PAWN);
		assertThat(Piece.getType(Piece.WHITE_KNIGHT)).isEqualTo(PieceType.KNIGHT);
		assertThat(Piece.getType(Piece.BLACK_KNIGHT)).isEqualTo(PieceType.KNIGHT);
		assertThat(Piece.getType(Piece.WHITE_BISHOP)).isEqualTo(PieceType.BISHOP);
		assertThat(Piece.getType(Piece.BLACK_BISHOP)).isEqualTo(PieceType.BISHOP);
		assertThat(Piece.getType(Piece.WHITE_ROOK)).isEqualTo(PieceType.ROOK);
		assertThat(Piece.getType(Piece.BLACK_ROOK)).isEqualTo(PieceType.ROOK);
		assertThat(Piece.getType(Piece.WHITE_QUEEN)).isEqualTo(PieceType.QUEEN);
		assertThat(Piece.getType(Piece.BLACK_QUEEN)).isEqualTo(PieceType.QUEEN);
		assertThat(Piece.getType(Piece.WHITE_KING)).isEqualTo(PieceType.KING);
		assertThat(Piece.getType(Piece.BLACK_KING)).isEqualTo(PieceType.KING);
	}

	@Test
	void testGetColor() {
		assertThat(Piece.getColor(Piece.WHITE_PAWN)).isEqualTo(WHITE);
		assertThat(Piece.getColor(Piece.BLACK_PAWN)).isEqualTo(BLACK);
		assertThat(Piece.getColor(Piece.WHITE_KNIGHT)).isEqualTo(WHITE);
		assertThat(Piece.getColor(Piece.BLACK_KNIGHT)).isEqualTo(BLACK);
		assertThat(Piece.getColor(Piece.WHITE_BISHOP)).isEqualTo(WHITE);
		assertThat(Piece.getColor(Piece.BLACK_BISHOP)).isEqualTo(BLACK);
		assertThat(Piece.getColor(Piece.WHITE_ROOK)).isEqualTo(WHITE);
		assertThat(Piece.getColor(Piece.BLACK_ROOK)).isEqualTo(BLACK);
		assertThat(Piece.getColor(Piece.WHITE_QUEEN)).isEqualTo(WHITE);
		assertThat(Piece.getColor(Piece.BLACK_QUEEN)).isEqualTo(BLACK);
		assertThat(Piece.getColor(Piece.WHITE_KING)).isEqualTo(WHITE);
		assertThat(Piece.getColor(Piece.BLACK_KING)).isEqualTo(BLACK);
	}
}
