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
			int square = bitboard::next(position.pieces[position.activeColor][PieceType::KING]);
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

	for (auto squares = position.pieces[activeColor][PieceType::PAWN];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		addPawnMoves(list, square, position);
	}
	for (auto squares = position.pieces[activeColor][PieceType::KNIGHT];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		addMoves(list, square, Square::knightDirections, position);
	}
	for (auto squares = position.pieces[activeColor][PieceType::BISHOP];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		addMoves(list, square, Square::bishopDirections, position);
	}
	for (auto squares = position.pieces[activeColor][PieceType::ROOK];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		addMoves(list, square, Square::rookDirections, position);
	}
	for (auto squares = position.pieces[activeColor][PieceType::QUEEN];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		addMoves(list, square, Square::queenDirections, position);
	}
	int square = bitboard::next(position.pieces[activeColor][PieceType::KING]);
	addMoves(list, square, Square::kingDirections, position);
}

void MoveGenerator::addMoves(MoveList<MoveEntry>& list, int originSquare, const std::vector<int>& directions,
							 Position& position) {
	int originPiece = position.board[originSquare];
	bool sliding = PieceType::isSliding(piece::getType(originPiece));
	int oppositeColor = color::opposite(piece::getColor(originPiece));

	// Go through all move directions for this piece
	for (auto direction : directions) {
		int targetSquare = originSquare + direction;

		// Check if we're still on the board
		while (Square::isValid(targetSquare)) {
			int targetPiece = position.board[targetSquare];

			if (targetPiece == piece::NOPIECE) {
				// quiet move
				list.entries[list.size++]->move = move::valueOf(
						movetype::NORMAL, originSquare, targetSquare, originPiece, piece::NOPIECE,
						PieceType::NOPIECETYPE);

				if (!sliding) {
					break;
				}

				targetSquare += direction;
			} else {
				if (piece::getColor(targetPiece) == oppositeColor) {
					// capturing move
					list.entries[list.size++]->move = move::valueOf(
							movetype::NORMAL, originSquare, targetSquare, originPiece, targetPiece,
							PieceType::NOPIECETYPE);
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
	for (unsigned int i = 1; i < Square::pawnDirections[pawnColor].size(); i++) {
		int direction = Square::pawnDirections[pawnColor][i];

		int targetSquare = pawnSquare + direction;
		if (Square::isValid(targetSquare)) {
			int targetPiece = position.board[targetSquare];

			if (targetPiece != piece::NOPIECE) {
				if (piece::getColor(targetPiece) == color::opposite(pawnColor)) {
					// Capturing move

					if ((pawnColor == color::WHITE && Square::getRank(targetSquare) == Rank::r8)
						|| (pawnColor == color::BLACK && Square::getRank(targetSquare) == Rank::r1)) {
						// Pawn promotion capturing move

						list.entries[list.size++]->move = move::valueOf(
								movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece,
								PieceType::QUEEN);
						list.entries[list.size++]->move = move::valueOf(
								movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece,
								PieceType::ROOK);
						list.entries[list.size++]->move = move::valueOf(
								movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece,
								PieceType::BISHOP);
						list.entries[list.size++]->move = move::valueOf(
								movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece,
								PieceType::KNIGHT);
					} else {
						// Normal capturing move

						list.entries[list.size++]->move = move::valueOf(
								movetype::NORMAL, pawnSquare, targetSquare, pawnPiece, targetPiece,
								PieceType::NOPIECETYPE);
					}
				}
			} else if (targetSquare == position.enPassantSquare) {
				// En passant move
				int captureSquare = targetSquare + (pawnColor == color::WHITE ? Square::S : Square::N);
				targetPiece = position.board[captureSquare];

				list.entries[list.size++]->move = move::valueOf(
						movetype::ENPASSANT, pawnSquare, targetSquare, pawnPiece, targetPiece, PieceType::NOPIECETYPE);
			}
		}
	}

	// Generate non-capturing moves
	int direction = Square::pawnDirections[pawnColor][0];

	// Move one rank forward
	int targetSquare = pawnSquare + direction;
	if (Square::isValid(targetSquare) && position.board[targetSquare] == piece::NOPIECE) {
		if ((pawnColor == color::WHITE && Square::getRank(targetSquare) == Rank::r8)
			|| (pawnColor == color::BLACK && Square::getRank(targetSquare) == Rank::r1)) {
			// Pawn promotion move

			list.entries[list.size++]->move = move::valueOf(
					movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE, PieceType::QUEEN);
			list.entries[list.size++]->move = move::valueOf(
					movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE, PieceType::ROOK);
			list.entries[list.size++]->move = move::valueOf(
					movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE, PieceType::BISHOP);
			list.entries[list.size++]->move = move::valueOf(
					movetype::PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE, PieceType::KNIGHT);
		} else {
			// Normal move

			list.entries[list.size++]->move = move::valueOf(
					movetype::NORMAL, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE, PieceType::NOPIECETYPE);

			// Move another rank forward
			targetSquare += direction;
			if (Square::isValid(targetSquare) && position.board[targetSquare] == piece::NOPIECE) {
				if ((pawnColor == color::WHITE && Square::getRank(targetSquare) == Rank::r4)
					|| (pawnColor == color::BLACK && Square::getRank(targetSquare) == Rank::r5)) {
					// Pawn double move

					list.entries[list.size++]->move = move::valueOf(
							movetype::PAWNDOUBLE, pawnSquare, targetSquare, pawnPiece, piece::NOPIECE,
							PieceType::NOPIECETYPE);
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
			&& position.board[Square::f1] == piece::NOPIECE
			&& position.board[Square::g1] == piece::NOPIECE
			&& !position.isAttacked(Square::f1, color::BLACK)) {
			list.entries[list.size++]->move = move::valueOf(
					movetype::CASTLING, kingSquare, Square::g1, kingPiece, piece::NOPIECE, PieceType::NOPIECETYPE);
		}
		// Do not test c1 whether it is attacked as we will test it in isLegal()
		if ((position.castlingRights & castling::WHITE_QUEENSIDE) != castling::NOCASTLING
			&& position.board[Square::b1] == piece::NOPIECE
			&& position.board[Square::c1] == piece::NOPIECE
			&& position.board[Square::d1] == piece::NOPIECE
			&& !position.isAttacked(Square::d1, color::BLACK)) {
			list.entries[list.size++]->move = move::valueOf(
					movetype::CASTLING, kingSquare, Square::c1, kingPiece, piece::NOPIECE, PieceType::NOPIECETYPE);
		}
	} else {
		// Do not test g8 whether it is attacked as we will test it in isLegal()
		if ((position.castlingRights & castling::BLACK_KINGSIDE) != castling::NOCASTLING
			&& position.board[Square::f8] == piece::NOPIECE
			&& position.board[Square::g8] == piece::NOPIECE
			&& !position.isAttacked(Square::f8, color::WHITE)) {
			list.entries[list.size++]->move = move::valueOf(
					movetype::CASTLING, kingSquare, Square::g8, kingPiece, piece::NOPIECE, PieceType::NOPIECETYPE);
		}
		// Do not test c8 whether it is attacked as we will test it in isLegal()
		if ((position.castlingRights & castling::BLACK_QUEENSIDE) != castling::NOCASTLING
			&& position.board[Square::b8] == piece::NOPIECE
			&& position.board[Square::c8] == piece::NOPIECE
			&& position.board[Square::d8] == piece::NOPIECE
			&& !position.isAttacked(Square::d8, color::WHITE)) {
			list.entries[list.size++]->move = move::valueOf(
					movetype::CASTLING, kingSquare, Square::c8, kingPiece, piece::NOPIECE, PieceType::NOPIECETYPE);
		}
	}
}
}
