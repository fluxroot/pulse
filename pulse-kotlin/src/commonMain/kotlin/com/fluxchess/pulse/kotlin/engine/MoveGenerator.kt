/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

fun generateLegalMoves(moveList: MoveList, position: Position) {
	generateMoves(moveList, position)

	val size = moveList.size
	moveList.reset()
	for (i in 0 until size) {
		val move = moveList.entries[i].move
		position.makeMove(move)
		if (!position.isCheck(oppositeOf(position.activeColor))) {
			moveList.add(move)
		}
		position.undoMove(move)
	}
}

fun generateMoves(moveList: MoveList, position: Position) {
	moveList.reset()
	addAllMoves(moveList, position)
	if (!position.isCheck()) {
		val square = next(position.pieces[position.activeColor][KING])
		addCastlingMoves(moveList, position, square)
	}

	moveList.rateByMVVLVA()
	moveList.sort()
}

fun generateQuiescentMoves(moveList: MoveList, position: Position) {
	moveList.reset()
	addAllMoves(moveList, position)
	if (!position.isCheck()) {
		val size = moveList.size
		moveList.reset()
		for (i in 0 until size) {
			if (targetPieceOf(moveList.entries[i].move) != NO_PIECE) {
				moveList.add(moveList.entries[i].move)
			}
		}
	}

	moveList.rateByMVVLVA()
	moveList.sort()
}

private fun addAllMoves(moveList: MoveList, position: Position) {
	val activeColor = position.activeColor

	var squares = position.pieces[activeColor][PAWN]
	while (squares.compareTo(0u) != 0) {
		val square = next(squares)
		addPawnMoves(moveList, position, square)
		squares = remainder(squares)
	}
	squares = position.pieces[activeColor][KNIGHT]
	while (squares.compareTo(0u) != 0) {
		val square = next(squares)
		addPieceMoves(moveList, position, square, knightDirections)
		squares = remainder(squares)
	}
	squares = position.pieces[activeColor][BISHOP]
	while (squares.compareTo(0u) != 0) {
		val square = next(squares)
		addPieceMoves(moveList, position, square, bishopDirections)
		squares = remainder(squares)
	}
	squares = position.pieces[activeColor][ROOK]
	while (squares.compareTo(0u) != 0) {
		val square = next(squares)
		addPieceMoves(moveList, position, square, rookDirections)
		squares = remainder(squares)
	}
	squares = position.pieces[activeColor][QUEEN]
	while (squares.compareTo(0u) != 0) {
		val square = next(squares)
		addPieceMoves(moveList, position, square, queenDirections)
		squares = remainder(squares)
	}
	val square = next(position.pieces[activeColor][KING])
	addPieceMoves(moveList, position, square, kingDirections)
}

private fun addPawnMoves(moveList: MoveList, position: Position, pawnSquare: Square) {
	val pawnPiece = position.board[pawnSquare]
	val pawnColor = pieceColorOf(pawnPiece)

	for (direction in pawnCapturingDirections[pawnColor]) {
		val targetSquare = pawnSquare + direction
		if (isValidSquare(targetSquare)) {
			var targetPiece = position.board[targetSquare]
			if (targetPiece != NO_PIECE) {
				if (pieceColorOf(targetPiece) == oppositeOf(pawnColor)) {
					// Capturing move
					if ((pawnColor == WHITE && rankOf(targetSquare) == RANK_8)
						|| (pawnColor == BLACK && rankOf(targetSquare) == RANK_1)
					) {
						// Pawn promotion capturing move
						moveList.add(
							moveOf(PAWN_PROMOTION_MOVE, pawnSquare, targetSquare, pawnPiece, targetPiece, QUEEN),
						)
						moveList.add(
							moveOf(PAWN_PROMOTION_MOVE, pawnSquare, targetSquare, pawnPiece, targetPiece, ROOK),
						)
						moveList.add(
							moveOf(PAWN_PROMOTION_MOVE, pawnSquare, targetSquare, pawnPiece, targetPiece, BISHOP),
						)
						moveList.add(
							moveOf(PAWN_PROMOTION_MOVE, pawnSquare, targetSquare, pawnPiece, targetPiece, KNIGHT),
						)
					} else {
						// Normal capturing move
						moveList.add(
							moveOf(NORMAL_MOVE, pawnSquare, targetSquare, pawnPiece, targetPiece, NO_PIECE_TYPE),
						)
					}
				}
			} else if (targetSquare == position.enPassantSquare) {
				// En passant move
				val oppositeDirection = pawnMoveDirections[oppositeOf(pawnColor)]
				val captureSquare = targetSquare + oppositeDirection
				targetPiece = position.board[captureSquare]
				moveList.add(moveOf(EN_PASSANT_MOVE, pawnSquare, targetSquare, pawnPiece, targetPiece, NO_PIECE_TYPE))
			}
		}
	}

	val direction = pawnMoveDirections[pawnColor]

	// Move one rank forward
	var targetSquare = pawnSquare + direction
	if (isValidSquare(targetSquare) && position.board[targetSquare] == NO_PIECE) {
		if ((pawnColor == WHITE && rankOf(targetSquare) == RANK_8)
			|| (pawnColor == BLACK && rankOf(targetSquare) == RANK_1)
		) {
			// Pawn promotion move
			moveList.add(moveOf(PAWN_PROMOTION_MOVE, pawnSquare, targetSquare, pawnPiece, NO_PIECE, QUEEN))
			moveList.add(moveOf(PAWN_PROMOTION_MOVE, pawnSquare, targetSquare, pawnPiece, NO_PIECE, ROOK))
			moveList.add(moveOf(PAWN_PROMOTION_MOVE, pawnSquare, targetSquare, pawnPiece, NO_PIECE, BISHOP))
			moveList.add(moveOf(PAWN_PROMOTION_MOVE, pawnSquare, targetSquare, pawnPiece, NO_PIECE, KNIGHT))
		} else {
			// Normal move
			moveList.add(moveOf(NORMAL_MOVE, pawnSquare, targetSquare, pawnPiece, NO_PIECE, NO_PIECE_TYPE))

			targetSquare += direction
			if (isValidSquare(targetSquare) && position.board[targetSquare] == NO_PIECE) {
				if ((pawnColor == WHITE && rankOf(targetSquare) == RANK_4)
					|| (pawnColor == BLACK && rankOf(targetSquare) == RANK_5)
				) {
					// Pawn double move
					moveList.add(moveOf(PAWN_DOUBLE_MOVE, pawnSquare, targetSquare, pawnPiece, NO_PIECE, NO_PIECE_TYPE))
				}
			}
		}
	}
}

