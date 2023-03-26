/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java;

import com.fluxchess.pulse.java.model.Move;
import com.fluxchess.pulse.java.model.Piece;
import com.fluxchess.pulse.java.model.PieceType;
import com.fluxchess.pulse.java.model.Square;

import static com.fluxchess.pulse.java.Bitboard.next;
import static com.fluxchess.pulse.java.Bitboard.remainder;
import static com.fluxchess.pulse.java.model.Castling.BLACK_KINGSIDE;
import static com.fluxchess.pulse.java.model.Castling.BLACK_QUEENSIDE;
import static com.fluxchess.pulse.java.model.Castling.NOCASTLING;
import static com.fluxchess.pulse.java.model.Castling.WHITE_KINGSIDE;
import static com.fluxchess.pulse.java.model.Castling.WHITE_QUEENSIDE;
import static com.fluxchess.pulse.java.model.Color.BLACK;
import static com.fluxchess.pulse.java.model.Color.WHITE;
import static com.fluxchess.pulse.java.model.Color.opposite;
import static com.fluxchess.pulse.java.model.MoveType.CASTLING;
import static com.fluxchess.pulse.java.model.MoveType.ENPASSANT;
import static com.fluxchess.pulse.java.model.MoveType.NORMAL;
import static com.fluxchess.pulse.java.model.MoveType.PAWNDOUBLE;
import static com.fluxchess.pulse.java.model.MoveType.PAWNPROMOTION;
import static com.fluxchess.pulse.java.model.Rank.r1;
import static com.fluxchess.pulse.java.model.Rank.r4;
import static com.fluxchess.pulse.java.model.Rank.r5;
import static com.fluxchess.pulse.java.model.Rank.r8;
import static com.fluxchess.pulse.java.model.Square.N;
import static com.fluxchess.pulse.java.model.Square.S;
import static com.fluxchess.pulse.java.model.Square.b1;
import static com.fluxchess.pulse.java.model.Square.b8;
import static com.fluxchess.pulse.java.model.Square.bishopDirections;
import static com.fluxchess.pulse.java.model.Square.c1;
import static com.fluxchess.pulse.java.model.Square.c8;
import static com.fluxchess.pulse.java.model.Square.d1;
import static com.fluxchess.pulse.java.model.Square.d8;
import static com.fluxchess.pulse.java.model.Square.f1;
import static com.fluxchess.pulse.java.model.Square.f8;
import static com.fluxchess.pulse.java.model.Square.g1;
import static com.fluxchess.pulse.java.model.Square.g8;
import static com.fluxchess.pulse.java.model.Square.kingDirections;
import static com.fluxchess.pulse.java.model.Square.knightDirections;
import static com.fluxchess.pulse.java.model.Square.pawnDirections;
import static com.fluxchess.pulse.java.model.Square.queenDirections;
import static com.fluxchess.pulse.java.model.Square.rookDirections;

final class MoveGenerator {

	private final MoveList<MoveList.MoveEntry> moves = new MoveList<>(MoveList.MoveEntry.class);

