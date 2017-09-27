/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static com.fluxchess.pulse.Color.BLACK;
import static com.fluxchess.pulse.Color.WHITE;
import static com.fluxchess.pulse.Piece.*;
import static com.fluxchess.pulse.PieceType.*;
import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PieceTest {

	@Test
	public void testUtilityClass() throws Exception {
		assertUtilityClassWellDefined(Piece.class);
	}

	@Test
	public void testValues() {
		for (int piece : Piece.values) {
			assertThat(Piece.values[piece], is(piece));
		}
	}

	@Test
	public void testValueOf() {
		assertThat(Piece.valueOf(Color.WHITE, PieceType.PAWN), is(Piece.WHITE_PAWN));
		assertThat(Piece.valueOf(Color.WHITE, PieceType.KNIGHT), is(Piece.WHITE_KNIGHT));
		assertThat(Piece.valueOf(Color.WHITE, PieceType.BISHOP), is(Piece.WHITE_BISHOP));
		assertThat(Piece.valueOf(Color.WHITE, PieceType.ROOK), is(Piece.WHITE_ROOK));
		assertThat(Piece.valueOf(Color.WHITE, PieceType.QUEEN), is(Piece.WHITE_QUEEN));
		assertThat(Piece.valueOf(Color.WHITE, PieceType.KING), is(Piece.WHITE_KING));
		assertThat(Piece.valueOf(Color.BLACK, PieceType.PAWN), is(Piece.BLACK_PAWN));
		assertThat(Piece.valueOf(Color.BLACK, PieceType.KNIGHT), is(Piece.BLACK_KNIGHT));
		assertThat(Piece.valueOf(Color.BLACK, PieceType.BISHOP), is(Piece.BLACK_BISHOP));
		assertThat(Piece.valueOf(Color.BLACK, PieceType.ROOK), is(Piece.BLACK_ROOK));
		assertThat(Piece.valueOf(Color.BLACK, PieceType.QUEEN), is(Piece.BLACK_QUEEN));
		assertThat(Piece.valueOf(Color.BLACK, PieceType.KING), is(Piece.BLACK_KING));
	}

	@Test
	public void testGetType() {
		assertThat(Piece.getType(WHITE_PAWN), is(PAWN));
		assertThat(Piece.getType(BLACK_PAWN), is(PAWN));
		assertThat(Piece.getType(WHITE_KNIGHT), is(KNIGHT));
		assertThat(Piece.getType(BLACK_KNIGHT), is(KNIGHT));
		assertThat(Piece.getType(WHITE_BISHOP), is(BISHOP));
		assertThat(Piece.getType(BLACK_BISHOP), is(BISHOP));
		assertThat(Piece.getType(WHITE_ROOK), is(ROOK));
		assertThat(Piece.getType(BLACK_ROOK), is(ROOK));
		assertThat(Piece.getType(WHITE_QUEEN), is(QUEEN));
		assertThat(Piece.getType(BLACK_QUEEN), is(QUEEN));
		assertThat(Piece.getType(WHITE_KING), is(KING));
		assertThat(Piece.getType(BLACK_KING), is(KING));
	}

	@Test
	public void testGetColor() {
		assertThat(Piece.getColor(WHITE_PAWN), is(WHITE));
		assertThat(Piece.getColor(BLACK_PAWN), is(BLACK));
		assertThat(Piece.getColor(WHITE_KNIGHT), is(WHITE));
		assertThat(Piece.getColor(BLACK_KNIGHT), is(BLACK));
		assertThat(Piece.getColor(WHITE_BISHOP), is(WHITE));
		assertThat(Piece.getColor(BLACK_BISHOP), is(BLACK));
		assertThat(Piece.getColor(WHITE_ROOK), is(WHITE));
		assertThat(Piece.getColor(BLACK_ROOK), is(BLACK));
		assertThat(Piece.getColor(WHITE_QUEEN), is(WHITE));
		assertThat(Piece.getColor(BLACK_QUEEN), is(BLACK));
		assertThat(Piece.getColor(WHITE_KING), is(WHITE));
		assertThat(Piece.getColor(BLACK_KING), is(BLACK));
	}
}
