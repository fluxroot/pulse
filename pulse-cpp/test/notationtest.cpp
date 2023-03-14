// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "notation.h"
#include "model/file.h"
#include "model/rank.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(notationtest, testStandardPosition) {
	Position position(notation::toPosition(notation::STANDARDPOSITION));

	// Test pawns
	for (auto file: file::values) {
		EXPECT_EQ(+piece::WHITE_PAWN, position.board[square::valueOf(file, rank::r2)]);
		EXPECT_EQ(+piece::BLACK_PAWN, position.board[square::valueOf(file, rank::r7)]);
	}

	// Test knights
	EXPECT_EQ(+piece::WHITE_KNIGHT, position.board[square::b1]);
	EXPECT_EQ(+piece::WHITE_KNIGHT, position.board[square::g1]);
	EXPECT_EQ(+piece::BLACK_KNIGHT, position.board[square::b8]);
	EXPECT_EQ(+piece::BLACK_KNIGHT, position.board[square::g8]);

	// Test bishops
	EXPECT_EQ(+piece::WHITE_BISHOP, position.board[square::c1]);
	EXPECT_EQ(+piece::WHITE_BISHOP, position.board[square::f1]);
	EXPECT_EQ(+piece::BLACK_BISHOP, position.board[square::c8]);
	EXPECT_EQ(+piece::BLACK_BISHOP, position.board[square::f8]);

	// Test rooks
	EXPECT_EQ(+piece::WHITE_ROOK, position.board[square::a1]);
	EXPECT_EQ(+piece::WHITE_ROOK, position.board[square::h1]);
	EXPECT_EQ(+piece::BLACK_ROOK, position.board[square::a8]);
	EXPECT_EQ(+piece::BLACK_ROOK, position.board[square::h8]);

	// Test queens
	EXPECT_EQ(+piece::WHITE_QUEEN, position.board[square::d1]);
	EXPECT_EQ(+piece::BLACK_QUEEN, position.board[square::d8]);

	// Test kings
	EXPECT_EQ(+piece::WHITE_KING, position.board[square::e1]);
	EXPECT_EQ(+piece::BLACK_KING, position.board[square::e8]);

	EXPECT_EQ(8 * piecetype::PAWN_VALUE
			  + 2 * piecetype::KNIGHT_VALUE
			  + 2 * piecetype::BISHOP_VALUE
			  + 2 * piecetype::ROOK_VALUE
			  + piecetype::QUEEN_VALUE
			  + piecetype::KING_VALUE,
			position.material[color::WHITE]);
	EXPECT_EQ(8 * piecetype::PAWN_VALUE
			  + 2 * piecetype::KNIGHT_VALUE
			  + 2 * piecetype::BISHOP_VALUE
			  + 2 * piecetype::ROOK_VALUE
			  + piecetype::QUEEN_VALUE
			  + piecetype::KING_VALUE,
			position.material[color::BLACK]);

	// Test castling
	EXPECT_NE(+castling::NOCASTLING, position.castlingRights & castling::WHITE_KINGSIDE);
	EXPECT_NE(+castling::NOCASTLING, position.castlingRights & castling::WHITE_QUEENSIDE);
	EXPECT_NE(+castling::NOCASTLING, position.castlingRights & castling::BLACK_KINGSIDE);
	EXPECT_NE(+castling::NOCASTLING, position.castlingRights & castling::BLACK_QUEENSIDE);

	// Test en passant
	EXPECT_EQ(+square::NOSQUARE, position.enPassantSquare);

	// Test active color
	EXPECT_EQ(+color::WHITE, position.activeColor);

	// Test half move clock
	EXPECT_EQ(0, position.halfmoveClock);

	// Test full move number
	EXPECT_EQ(1, position.getFullmoveNumber());
}