	MoveList<MoveList.MoveEntry> getLegalMoves(Position position, int depth, boolean isCheck) {
		MoveList<MoveList.MoveEntry> legalMoves = getMoves(position, depth, isCheck);

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

	MoveList<MoveList.MoveEntry> getMoves(Position position, int depth, boolean isCheck) {
		moves.size = 0;

		if (depth > 0) {
			// Generate main moves

			addMoves(moves, position);

			if (!isCheck) {
				int square = next(position.pieces[position.activeColor][PieceType.KING]);
				addCastlingMoves(moves, square, position);
			}
		} else {
			// Generate quiescent moves

			addMoves(moves, position);

			if (!isCheck) {
				int size = moves.size;
				moves.size = 0;
				for (int i = 0; i < size; i++) {
					if (Move.getTargetPiece(moves.entries[i].move) != Piece.NOPIECE) {
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

	private void addMoves(MoveList<MoveList.MoveEntry> list, Position position) {
		int activeColor = position.activeColor;

		for (long squares = position.pieces[activeColor][PieceType.PAWN]; squares != 0; squares = remainder(squares)) {
			int square = next(squares);
			addPawnMoves(list, square, position);
		}
		for (long squares = position.pieces[activeColor][PieceType.KNIGHT]; squares != 0; squares = remainder(squares)) {
			int square = next(squares);
			addMoves(list, square, knightDirections, position);
		}
		for (long squares = position.pieces[activeColor][PieceType.BISHOP]; squares != 0; squares = remainder(squares)) {
			int square = next(squares);
			addMoves(list, square, bishopDirections, position);
		}
		for (long squares = position.pieces[activeColor][PieceType.ROOK]; squares != 0; squares = remainder(squares)) {
			int square = next(squares);
			addMoves(list, square, rookDirections, position);
		}
		for (long squares = position.pieces[activeColor][PieceType.QUEEN]; squares != 0; squares = remainder(squares)) {
			int square = next(squares);
			addMoves(list, square, queenDirections, position);
		}
		int square = next(position.pieces[activeColor][PieceType.KING]);
		addMoves(list, square, kingDirections, position);
	}

	private void addMoves(MoveList<MoveList.MoveEntry> list, int originSquare, int[] directions, Position position) {
		int originPiece = position.board[originSquare];
		boolean sliding = PieceType.isSliding(Piece.getType(originPiece));
		int oppositeColor = opposite(Piece.getColor(originPiece));

		// Go through all move directions for this piece
		for (int direction : directions) {
			int targetSquare = originSquare + direction;

			// Check if we're still on the board
			while (Square.isValid(targetSquare)) {
				int targetPiece = position.board[targetSquare];

				if (targetPiece == Piece.NOPIECE) {
					// quiet move
					list.entries[list.size++].move = Move.valueOf(
						NORMAL, originSquare, targetSquare, originPiece, Piece.NOPIECE, PieceType.NOPIECETYPE);

					if (!sliding) {
						break;
					}

					targetSquare += direction;
				} else {
					if (Piece.getColor(targetPiece) == oppositeColor) {
						// capturing move
						list.entries[list.size++].move = Move.valueOf(
							NORMAL, originSquare, targetSquare, originPiece, targetPiece, PieceType.NOPIECETYPE);
					}

					break;
				}
			}
		}
	}

	private void addPawnMoves(MoveList<MoveList.MoveEntry> list, int pawnSquare, Position position) {
		int pawnPiece = position.board[pawnSquare];
		int pawnColor = Piece.getColor(pawnPiece);

		// Generate only capturing moves first (i = 1)
		for (int i = 1; i < pawnDirections[pawnColor].length; i++) {
			int direction = pawnDirections[pawnColor][i];

			int targetSquare = pawnSquare + direction;
			if (Square.isValid(targetSquare)) {
				int targetPiece = position.board[targetSquare];

				if (targetPiece != Piece.NOPIECE) {
					if (Piece.getColor(targetPiece) == opposite(pawnColor)) {
						// Capturing move

						if ((pawnColor == WHITE && Square.getRank(targetSquare) == r8)
							|| (pawnColor == BLACK && Square.getRank(targetSquare) == r1)) {
							// Pawn promotion capturing move

							list.entries[list.size++].move = Move.valueOf(
								PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, PieceType.QUEEN);
							list.entries[list.size++].move = Move.valueOf(
								PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, PieceType.ROOK);
							list.entries[list.size++].move = Move.valueOf(
								PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, PieceType.BISHOP);
							list.entries[list.size++].move = Move.valueOf(
								PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, PieceType.KNIGHT);
						} else {
							// Normal capturing move

							list.entries[list.size++].move = Move.valueOf(
								NORMAL, pawnSquare, targetSquare, pawnPiece, targetPiece, PieceType.NOPIECETYPE);
						}
					}
				} else if (targetSquare == position.enPassantSquare) {
					// En passant move
					int captureSquare = targetSquare + (pawnColor == WHITE ? S : N);
					targetPiece = position.board[captureSquare];

					list.entries[list.size++].move = Move.valueOf(
						ENPASSANT, pawnSquare, targetSquare, pawnPiece, targetPiece, PieceType.NOPIECETYPE);
				}
			}
		}

		// Generate non-capturing moves
		int direction = pawnDirections[pawnColor][0];

		// Move one rank forward
		int targetSquare = pawnSquare + direction;
		if (Square.isValid(targetSquare) && position.board[targetSquare] == Piece.NOPIECE) {
			if ((pawnColor == WHITE && Square.getRank(targetSquare) == r8)
				|| (pawnColor == BLACK && Square.getRank(targetSquare) == r1)) {
				// Pawn promotion move

				list.entries[list.size++].move = Move.valueOf(
					PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, PieceType.QUEEN);
				list.entries[list.size++].move = Move.valueOf(
					PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, PieceType.ROOK);
				list.entries[list.size++].move = Move.valueOf(
					PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, PieceType.BISHOP);
				list.entries[list.size++].move = Move.valueOf(
					PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, PieceType.KNIGHT);
			} else {
				// Normal move

				list.entries[list.size++].move = Move.valueOf(
					NORMAL, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, PieceType.NOPIECETYPE);

				// Move another rank forward
				targetSquare += direction;
				if (Square.isValid(targetSquare) && position.board[targetSquare] == Piece.NOPIECE) {
					if ((pawnColor == WHITE && Square.getRank(targetSquare) == r4)
						|| (pawnColor == BLACK && Square.getRank(targetSquare) == r5)) {
						// Pawn double move

						list.entries[list.size++].move = Move.valueOf(
							PAWNDOUBLE, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, PieceType.NOPIECETYPE);
					}
				}
			}
		}
	}

	private void addCastlingMoves(MoveList<MoveList.MoveEntry> list, int kingSquare, Position position) {
		int kingPiece = position.board[kingSquare];

		if (Piece.getColor(kingPiece) == WHITE) {
			// Do not test g1 whether it is attacked as we will test it in isLegal()
			if ((position.castlingRights & WHITE_KINGSIDE) != NOCASTLING
				&& position.board[f1] == Piece.NOPIECE
				&& position.board[g1] == Piece.NOPIECE
				&& !position.isAttacked(f1, BLACK)) {
				list.entries[list.size++].move = Move.valueOf(
					CASTLING, kingSquare, g1, kingPiece, Piece.NOPIECE, PieceType.NOPIECETYPE);
			}
			// Do not test c1 whether it is attacked as we will test it in isLegal()
			if ((position.castlingRights & WHITE_QUEENSIDE) != NOCASTLING
				&& position.board[b1] == Piece.NOPIECE
				&& position.board[c1] == Piece.NOPIECE
				&& position.board[d1] == Piece.NOPIECE
				&& !position.isAttacked(d1, BLACK)) {
				list.entries[list.size++].move = Move.valueOf(
					CASTLING, kingSquare, c1, kingPiece, Piece.NOPIECE, PieceType.NOPIECETYPE);
			}
		} else {
			// Do not test g8 whether it is attacked as we will test it in isLegal()
			if ((position.castlingRights & BLACK_KINGSIDE) != NOCASTLING
				&& position.board[f8] == Piece.NOPIECE
				&& position.board[g8] == Piece.NOPIECE
				&& !position.isAttacked(f8, WHITE)) {
				list.entries[list.size++].move = Move.valueOf(
					CASTLING, kingSquare, g8, kingPiece, Piece.NOPIECE, PieceType.NOPIECETYPE);
			}
			// Do not test c8 whether it is attacked as we will test it in isLegal()
			if ((position.castlingRights & BLACK_QUEENSIDE) != NOCASTLING
				&& position.board[b8] == Piece.NOPIECE
				&& position.board[c8] == Piece.NOPIECE
				&& position.board[d8] == Piece.NOPIECE
				&& !position.isAttacked(d8, WHITE)) {
				list.entries[list.size++].move = Move.valueOf(
					CASTLING, kingSquare, c8, kingPiece, Piece.NOPIECE, PieceType.NOPIECETYPE);
			}
		}
	}
}
