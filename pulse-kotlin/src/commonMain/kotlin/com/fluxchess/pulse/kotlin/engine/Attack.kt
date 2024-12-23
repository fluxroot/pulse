/*
 * Copyright 2013-2025 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

internal fun Position.isCheck(): Boolean {
	return isAttacked(next(pieces[activeColor][KING]), oppositeOf(activeColor))
}

internal fun Position.isCheck(color: Color): Boolean {
	return isAttacked(next(pieces[color][KING]), oppositeOf(color))
}

internal fun Position.isAttacked(targetSquare: Square, attackerColor: Color): Boolean =
	isAttackedByPawn(targetSquare, attackerColor)
		|| isAttackedByNonSlidingPiece(targetSquare, pieceOf(attackerColor, KNIGHT), knightDirections)
		|| isAttackedBySlidingPiece(
		targetSquare,
		pieceOf(attackerColor, BISHOP),
		pieceOf(attackerColor, QUEEN),
		bishopDirections,
	)
		|| isAttackedBySlidingPiece(
		targetSquare,
		pieceOf(attackerColor, ROOK),
		pieceOf(attackerColor, QUEEN),
		rookDirections,
	)
		|| isAttackedByNonSlidingPiece(targetSquare, pieceOf(attackerColor, KING), kingDirections)

private fun Position.isAttackedByPawn(targetSquare: Square, attackerColor: Color): Boolean {
	val attackerPawn = pieceOf(attackerColor, PAWN)
	for (direction in pawnCapturingDirections[attackerColor]) {
		val attackerSquare = targetSquare - direction
		if (isValidSquare(attackerSquare) && board[attackerSquare] == attackerPawn) {
			return true
		}
	}
	return false
}

private fun Position.isAttackedByNonSlidingPiece(
	targetSquare: Square,
	attackerPiece: Piece,
	directions: Array<Direction>,
): Boolean {
	for (direction in directions) {
		val attackerSquare = targetSquare + direction
		if (isValidSquare(attackerSquare) && board[attackerSquare] == attackerPiece) {
			return true
		}
	}
	return false
}

private fun Position.isAttackedBySlidingPiece(
	targetSquare: Square,
	attackerPiece: Piece,
	attackerQueen: Piece,
	directions: Array<Direction>,
): Boolean {
	for (direction in directions) {
		var attackerSquare = targetSquare + direction
		while (isValidSquare(attackerSquare)) {
			val piece = board[attackerSquare]
			if (piece != NO_PIECE) {
				if (piece == attackerPiece || piece == attackerQueen) {
					return true
				}
				break
			} else {
				attackerSquare += direction
			}
		}
	}
	return false
}
