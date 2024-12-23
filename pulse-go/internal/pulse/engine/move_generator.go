/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

func GenerateLegalMoves(ml *MoveList, p *Position) {
	GenerateMoves(ml, p)

	size := ml.Size
	ml.reset()
	for i := 0; i < size; i++ {
		m := ml.Entries[i].Move
		p.MakeMove(m)
		if !p.isCheckFor(OppositeOf(p.ActiveColor)) {
			ml.add(m)
		}
		p.UndoMove(m)
	}
}

func GenerateMoves(ml *MoveList, p *Position) {
	ml.reset()
	addAllMoves(ml, p)
	if !p.isCheck() {
		sq := next(p.pieces[p.ActiveColor][King])
		addCastlingMoves(ml, p, sq)
	}

	ml.rateByMVVLVA()
	ml.sort()
}

func GenerateQuiescentMoves(ml *MoveList, p *Position) {
	ml.reset()
	addAllMoves(ml, p)
	if !p.isCheck() {
		size := ml.Size
		ml.reset()
		for i := 0; i < size; i++ {
			if TargetPieceOf(ml.Entries[i].Move) != NoPiece {
				ml.add(ml.Entries[i].Move)
			}
		}
	}

	ml.rateByMVVLVA()
	ml.sort()
}

func addAllMoves(ml *MoveList, p *Position) {
	activeCol := p.ActiveColor

	sqs := p.pieces[activeCol][Pawn]
	for sqs != 0 {
		sq := next(sqs)
		addPawnMoves(ml, p, sq)
		sqs = remainder(sqs)
	}
	sqs = p.pieces[activeCol][Knight]
	for sqs != 0 {
		sq := next(sqs)
		addPieceMoves(ml, p, sq, knightDirections[:])
		sqs = remainder(sqs)
	}
	sqs = p.pieces[activeCol][Bishop]
	for sqs != 0 {
		sq := next(sqs)
		addPieceMoves(ml, p, sq, bishopDirections[:])
		sqs = remainder(sqs)
	}
	sqs = p.pieces[activeCol][Rook]
	for sqs != 0 {
		sq := next(sqs)
		addPieceMoves(ml, p, sq, rookDirections[:])
		sqs = remainder(sqs)
	}
	sqs = p.pieces[activeCol][Queen]
	for sqs != 0 {
		sq := next(sqs)
		addPieceMoves(ml, p, sq, queenDirections[:])
		sqs = remainder(sqs)
	}
	sq := next(p.pieces[activeCol][King])
	addPieceMoves(ml, p, sq, kingDirections[:])
}

func addPawnMoves(ml *MoveList, p *Position, pawnSq Square) {
	pawnPc := p.board[pawnSq]
	pawnCol := PieceColorOf(pawnPc)

	for _, dir := range pawnCapturingDirections[pawnCol] {
		targetSq := pawnSq + dir
		if IsValidSquare(targetSq) {
			targetPc := p.board[targetSq]
			if targetPc != NoPiece {
				if PieceColorOf(targetPc) == OppositeOf(pawnCol) {
					// Capturing move
					if (pawnCol == White && RankOf(targetSq) == Rank8) ||
						(pawnCol == Black && RankOf(targetSq) == Rank1) {
						// Pawn promotion capturing move
						ml.add(moveOf(pawnPromotionMove, pawnSq, targetSq, pawnPc, targetPc, Queen))
						ml.add(moveOf(pawnPromotionMove, pawnSq, targetSq, pawnPc, targetPc, Rook))
						ml.add(moveOf(pawnPromotionMove, pawnSq, targetSq, pawnPc, targetPc, Bishop))
						ml.add(moveOf(pawnPromotionMove, pawnSq, targetSq, pawnPc, targetPc, Knight))
					} else {
						// Normal capturing move
						ml.add(moveOf(normalMove, pawnSq, targetSq, pawnPc, targetPc, NoPieceType))
					}
				}
			} else if targetSq == p.EnPassantSquare {
				// En passant move
				oppositeDir := pawnMoveDirections[OppositeOf(pawnCol)]
				captureSq := targetSq + oppositeDir
				targetPc = p.board[captureSq]
				ml.add(moveOf(enPassantMove, pawnSq, targetSq, pawnPc, targetPc, NoPieceType))
			}
		}
	}

	dir := pawnMoveDirections[pawnCol]

	// Move one rank forward
	targetSq := pawnSq + dir
	if IsValidSquare(targetSq) && p.board[targetSq] == NoPiece {
		if (pawnCol == White && RankOf(targetSq) == Rank8) ||
			(pawnCol == Black && RankOf(targetSq) == Rank1) {
			// Pawn promotion move
			ml.add(moveOf(pawnPromotionMove, pawnSq, targetSq, pawnPc, NoPiece, Queen))
			ml.add(moveOf(pawnPromotionMove, pawnSq, targetSq, pawnPc, NoPiece, Rook))
			ml.add(moveOf(pawnPromotionMove, pawnSq, targetSq, pawnPc, NoPiece, Bishop))
			ml.add(moveOf(pawnPromotionMove, pawnSq, targetSq, pawnPc, NoPiece, Knight))
		} else {
			// Normal move
			ml.add(moveOf(normalMove, pawnSq, targetSq, pawnPc, NoPiece, NoPieceType))

			targetSq += dir
			if IsValidSquare(targetSq) && p.board[targetSq] == NoPiece {
				if (pawnCol == White && RankOf(targetSq) == Rank4) ||
					(pawnCol == Black && RankOf(targetSq) == Rank5) {
					// Pawn double move
					ml.add(moveOf(pawnDoubleMove, pawnSq, targetSq, pawnPc, NoPiece, NoPieceType))
				}
			}
		}
	}
}

