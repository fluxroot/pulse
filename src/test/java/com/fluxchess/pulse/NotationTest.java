/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static com.fluxchess.pulse.Castling.*;
import static com.fluxchess.pulse.Color.BLACK;
import static com.fluxchess.pulse.Color.WHITE;
import static com.fluxchess.pulse.Piece.*;
import static com.fluxchess.pulse.PieceType.*;
import static com.fluxchess.pulse.Rank.r2;
import static com.fluxchess.pulse.Rank.r7;
import static com.fluxchess.pulse.Square.*;
import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class NotationTest {

	@Test
	public void testUtilityClass() throws Exception {
		assertUtilityClassWellDefined(Notation.class);
	}

	@Test
	public void testStandardPosition() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);

		// Test pawns
		for (int file : File.values) {
			assertThat(position.board[Square.valueOf(file, r2)], is(WHITE_PAWN));
			assertThat(position.board[Square.valueOf(file, r7)], is(BLACK_PAWN));
		}

		// Test knights
		assertThat(position.board[b1], is(WHITE_KNIGHT));
		assertThat(position.board[g1], is(WHITE_KNIGHT));
		assertThat(position.board[b8], is(BLACK_KNIGHT));
		assertThat(position.board[g8], is(BLACK_KNIGHT));

		// Test bishops
		assertThat(position.board[c1], is(WHITE_BISHOP));
		assertThat(position.board[f1], is(WHITE_BISHOP));
		assertThat(position.board[c8], is(BLACK_BISHOP));
		assertThat(position.board[f8], is(BLACK_BISHOP));

		// Test rooks
		assertThat(position.board[a1], is(WHITE_ROOK));
		assertThat(position.board[h1], is(WHITE_ROOK));
		assertThat(position.board[a8], is(BLACK_ROOK));
		assertThat(position.board[h8], is(BLACK_ROOK));

		// Test queens
		assertThat(position.board[d1], is(WHITE_QUEEN));
		assertThat(position.board[d8], is(BLACK_QUEEN));

		// Test kings
		assertThat(position.board[e1], is(WHITE_KING));
		assertThat(position.board[e8], is(BLACK_KING));

		assertThat(position.material[WHITE], is((8 * PAWN_VALUE)
				+ (2 * KNIGHT_VALUE)
				+ (2 * BISHOP_VALUE)
				+ (2 * ROOK_VALUE)
				+ QUEEN_VALUE
				+ KING_VALUE));
		assertThat(position.material[BLACK], is((8 * PAWN_VALUE)
				+ (2 * KNIGHT_VALUE)
				+ (2 * BISHOP_VALUE)
				+ (2 * ROOK_VALUE)
				+ QUEEN_VALUE
				+ KING_VALUE));

		// Test castling
		assertThat(position.castlingRights & WHITE_KINGSIDE, is(not(NOCASTLING)));
		assertThat(position.castlingRights & WHITE_QUEENSIDE, is(not(NOCASTLING)));
		assertThat(position.castlingRights & BLACK_KINGSIDE, is(not(NOCASTLING)));
		assertThat(position.castlingRights & BLACK_QUEENSIDE, is(not(NOCASTLING)));

		// Test en passant
		assertThat(position.enPassantSquare, is(NOSQUARE));

		// Test active color
		assertThat(position.activeColor, is(WHITE));

		// Test half move clock
		assertThat(position.halfmoveClock, is(0));

		// Test full move number
		assertThat(position.getFullmoveNumber(), is(1));
	}
}
