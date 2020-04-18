/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "movegenerator.h"
#include "rank.h"

namespace pulse {

MoveList<MoveEntry>& MoveGenerator::getLegalMoves(Position& position, int depth, bool isCheck) {
	MoveList<MoveEntry>& legalMoves = getMoves(position, depth, isCheck);

	int size = legalMoves.size;
	legalMoves.size = 0;
	for (int i = 0; i < size; i++) {
		int move = legalMoves.entries[i]->move;

		position.makeMove(move);
		if (!position.isCheck(color::opposite(position.activeColor))) {
			legalMoves.entries[legalMoves.size++]->move = move;
		}
		position.undoMove(move);
	}

	return legalMoves;
}

MoveList<MoveEntry>& MoveGenerator::getMoves(Position& position, int depth, bool isCheck) {
	moves.size = 0;

	if (depth > 0) {
		// Generate main moves

		addMoves(moves, position);

		if (!isCheck) {
			int square = bitboard::next(position.pieces[position.activeColor][piecetype::KING]);
			addCastlingMoves(moves, square, position);
		}
	} else {
		// Generate quiescent moves

		addMoves(moves, position);

		if (!isCheck) {
			int size = moves.size;
			moves.size = 0;
			for (int i = 0; i < size; i++) {
				if (move::getTargetPiece(moves.entries[i]->move) != piece::NOPIECE) {
					// Add only capturing moves
					moves.entries[moves.size++]->move = moves.entries[i]->move;
				}
			}
		}
	}

	moves.rateFromMVVLVA();
	moves.sort();

	return moves;
}

void MoveGenerator::addMoves(MoveList<MoveEntry>& list, Position& position) {
	int activeColor = position.activeColor;

	for (auto squares = position.pieces[activeColor][piecetype::PAWN];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		addPawnMoves(list, square, position);
	}
	for (auto squares = position.pieces[activeColor][piecetype::KNIGHT];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		addMoves(list, square, square::knightDirections, position);
	}
	for (auto squares = position.pieces[activeColor][piecetype::BISHOP];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		addMoves(list, square, square::bishopDirections, position);
	}
	for (auto squares = position.pieces[activeColor][piecetype::ROOK];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		addMoves(list, square, square::rookDirections, position);
	}
	for (auto squares = position.pieces[activeColor][piecetype::QUEEN];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		addMoves(list, square, square::queenDirections, position);
	}
	int square = bitboard::next(position.pieces[activeColor][piecetype::KING]);
	addMoves(list, square, square::kingDirections, position);
}

void MoveGenerator::addMoves(MoveList<MoveEntry>& list, int originSquare, const std::vector<int>& directions,
							 Position& position) {
	int originPiece = position.board[originSquare];
	bool sliding = piecetype::isSliding(piece::getType(originPiece));
	int oppositeColor = color::opposite(piece::getColor(originPiece));

	// Go through all move directions for this piece
	for (auto direction : directions) {
		int targetSquare = originSquare + direction;

		// Check if we're still on the board
		while (square::isValid(targetSquare)) {
			int targetPiece = position.board[targetSquare];

			if (targetPiece == piece::NOPIECE) {
				// quiet move
				list.entries[list.size++]->move = move::valueOf(
						movetype::NORMAL, originSquare, targetSquare, originPiece, piece::NOPIECE,
						piecetype::NOPIECETYPE);

				if (!sliding) {
					break;
				}

				targetSquare += direction;
			} else {
				if (piece::getColor(targetPiece) == oppositeColor) {
					// capturing move
					list.entries[list.size++]->move = move::valueOf(
							movetype::NORMAL, originSquare, targetSquare, originPiece, targetPiece,
							piecetype::NOPIECETYPE);
				}

				break;
			}
		}
	}
}

void MoveGenerator::addPawnMoves(MoveList<MoveEntry>& list, int pawnSquare, Position& position) {
	int pawnPiece = position.board[pawnSquare];
	int pawnColor = piece::getColor(pawnPiece);

	// Generate only capturing moves first (i = 1)
	for (unsigned int i = 1; i < square::pawnDirections[pawnColor].size(); i++) {
		int direction = square::pawnDirections[pawnColor][i];

		int targetSquare = pawnSquare + direction;
		if (square::isValid(targetSquare)) {
			int targetPiece = position.board[targetSquare];

			if (targetPiece != piece::NOPIECE) {
				if (piece::getColor(targetPiece) == color::opposite(pawnColor)) {
					// Capturing move

					if ((pawnColor == color::WHITE && square::getRank(targetSquare) == rank::r8)
						|| (pawnColor == color::BLACK && square::getRank(targetSquare) == rank::r1)) {
						// Pawn promotion capturing move

						list.entries[list.size++]->move = move::valueOf(
								movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece,
								piecetype::QUEEN);
						list.entries[list.size++]->move = move::valueOf(
								movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece,
								piecetype::ROOK);
						list.entries[list.size++]->move = move::valueOf(
								movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece,
								piecetype::BISHOP);
						list.entries[list.size++]->move = move::valueOf(
								movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece,
								piecetype::KNIGHT);
					} else {
						// Normal capturing move

						list.entries[list.size++]->move = move::valueOf(
								movetype::NORMAL, pawnSquare, targetSquare, pawnPiece, targetPiece,
								piecetype::NOPIECETYPE);
					}
				}
			} else if (targetSquare == position.enPassantSquare) {
				// En passant move
				int captureSquare = targetSquare + (pawnColor == color::WHITE ? square::S : square::N);
				targetPiece = position.board[captureSquare];

				list.entries[list.size++]->move = move::valueOf(
						movetype::ENPASSANT, pawnSquare, targetSquare, pawnPiece, targetPiece, piecetype::NOPIECETYPE);
			}
		}
	}

	// Generate non-capturing moves
	int direction = square::pawnDirections[pawnColor][0];

	// Move one rank forward
	int targetSquare = pawnSquare + direction;
	if (square::isValid(targetSquare) && position.board[targetSquare] == piece::NOPIECE) {
		if ((pawnColor == color::WHITE && square::getRank(targetSquare) == rank::r8)
			|| (pawnColor == color::BLACK && square::getRank(targetSquare) == rank::r1)) {
			// Pawn promotion move

			list.entries[list.size++]->move = move::valueOf(
					movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE, piecetype::QUEEN);
			list.entries[list.size++]->move = move::valueOf(
					movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE, piecetype::ROOK);
			list.entries[list.size++]->move = move::valueOf(
					movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE, piecetype::BISHOP);
			list.entries[list.size++]->move = move::valueOf(
					movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE, piecetype::KNIGHT);
		} else {
			// Normal move

			list.entries[list.size++]->move = move::valueOf(
					movetype::NORMAL, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE, piecetype::NOPIECETYPE);

			// Move another rank forward
			targetSquare += direction;
			if (square::isValid(targetSquare) && position.board[targetSquare] == piece::NOPIECE) {
				if ((pawnColor == color::WHITE && square::getRank(targetSquare) == rank::r4)
					|| (pawnColor == color::BLACK && square::getRank(targetSquare) == rank::r5)) {
					// Pawn double move

					list.entries[list.size++]->move = move::valueOf(
							movetype::PAWNDOUBLE, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE,
							piecetype::NOPIECETYPE);
				}
			}
		}
	}
}

void MoveGenerator::addCastlingMoves(MoveList<MoveEntry>& list, int kingSquare, Position& position) {
	int kingPiece = position.board[kingSquare];

	if (piece::getColor(kingPiece) == color::WHITE) {
		// Do not test g1 whether it is attacked as we will test it in isLegal()
		if ((position.castlingRights & castling::WHITE_KINGSIDE) != castling::NOCASTLING
			&& position.board[square::f1] == piece::NOPIECE
			&& position.board[square::g1] == piece::NOPIECE
			&& !position.isAttacked(square::f1, color::BLACK)) {
			list.entries[list.size++]->move = move::valueOf(
					movetype::CASTLING, kingSquare, square::g1, kingPiece, piece::NOPIECE, piecetype::NOPIECETYPE);
		}
		// Do not test c1 whether it is attacked as we will test it in isLegal()
		if ((position.castlingRights & castling::WHITE_QUEENSIDE) != castling::NOCASTLING
			&& position.board[square::b1] == piece::NOPIECE
			&& position.board[square::c1] == piece::NOPIECE
			&& position.board[square::d1] == piece::NOPIECE
			&& !position.isAttacked(square::d1, color::BLACK)) {
			list.entries[list.size++]->move = move::valueOf(
					movetype::CASTLING, kingSquare, square::c1, kingPiece, piece::NOPIECE, piecetype::NOPIECETYPE);
		}
	} else {
		// Do not test g8 whether it is attacked as we will test it in isLegal()
		if ((position.castlingRights & castling::BLACK_KINGSIDE) != castling::NOCASTLING
			&& position.board[square::f8] == piece::NOPIECE
			&& position.board[square::g8] == piece::NOPIECE
			&& !position.isAttacked(square::f8, color::WHITE)) {
			list.entries[list.size++]->move = move::valueOf(
					movetype::CASTLING, kingSquare, square::g8, kingPiece, piece::NOPIECE, piecetype::NOPIECETYPE);
		}
		// Do not test c8 whether it is attacked as we will test it in isLegal()
		if ((position.castlingRights & castling::BLACK_QUEENSIDE) != castling::NOCASTLING
			&& position.board[square::b8] == piece::NOPIECE
			&& position.board[square::c8] == piece::NOPIECE
			&& position.board[square::d8] == piece::NOPIECE
			&& !position.isAttacked(square::d8, color::WHITE)) {
			list.entries[list.size++]->move = move::valueOf(
					movetype::CASTLING, kingSquare, square::c8, kingPiece, piece::NOPIECE, piecetype::NOPIECETYPE);
		}
	}
}
}
