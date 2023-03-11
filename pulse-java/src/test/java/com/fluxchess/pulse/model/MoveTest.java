/*
 * Copyright 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

import org.junit.jupiter.api.Test;

import static com.fluxchess.pulse.model.MoveType.PAWNPROMOTION;
import static com.fluxchess.pulse.model.Piece.BLACK_QUEEN;
import static com.fluxchess.pulse.model.Piece.WHITE_PAWN;
import static com.fluxchess.pulse.model.PieceType.KNIGHT;
import static com.fluxchess.pulse.model.Square.*;
import static org.assertj.core.api.Assertions.assertThat;

class MoveTest {

	@Test
	void testCreation() {
		int move = Move.valueOf(PAWNPROMOTION, a7, b8, WHITE_PAWN, BLACK_QUEEN, KNIGHT);

		assertThat(Move.getType(move)).isEqualTo(PAWNPROMOTION);
		assertThat(Move.getOriginSquare(move)).isEqualTo(a7);
		assertThat(Move.getTargetSquare(move)).isEqualTo(b8);
		assertThat(Move.getOriginPiece(move)).isEqualTo(WHITE_PAWN);
		assertThat(Move.getTargetPiece(move)).isEqualTo(BLACK_QUEEN);
		assertThat(Move.getPromotion(move)).isEqualTo(KNIGHT);
	}

	@Test
	void testPromotion() {
		int move = Move.valueOf(PAWNPROMOTION, b7, c8, WHITE_PAWN, BLACK_QUEEN, KNIGHT);

		assertThat(Move.getPromotion(move)).isEqualTo(KNIGHT);
	}
}
