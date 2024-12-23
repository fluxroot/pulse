/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

class Position {
	var activeColor: Color = NO_COLOR
	var castlingRights: Castling = NO_CASTLING
	var enPassantSquare: Square = NO_SQUARE
	var halfmoveClock: Int = 0
	var halfmoveNumber: Int = 0

	internal val board: Array<Piece> = Array(SQUARES_MAX_VALUE) { NO_PIECE }
	internal val pieces: Array<BitboardArray> = Array(colors.size) { BitboardArray(pieceTypes.size) { 0u } }

	private val stateList: StateList = StateList()

	fun makeMove(move: Move) {
		saveState()

		val moveType = moveTypeOf(move)
		val originSquare = originSquareOf(move)
		val targetSquare = targetSquareOf(move)
		val originPiece = originPieceOf(move)
		val originColor = pieceColorOf(originPiece)
		val targetPiece = targetPieceOf(move)

		if (targetPiece != NO_PIECE) {
			var captureSquare = targetSquare
			if (moveType == EN_PASSANT_MOVE) {
				val oppositeDirection = pawnMoveDirections[oppositeOf(originColor)]
				captureSquare += oppositeDirection
			}
			remove(captureSquare)
			clearCastling(captureSquare)
		}

		remove(originSquare)
		if (moveType == PAWN_PROMOTION_MOVE) {
			put(pieceOf(originColor, promotionOf(move)), targetSquare)
		} else {
			put(originPiece, targetSquare)
		}

		if (moveType == CASTLING_MOVE) {
			when (targetSquare) {
				G1 -> put(remove(H1), F1)
				C1 -> put(remove(A1), D1)
				G8 -> put(remove(H8), F8)
				C8 -> put(remove(A8), D8)
				else -> error("Invalid target square: $targetSquare")
			}
		}

		clearCastling(originSquare)

		if (moveType == PAWN_DOUBLE_MOVE) {
			val oppositeDirection = pawnMoveDirections[oppositeOf(originColor)]
			enPassantSquare = targetSquare + oppositeDirection
		} else {
			enPassantSquare = NO_SQUARE
		}

		activeColor = oppositeOf(activeColor)

		if (pieceTypeOf(originPiece) == PAWN || targetPiece != NO_PIECE) {
			halfmoveClock = 0
		} else {
			halfmoveClock++
		}

		halfmoveNumber++
	}

	fun undoMove(move: Move) {
		val moveType = moveTypeOf(move)
		val originSquare = originSquareOf(move)
		val targetSquare = targetSquareOf(move)
		val originPiece = originPieceOf(move)
		val originColor = pieceColorOf(originPiece)
		val targetPiece = targetPieceOf(move)

		halfmoveNumber--

		activeColor = oppositeOf(activeColor)

		if (moveType == CASTLING_MOVE) {
			when (targetSquare) {
				G1 -> put(remove(F1), H1)
				C1 -> put(remove(D1), A1)
				G8 -> put(remove(F8), H8)
				C8 -> put(remove(D8), A8)
				else -> error("Invalid target square: $targetSquare")
			}
		}

		remove(targetSquare)
		put(originPiece, originSquare)

		if (targetPiece != NO_PIECE) {
			var captureSquare = targetSquare
			if (moveType == EN_PASSANT_MOVE) {
				val oppositeDirection = pawnMoveDirections[oppositeOf(originColor)]
				captureSquare += oppositeDirection
			}
			put(targetPiece, captureSquare)
		}

		restoreState()
	}

	private fun saveState() {
		val entry = stateList.entries[stateList.size]
		entry.castlingRights = castlingRights
		entry.enPassantSquare = enPassantSquare
		entry.halfmoveClock = halfmoveClock
		stateList.size++
	}

	private fun restoreState() {
		stateList.size--
		val entry = stateList.entries[stateList.size]
		castlingRights = entry.castlingRights
		enPassantSquare = entry.enPassantSquare
		halfmoveClock = entry.halfmoveClock
	}

	fun get(square: Square): Piece {
		return board[square]
	}

	fun put(piece: Piece, square: Square) {
		val color = pieceColorOf(piece)
		val pieceType = pieceTypeOf(piece)

		board[square] = piece
		pieces[color][pieceType] = addSquare(square, pieces[color][pieceType])
	}

	fun remove(square: Square): Piece {
		val piece = board[square]
		val color = pieceColorOf(piece)
		val pieceType = pieceTypeOf(piece)

		board[square] = NO_PIECE
		pieces[color][pieceType] = removeSquare(square, pieces[color][pieceType])

		return piece
	}

	fun setCastlingRight(castling: Castling) {
		castlingRights = castlingRights or castling
	}

	private fun clearCastling(square: Square) {
		when (square) {
			A1 -> castlingRights = castlingRights and WHITE_QUEENSIDE.inv()
			A8 -> castlingRights = castlingRights and BLACK_QUEENSIDE.inv()
			H1 -> castlingRights = castlingRights and WHITE_KINGSIDE.inv()
			H8 -> castlingRights = castlingRights and BLACK_KINGSIDE.inv()
			E1 -> castlingRights = castlingRights and (WHITE_KINGSIDE or WHITE_QUEENSIDE).inv()
			E8 -> castlingRights = castlingRights and (BLACK_KINGSIDE or BLACK_QUEENSIDE).inv()
		}
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Position) return false

		return this.activeColor == other.activeColor
			&& this.castlingRights == other.castlingRights
			&& this.enPassantSquare == other.enPassantSquare
			&& this.halfmoveClock == other.halfmoveClock
			&& this.halfmoveNumber == other.halfmoveNumber
			&& this.board contentDeepEquals other.board
			&& this.pieces contentDeepEquals other.pieces
	}
}

private class StateList {
	var size: Int = 0
	val entries: Array<StateEntry> = Array(MAX_PLY) { StateEntry() }
}

private class StateEntry {
	var castlingRights: Castling = NO_CASTLING
	var enPassantSquare: Square = NO_SQUARE
	var halfmoveClock: Int = 0
}