func addPieceMoves(ml *MoveList, p *Position, originSq Square, directions []direction) {
	originPc := p.board[originSq]
	sliding := isSliding(PieceTypeOf(originPc))
	oppositeCol := OppositeOf(PieceColorOf(originPc))

	for _, dir := range directions {
		targetSq := originSq + dir
		for IsValidSquare(targetSq) {
			targetPc := p.board[targetSq]
			if targetPc != NoPiece {
				if PieceColorOf(targetPc) == oppositeCol {
					// Capturing move
					ml.add(moveOf(normalMove, originSq, targetSq, originPc, targetPc, NoPieceType))
				}
				break
			} else {
				// Normal move
				ml.add(moveOf(normalMove, originSq, targetSq, originPc, NoPiece, NoPieceType))
				if !sliding {
					break
				}
				targetSq += dir
			}
		}
	}
}

func addCastlingMoves(ml *MoveList, p *Position, kingSq Square) {
	kingPc := p.board[kingSq]

	if PieceColorOf(kingPc) == White {
		// Do not test g1 whether it is attacked as we will test it later
		if (p.CastlingRights&WhiteKingside) != NoCastling &&
			p.board[F1] == NoPiece &&
			p.board[G1] == NoPiece &&
			!p.isAttacked(F1, Black) {
			ml.add(moveOf(castlingMove, kingSq, G1, kingPc, NoPiece, NoPieceType))
		}
		// Do not test c1 whether it is attacked as we will test it later
		if (p.CastlingRights&WhiteQueenside) != NoCastling &&
			p.board[B1] == NoPiece &&
			p.board[C1] == NoPiece &&
			p.board[D1] == NoPiece &&
			!p.isAttacked(D1, Black) {
			ml.add(moveOf(castlingMove, kingSq, C1, kingPc, NoPiece, NoPieceType))
		}
	} else {
		// Do not test g8 whether it is attacked as we will test it later
		if (p.CastlingRights&BlackKingside) != NoCastling &&
			p.board[F8] == NoPiece &&
			p.board[G8] == NoPiece &&
			!p.isAttacked(F8, White) {
			ml.add(moveOf(castlingMove, kingSq, G8, kingPc, NoPiece, NoPieceType))
		}
		// Do not test c8 whether it is attacked as we will test it later
		if (p.CastlingRights&BlackQueenside) != NoCastling &&
			p.board[B8] == NoPiece &&
			p.board[C8] == NoPiece &&
			p.board[D8] == NoPiece &&
			!p.isAttacked(D8, White) {
			ml.add(moveOf(castlingMove, kingSq, C8, kingPc, NoPiece, NoPieceType))
		}
	}
}
