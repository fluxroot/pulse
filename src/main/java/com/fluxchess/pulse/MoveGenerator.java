/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import static com.fluxchess.pulse.Bitboard.next;
import static com.fluxchess.pulse.Bitboard.remainder;
import static com.fluxchess.pulse.Castling.*;
import static com.fluxchess.pulse.Color.*;
import static com.fluxchess.pulse.MoveList.MoveEntry;
import static com.fluxchess.pulse.MoveType.*;
import static com.fluxchess.pulse.Piece.NOPIECE;
import static com.fluxchess.pulse.PieceType.*;
import static com.fluxchess.pulse.Rank.*;
import static com.fluxchess.pulse.Square.*;

final class MoveGenerator {

	private final MoveList<MoveEntry> moves = new MoveList<>(MoveEntry.class);

	MoveList<MoveEntry> getLegalMoves(Position position, int depth, boolean isCheck) {
		MoveList<MoveEntry> legalMoves = getMoves(position, depth, isCheck);

		int size = legalMoves.size;
		legalMoves.size = 0;
		for (int i = 0; i < size; i++) {
			int move = legalMoves.entries[i].move;

			position.makeMove(move);
			if (!position.isCheck(opposite(position.activeColor))) {
				legalMoves.entries[legalMoves.size++].move = move;
			}
			position.undoMove(move);
		}

		return legalMoves;
	}

	MoveList<MoveEntry> getMoves(Position position, int depth, boolean isCheck) {
		moves.size = 0;

		if (depth > 0) {
			// Generate main moves

			addMoves(moves, position);

			if (!isCheck) {
				int square = next(position.pieces[position.activeColor][KING]);
				addCastlingMoves(moves, square, position);
			}
		} else {
			// Generate quiescent moves

			addMoves(moves, position);

			if (!isCheck) {
				int size = moves.size;
				moves.size = 0;
				for (int i = 0; i < size; i++) {
					if (Move.getTargetPiece(moves.entries[i].move) != NOPIECE) {
						// Add only capturing moves
						moves.entries[moves.size++].move = moves.entries[i].move;
					}
				}
			}
		}

		moves.rateFromMVVLVA();
		moves.sort();

		return moves;
	}

	private void addMoves(MoveList<MoveEntry> list, Position position) {
		int activeColor = position.activeColor;

		for (long squares = position.pieces[activeColor][PAWN]; squares != 0; squares = remainder(squares)) {
			int square = next(squares);
			addPawnMoves(list, square, position);
		}
		for (long squares = position.pieces[activeColor][KNIGHT]; squares != 0; squares = remainder(squares)) {
			int square = next(squares);
			addMoves(list, square, knightDirections, position);
		}
		for (long squares = position.pieces[activeColor][BISHOP]; squares != 0; squares = remainder(squares)) {
			int square = next(squares);
			addMoves(list, square, bishopDirections, position);
		}
		for (long squares = position.pieces[activeColor][ROOK]; squares != 0; squares = remainder(squares)) {
			int square = next(squares);
			addMoves(list, square, rookDirections, position);
		}
		for (long squares = position.pieces[activeColor][QUEEN]; squares != 0; squares = remainder(squares)) {
			int square = next(squares);
			addMoves(list, square, queenDirections, position);
		}
		int square = next(position.pieces[activeColor][KING]);
		addMoves(list, square, kingDirections, position);
	}

	private void addMoves(MoveList<MoveEntry> list, int originSquare, int[] directions, Position position) {
		int originPiece = position.board[originSquare];
		boolean sliding = isSliding(Piece.getType(originPiece));
		int oppositeColor = opposite(Piece.getColor(originPiece));

		// Go through all move directions for this piece
		for (int direction : directions) {
			int targetSquare = originSquare + direction;

			// Check if we're still on the board
			while (Square.isValid(targetSquare)) {
				int targetPiece = position.board[targetSquare];

				if (targetPiece == NOPIECE) {
					// quiet move
					list.entries[list.size++].move = Move.valueOf(
							NORMAL, originSquare, targetSquare, originPiece, NOPIECE, NOPIECETYPE);

					if (!sliding) {
						break;
					}

					targetSquare += direction;
				} else {
					if (Piece.getColor(targetPiece) == oppositeColor) {
						// capturing move
						list.entries[list.size++].move = Move.valueOf(
								NORMAL, originSquare, targetSquare, originPiece, targetPiece, NOPIECETYPE);
					}

					break;
				}
			}
		}
	}

