/*
 * Copyright 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.pulse.model.File;
import com.fluxchess.pulse.model.Square;
import org.junit.jupiter.api.Test;

import static com.fluxchess.pulse.model.Castling.*;
import static com.fluxchess.pulse.model.Color.BLACK;
import static com.fluxchess.pulse.model.Color.WHITE;
import static com.fluxchess.pulse.model.Piece.*;
import static com.fluxchess.pulse.model.PieceType.*;
import static com.fluxchess.pulse.model.Rank.r2;
import static com.fluxchess.pulse.model.Rank.r7;
import static com.fluxchess.pulse.model.Square.*;
import static org.assertj.core.api.Assertions.assertThat;

class NotationTest {

	@Test
	void testStandardPosition() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);

		// Test pawns
		for (int file : File.values) {
			assertThat(position.board[Square.valueOf(file, r2)]).isEqualTo(WHITE_PAWN);
			assertThat(position.board[Square.valueOf(file, r7)]).isEqualTo(BLACK_PAWN);
		}

		// Test knights
		assertThat(position.board[b1]).isEqualTo(WHITE_KNIGHT);
		assertThat(position.board[g1]).isEqualTo(WHITE_KNIGHT);
		assertThat(position.board[b8]).isEqualTo(BLACK_KNIGHT);
		assertThat(position.board[g8]).isEqualTo(BLACK_KNIGHT);

		// Test bishops
		assertThat(position.board[c1]).isEqualTo(WHITE_BISHOP);
		assertThat(position.board[f1]).isEqualTo(WHITE_BISHOP);
		assertThat(position.board[c8]).isEqualTo(BLACK_BISHOP);
		assertThat(position.board[f8]).isEqualTo(BLACK_BISHOP);

		// Test rooks
		assertThat(position.board[a1]).isEqualTo(WHITE_ROOK);
		assertThat(position.board[h1]).isEqualTo(WHITE_ROOK);
		assertThat(position.board[a8]).isEqualTo(BLACK_ROOK);
		assertThat(position.board[h8]).isEqualTo(BLACK_ROOK);

		// Test queens
		assertThat(position.board[d1]).isEqualTo(WHITE_QUEEN);
		assertThat(position.board[d8]).isEqualTo(BLACK_QUEEN);

		// Test kings
		assertThat(position.board[e1]).isEqualTo(WHITE_KING);
		assertThat(position.board[e8]).isEqualTo(BLACK_KING);

		assertThat(position.material[WHITE]).isEqualTo((8 * PAWN_VALUE)
				+ (2 * KNIGHT_VALUE)
				+ (2 * BISHOP_VALUE)
				+ (2 * ROOK_VALUE)
				+ QUEEN_VALUE
				+ KING_VALUE);
		assertThat(position.material[BLACK]).isEqualTo((8 * PAWN_VALUE)
				+ (2 * KNIGHT_VALUE)
				+ (2 * BISHOP_VALUE)
				+ (2 * ROOK_VALUE)
				+ QUEEN_VALUE
				+ KING_VALUE);

		// Test castling
		assertThat(position.castlingRights & WHITE_KINGSIDE).isNotEqualTo(NOCASTLING);
		assertThat(position.castlingRights & WHITE_QUEENSIDE).isNotEqualTo(NOCASTLING);
		assertThat(position.castlingRights & BLACK_KINGSIDE).isNotEqualTo(NOCASTLING);
		assertThat(position.castlingRights & BLACK_QUEENSIDE).isNotEqualTo(NOCASTLING);

		// Test en passant
		assertThat(position.enPassantSquare).isEqualTo(NOSQUARE);

		// Test active color
		assertThat(position.activeColor).isEqualTo(WHITE);

		// Test half move clock
		assertThat(position.halfmoveClock).isEqualTo(0);

		// Test full move number
		assertThat(position.getFullmoveNumber()).isEqualTo(1);
	}
}