private fun addPieceMoves(moveList: MoveList, position: Position, originSquare: Square, directions: Array<Direction>) {
	val originPiece = position.board[originSquare]
	val sliding = isSliding(pieceTypeOf(originPiece))
	val oppositeColor = oppositeOf(pieceColorOf(originPiece))

	for (direction in directions) {
		var targetSquare = originSquare + direction
		while (isValidSquare(targetSquare)) {
			val targetPiece = position.board[targetSquare]
			if (targetPiece != NO_PIECE) {
				if (pieceColorOf(targetPiece) == oppositeColor) {
					// Capturing move
					moveList.add(
						moveOf(NORMAL_MOVE, originSquare, targetSquare, originPiece, targetPiece, NO_PIECE_TYPE),
					)
				}
				break
			} else {
				// Normal move
				moveList.add(moveOf(NORMAL_MOVE, originSquare, targetSquare, originPiece, NO_PIECE, NO_PIECE_TYPE))
				if (!sliding) {
					break
				}
				targetSquare += direction
			}
		}
	}
}

private fun addCastlingMoves(moveList: MoveList, position: Position, kingSquare: Square) {
	val kingPiece = position.board[kingSquare]

	if (pieceColorOf(kingPiece) == WHITE) {
		// Do not test g1 whether it is attacked as we will test it later
		if ((position.castlingRights and WHITE_KINGSIDE) != NO_CASTLING
			&& position.board[F1] == NO_PIECE
			&& position.board[G1] == NO_PIECE
			&& !position.isAttacked(F1, BLACK)
		) {
			moveList.add(moveOf(CASTLING_MOVE, kingSquare, G1, kingPiece, NO_PIECE, NO_PIECE_TYPE))
		}
		// Do not test c1 whether it is attacked as we will test it later
		if ((position.castlingRights and WHITE_QUEENSIDE) != NO_CASTLING
			&& position.board[B1] == NO_PIECE
			&& position.board[C1] == NO_PIECE
			&& position.board[D1] == NO_PIECE
			&& !position.isAttacked(D1, BLACK)
		) {
			moveList.add(moveOf(CASTLING_MOVE, kingSquare, C1, kingPiece, NO_PIECE, NO_PIECE_TYPE))
		}
	} else {
		// Do not test g8 whether it is attacked as we will test it later
		if ((position.castlingRights and BLACK_KINGSIDE) != NO_CASTLING
			&& position.board[F8] == NO_PIECE
			&& position.board[G8] == NO_PIECE
			&& !position.isAttacked(F8, WHITE)
		) {
			moveList.add(moveOf(CASTLING_MOVE, kingSquare, G8, kingPiece, NO_PIECE, NO_PIECE_TYPE))
		}
		// Do not test c8 whether it is attacked as we will test it later
		if ((position.castlingRights and BLACK_QUEENSIDE) != NO_CASTLING
			&& position.board[B8] == NO_PIECE
			&& position.board[C8] == NO_PIECE
			&& position.board[D8] == NO_PIECE
			&& !position.isAttacked(D8, WHITE)
		) {
			moveList.add(moveOf(CASTLING_MOVE, kingSquare, C8, kingPiece, NO_PIECE, NO_PIECE_TYPE))
		}
	}
}
