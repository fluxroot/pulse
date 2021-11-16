/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "notation.h"
#include "model/move.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(positiontest, testEquals) {
	// Standard position test
	Position position1(notation::toPosition(notation::STANDARDPOSITION));
	Position position2(notation::toPosition(notation::STANDARDPOSITION));

	// reflexive test
	EXPECT_EQ(position1, position1);

	// symmetric test
	EXPECT_EQ(position1, position2);
	EXPECT_EQ(position2, position1);

	// FEN test
	Position position3(notation::toPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
	EXPECT_EQ(position1, position3);

	Position position4(notation::toPosition("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1"));
	Position position5(notation::toPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1"));
	Position position6(notation::toPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1"));
	Position position7(notation::toPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1 1"));
	Position position8(notation::toPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 2"));

	EXPECT_NE(position1, position4);
	EXPECT_NE(position1, position5);
	EXPECT_NE(position1, position6);
	EXPECT_NE(position1, position7);
	EXPECT_NE(position1, position8);
}

TEST(positiontest, testToString) {
	std::string fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

	Position position(notation::toPosition(fen));

	EXPECT_EQ(fen, notation::fromPosition(position));
}

TEST(positiontest, testActiveColor) {
	Position position(notation::toPosition(notation::STANDARDPOSITION));

	// Move white pawn
	int move = move::valueOf(movetype::NORMAL, square::a2, square::a3, piece::WHITE_PAWN, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);
	EXPECT_EQ(+color::BLACK, position.activeColor);

	// Move black pawn
	move = move::valueOf(movetype::NORMAL, square::b7, square::b6, piece::BLACK_PAWN, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);
	EXPECT_EQ(+color::WHITE, position.activeColor);
}

TEST(positiontest, testHalfMoveClock) {
	Position position(notation::toPosition(notation::STANDARDPOSITION));

	// Move white pawn
	int move = move::valueOf(movetype::NORMAL, square::a2, square::a3, piece::WHITE_PAWN, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);
	EXPECT_EQ(0, position.halfmoveClock);

	// Move black pawn
	move = move::valueOf(movetype::NORMAL, square::b7, square::b6, piece::BLACK_PAWN, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);

	// Move white knight
	move = move::valueOf(movetype::NORMAL, square::b1, square::c3, piece::WHITE_KNIGHT, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);
	EXPECT_EQ(1, position.halfmoveClock);
}

TEST(positiontest, testFullMoveNumber) {
	Position position(notation::toPosition(notation::STANDARDPOSITION));

	// Move white pawn
	int move = move::valueOf(movetype::NORMAL, square::a2, square::a3, piece::WHITE_PAWN, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);
	EXPECT_EQ(1, position.getFullmoveNumber());

	// Move black pawn
	move = move::valueOf(movetype::NORMAL, square::b7, square::b6, piece::BLACK_PAWN, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);
	EXPECT_EQ(2, position.getFullmoveNumber());
}

TEST(positiontest, testIsRepetition) {
	Position position(notation::toPosition(notation::STANDARDPOSITION));

	// Move white knight
	int move = move::valueOf(movetype::NORMAL, square::b1, square::c3, piece::WHITE_KNIGHT, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);

	// Move black knight
	move = move::valueOf(movetype::NORMAL, square::b8, square::c6, piece::BLACK_KNIGHT, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);

	// Move white knight
	move = move::valueOf(movetype::NORMAL, square::g1, square::f3, piece::WHITE_KNIGHT, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);

	// Move black knight
	move = move::valueOf(movetype::NORMAL, square::c6, square::b8, piece::BLACK_KNIGHT, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);

	// Move white knight
	move = move::valueOf(movetype::NORMAL, square::f3, square::g1, piece::WHITE_KNIGHT, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);

	EXPECT_TRUE(position.isRepetition());
}

TEST(positiontest, testHasInsufficientMaterial) {
	Position position(notation::toPosition("8/4k3/8/8/8/8/2K5/8 w - - 0 1"));
	EXPECT_TRUE(position.hasInsufficientMaterial());

	position = notation::toPosition("8/4k3/8/2B5/8/8/2K5/8 b - - 0 1");
	EXPECT_TRUE(position.hasInsufficientMaterial());

	position = notation::toPosition("8/4k3/8/2B3n1/8/8/2K5/8 b - - 0 1");
	EXPECT_TRUE(position.hasInsufficientMaterial());
}

TEST(positiontest, testNormalMove) {
	Position position(notation::toPosition(notation::STANDARDPOSITION));
	uint64_t zobristKey = position.zobristKey;

	int move = move::valueOf(movetype::NORMAL, square::a2, square::a3, piece::WHITE_PAWN, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);
	position.undoMove(move);

	EXPECT_EQ(notation::STANDARDPOSITION, notation::fromPosition(position));
	EXPECT_EQ(zobristKey, position.zobristKey);
}

TEST(positiontest, testPawnDoubleMove) {
	Position position(notation::toPosition(notation::STANDARDPOSITION));
	uint64_t zobristKey = position.zobristKey;

	int move = move::valueOf(movetype::PAWNDOUBLE, square::a2, square::a4, piece::WHITE_PAWN, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);

	EXPECT_EQ(+square::a3, position.enPassantSquare);

	position.undoMove(move);

	EXPECT_EQ(notation::STANDARDPOSITION, notation::fromPosition(position));
	EXPECT_EQ(zobristKey, position.zobristKey);
}

TEST(positiontest, testPawnPromotionMove) {
	Position position(notation::toPosition("8/P5k1/8/8/2K5/8/8/8 w - - 0 1"));
	uint64_t zobristKey = position.zobristKey;

	int move = move::valueOf(movetype::PAWNPROMOTION, square::a7, square::a8, piece::WHITE_PAWN, piece::NOPIECE,
			piecetype::QUEEN);
	position.makeMove(move);

	EXPECT_EQ(+piece::WHITE_QUEEN, position.board[square::a8]);

	position.undoMove(move);

	EXPECT_EQ("8/P5k1/8/8/2K5/8/8/8 w - - 0 1", notation::fromPosition(position));
	EXPECT_EQ(zobristKey, position.zobristKey);
}

TEST(positiontest, testEnPassantMove) {
	Position position(notation::toPosition("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1"));
	uint64_t zobristKey = position.zobristKey;

	// Make en passant move
	int move = move::valueOf(movetype::ENPASSANT, square::e4, square::d3, piece::BLACK_PAWN, piece::WHITE_PAWN,
			piecetype::NOPIECETYPE);
	position.makeMove(move);

	EXPECT_EQ(+piece::NOPIECE, position.board[square::d4]);
	EXPECT_EQ(+piece::BLACK_PAWN, position.board[square::d3]);
	EXPECT_EQ(+square::NOSQUARE, position.enPassantSquare);

	position.undoMove(move);

	EXPECT_EQ("5k2/8/8/8/3Pp3/8/8/3K4 b - d3 0 1", notation::fromPosition(position));
	EXPECT_EQ(zobristKey, position.zobristKey);
}

TEST(positiontest, testCastlingMove) {
	Position position(notation::toPosition("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1"));
	uint64_t zobristKey = position.zobristKey;

	int move = move::valueOf(movetype::CASTLING, square::e1, square::c1, piece::WHITE_KING, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);

	EXPECT_EQ(+castling::NOCASTLING, position.castlingRights & castling::WHITE_QUEENSIDE);

	position.undoMove(move);

	EXPECT_EQ("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", notation::fromPosition(position));
	EXPECT_EQ(zobristKey, position.zobristKey);

	position = notation::toPosition("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
	zobristKey = position.zobristKey;

	move = move::valueOf(movetype::CASTLING, square::e1, square::g1, piece::WHITE_KING, piece::NOPIECE,
			piecetype::NOPIECETYPE);
	position.makeMove(move);

	EXPECT_EQ(+castling::NOCASTLING, position.castlingRights & castling::WHITE_KINGSIDE);

	position.undoMove(move);

	EXPECT_EQ("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", notation::fromPosition(position));
	EXPECT_EQ(zobristKey, position.zobristKey);
}
