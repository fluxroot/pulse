/*
 * Copyright 2013-2025 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

func (p *Position) isCheck() bool {
	return p.isAttacked(next(p.pieces[p.ActiveColor][King]), OppositeOf(p.ActiveColor))
}

func (p *Position) isCheckFor(col Color) bool {
	return p.isAttacked(next(p.pieces[col][King]), OppositeOf(col))
}

func (p *Position) isAttacked(targetSq Square, attackerCol Color) bool {
	return p.isAttackedByPawn(targetSq, attackerCol) ||
		p.isAttackedByNonSlidingPiece(targetSq, PieceOf(attackerCol, Knight), knightDirections[:]) ||
		p.isAttackedBySlidingPiece(targetSq, PieceOf(attackerCol, Bishop), PieceOf(attackerCol, Queen), bishopDirections[:]) ||
		p.isAttackedBySlidingPiece(targetSq, PieceOf(attackerCol, Rook), PieceOf(attackerCol, Queen), rookDirections[:]) ||
		p.isAttackedByNonSlidingPiece(targetSq, PieceOf(attackerCol, King), kingDirections[:])
}

func (p *Position) isAttackedByPawn(targetSq Square, attackerCol Color) bool {
	attackerPawn := PieceOf(attackerCol, Pawn)
	for _, dir := range pawnCapturingDirections[attackerCol] {
		attackerSq := targetSq - dir
		if IsValidSquare(attackerSq) && p.board[attackerSq] == attackerPawn {
			return true
		}
	}
	return false
}

func (p *Position) isAttackedByNonSlidingPiece(targetSq Square, attackerPc Piece, directions []direction) bool {
	for _, dir := range directions {
		attackerSq := targetSq + dir
		if IsValidSquare(attackerSq) && p.board[attackerSq] == attackerPc {
			return true
		}
	}
	return false
}

func (p *Position) isAttackedBySlidingPiece(targetSq Square, attackerPc Piece, attackerQueen Piece, directions []direction) bool {
	for _, dir := range directions {
		attackerSq := targetSq + dir
		for IsValidSquare(attackerSq) {
			pc := p.board[attackerSq]
			if pc != NoPiece {
				if pc == attackerPc || pc == attackerQueen {
					return true
				}
				break
			} else {
				attackerSq += dir
			}
		}
	}
	return false
}