	private void addPawnMoves(MoveList<MoveEntry> list, int pawnSquare, Position position) {
		int pawnPiece = position.board[pawnSquare];
		int pawnColor = Piece.getColor(pawnPiece);

		// Generate only capturing moves first (i = 1)
		for (int i = 1; i < pawnDirections[pawnColor].length; i++) {
			int direction = pawnDirections[pawnColor][i];

			int targetSquare = pawnSquare + direction;
			if (Square.isValid(targetSquare)) {
				int targetPiece = position.board[targetSquare];

				if (targetPiece != NOPIECE) {
					if (Piece.getColor(targetPiece) == opposite(pawnColor)) {
						// Capturing move

						if ((pawnColor == WHITE && Square.getRank(targetSquare) == r8)
								|| (pawnColor == BLACK && Square.getRank(targetSquare) == r1)) {
							// Pawn promotion capturing move

							list.entries[list.size++].move = Move.valueOf(
									PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, QUEEN);
							list.entries[list.size++].move = Move.valueOf(
									PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, ROOK);
							list.entries[list.size++].move = Move.valueOf(
									PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, BISHOP);
							list.entries[list.size++].move = Move.valueOf(
									PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, KNIGHT);
						} else {
							// Normal capturing move

							list.entries[list.size++].move = Move.valueOf(
									NORMAL, pawnSquare, targetSquare, pawnPiece, targetPiece, NOPIECETYPE);
						}
					}
				} else if (targetSquare == position.enPassantSquare) {
					// En passant move
					int captureSquare = targetSquare + (pawnColor == WHITE ? S : N);
					targetPiece = position.board[captureSquare];

					list.entries[list.size++].move = Move.valueOf(
							ENPASSANT, pawnSquare, targetSquare, pawnPiece, targetPiece, NOPIECETYPE);
				}
			}
		}

		// Generate non-capturing moves
		int direction = pawnDirections[pawnColor][0];

		// Move one rank forward
		int targetSquare = pawnSquare + direction;
		if (Square.isValid(targetSquare) && position.board[targetSquare] == NOPIECE) {
			if ((pawnColor == WHITE && Square.getRank(targetSquare) == r8)
					|| (pawnColor == BLACK && Square.getRank(targetSquare) == r1)) {
				// Pawn promotion move

				list.entries[list.size++].move = Move.valueOf(
						PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, NOPIECE, QUEEN);
				list.entries[list.size++].move = Move.valueOf(
						PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, NOPIECE, ROOK);
				list.entries[list.size++].move = Move.valueOf(
						PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, NOPIECE, BISHOP);
				list.entries[list.size++].move = Move.valueOf(
						PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, NOPIECE, KNIGHT);
			} else {
				// Normal move

				list.entries[list.size++].move = Move.valueOf(
						NORMAL, pawnSquare, targetSquare, pawnPiece, NOPIECE, NOPIECETYPE);

				// Move another rank forward
				targetSquare += direction;
				if (Square.isValid(targetSquare) && position.board[targetSquare] == NOPIECE) {
					if ((pawnColor == WHITE && Square.getRank(targetSquare) == r4)
							|| (pawnColor == BLACK && Square.getRank(targetSquare) == r5)) {
						// Pawn double move

						list.entries[list.size++].move = Move.valueOf(
								PAWNDOUBLE, pawnSquare, targetSquare, pawnPiece, NOPIECE, NOPIECETYPE);
					}
				}
			}
		}
	}

	private void addCastlingMoves(MoveList<MoveEntry> list, int kingSquare, Position position) {
		int kingPiece = position.board[kingSquare];

		if (Piece.getColor(kingPiece) == WHITE) {
			// Do not test g1 whether it is attacked as we will test it in isLegal()
			if ((position.castlingRights & WHITE_KINGSIDE) != NOCASTLING
					&& position.board[f1] == NOPIECE
					&& position.board[g1] == NOPIECE
					&& !position.isAttacked(f1, BLACK)) {
				list.entries[list.size++].move = Move.valueOf(
						CASTLING, kingSquare, g1, kingPiece, NOPIECE, NOPIECETYPE);
			}
			// Do not test c1 whether it is attacked as we will test it in isLegal()
			if ((position.castlingRights & WHITE_QUEENSIDE) != NOCASTLING
					&& position.board[b1] == NOPIECE
					&& position.board[c1] == NOPIECE
					&& position.board[d1] == NOPIECE
					&& !position.isAttacked(d1, BLACK)) {
				list.entries[list.size++].move = Move.valueOf(
						CASTLING, kingSquare, c1, kingPiece, NOPIECE, NOPIECETYPE);
			}
		} else {
			// Do not test g8 whether it is attacked as we will test it in isLegal()
			if ((position.castlingRights & BLACK_KINGSIDE) != NOCASTLING
					&& position.board[f8] == NOPIECE
					&& position.board[g8] == NOPIECE
					&& !position.isAttacked(f8, WHITE)) {
				list.entries[list.size++].move = Move.valueOf(
						CASTLING, kingSquare, g8, kingPiece, NOPIECE, NOPIECETYPE);
			}
			// Do not test c8 whether it is attacked as we will test it in isLegal()
			if ((position.castlingRights & BLACK_QUEENSIDE) != NOCASTLING
					&& position.board[b8] == NOPIECE
					&& position.board[c8] == NOPIECE
					&& position.board[d8] == NOPIECE
					&& !position.isAttacked(d8, WHITE)) {
				list.entries[list.size++].move = Move.valueOf(
						CASTLING, kingSquare, c8, kingPiece, NOPIECE, NOPIECETYPE);
			}
		}
	}
}
