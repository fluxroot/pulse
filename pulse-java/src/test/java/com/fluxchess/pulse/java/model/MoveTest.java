/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java.model;

import org.junit.jupiter.api.Test;

import static com.fluxchess.pulse.java.model.MoveType.PAWNPROMOTION;
import static com.fluxchess.pulse.java.model.Square.a7;
import static com.fluxchess.pulse.java.model.Square.b7;
import static com.fluxchess.pulse.java.model.Square.b8;
import static com.fluxchess.pulse.java.model.Square.c8;
import static org.assertj.core.api.Assertions.assertThat;

class MoveTest {

	@Test
	void testCreation() {
		int move = Move.valueOf(PAWNPROMOTION, a7, b8, Piece.WHITE_PAWN, Piece.BLACK_QUEEN, PieceType.KNIGHT);

		assertThat(Move.getType(move)).isEqualTo(PAWNPROMOTION);
		assertThat(Move.getOriginSquare(move)).isEqualTo(a7);
		assertThat(Move.getTargetSquare(move)).isEqualTo(b8);
		assertThat(Move.getOriginPiece(move)).isEqualTo(Piece.WHITE_PAWN);
		assertThat(Move.getTargetPiece(move)).isEqualTo(Piece.BLACK_QUEEN);
		assertThat(Move.getPromotion(move)).isEqualTo(PieceType.KNIGHT);
	}

	@Test
	void testPromotion() {
		int move = Move.valueOf(PAWNPROMOTION, b7, c8, Piece.WHITE_PAWN, Piece.BLACK_QUEEN, PieceType.KNIGHT);

		assertThat(Move.getPromotion(move)).isEqualTo(PieceType.KNIGHT);
	}
}
