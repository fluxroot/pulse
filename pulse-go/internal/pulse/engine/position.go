/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import (
	"fmt"
)

func NewPosition() *Position {
	position := &Position{
		ActiveColor:     NoColor,
		CastlingRights:  NoCastling,
		EnPassantSquare: NoSquare,
		HalfmoveClock:   0,
		HalfmoveNumber:  0,
		board:           [squaresMaxValue]Piece{},
		pieces:          [len(Colors)][len(PieceTypes)]bitboard{},
		stateList: stateList{
			size:    0,
			entries: [MaxPly]stateEntry{},
		},
	}
	for sq := range position.board {
		position.board[sq] = NoPiece
	}
	for col := range position.pieces {
		for pt := range position.pieces[col] {
			position.pieces[col][pt] = bitboard(0)
		}
	}
	for i := range position.stateList.entries {
		position.stateList.entries[i] = stateEntry{
			castlingRights:  NoCastling,
			enPassantSquare: NoSquare,
			halfmoveClock:   0,
		}
	}
	return position
}

type Position struct {
	ActiveColor     Color
	CastlingRights  Castling
	EnPassantSquare Square
	HalfmoveClock   int
	HalfmoveNumber  int

	board  [squaresMaxValue]Piece
	pieces [len(Colors)][len(PieceTypes)]bitboard

	stateList stateList
}

func (p *Position) MakeMove(m Move) {
	p.saveState()

	mt := MoveTypeOf(m)
	originSq := OriginSquareOf(m)
	targetSq := TargetSquareOf(m)
	originPc := OriginPieceOf(m)
	originCol := PieceColorOf(originPc)
	targetPc := TargetPieceOf(m)

	if targetPc != NoPiece {
		captureSq := targetSq
		if mt == EnPassantMove {
			oppositeDir := pawnMoveDirections[OppositeOf(originCol)]
			captureSq += oppositeDir
		}
		p.Remove(captureSq)
		p.clearCastling(captureSq)
	}

	p.Remove(originSq)
	if mt == PawnPromotionMove {
		p.Put(PieceOf(originCol, PromotionOf(m)), targetSq)
	} else {
		p.Put(originPc, targetSq)
	}

	if mt == CastlingMove {
		switch targetSq {
		case G1:
			p.Put(p.Remove(H1), F1)
		case C1:
			p.Put(p.Remove(A1), D1)
		case G8:
			p.Put(p.Remove(H8), F8)
		case C8:
			p.Put(p.Remove(A8), D8)
		default:
			panic(fmt.Sprintf("Invalid target square: %v", targetSq))
		}
	}

	p.clearCastling(originSq)

	if mt == PawnDoubleMove {
		oppositeDir := pawnMoveDirections[OppositeOf(originCol)]
		p.EnPassantSquare = targetSq + oppositeDir
	} else {
		p.EnPassantSquare = NoSquare
	}

	p.ActiveColor = OppositeOf(p.ActiveColor)

	if PieceTypeOf(originPc) == Pawn || targetPc != NoPiece {
		p.HalfmoveClock = 0
	} else {
		p.HalfmoveClock++
	}

	p.HalfmoveNumber++
}

func (p *Position) UndoMove(m Move) {
	mt := MoveTypeOf(m)
	originSq := OriginSquareOf(m)
	targetSq := TargetSquareOf(m)
	originPc := OriginPieceOf(m)
	originCol := PieceColorOf(originPc)
	targetPc := TargetPieceOf(m)

	p.HalfmoveNumber--

	p.ActiveColor = OppositeOf(p.ActiveColor)

	if mt == CastlingMove {
		switch targetSq {
		case G1:
			p.Put(p.Remove(F1), H1)
		case C1:
			p.Put(p.Remove(D1), A1)
		case G8:
			p.Put(p.Remove(F8), H8)
		case C8:
			p.Put(p.Remove(D8), A8)
		default:
			panic(fmt.Sprintf("Invalid target square: %v", targetSq))
		}
	}

	p.Remove(targetSq)
	p.Put(originPc, originSq)

	if targetPc != NoPiece {
		captureSq := targetSq
		if mt == EnPassantMove {
			oppositeDir := pawnMoveDirections[OppositeOf(originCol)]
			captureSq += oppositeDir
		}
		p.Put(targetPc, captureSq)
	}

	p.restoreState()
}

func (p *Position) saveState() {
	entry := &p.stateList.entries[p.stateList.size]
	entry.castlingRights = p.CastlingRights
	entry.enPassantSquare = p.EnPassantSquare
	entry.halfmoveClock = p.HalfmoveClock
	p.stateList.size++
}

func (p *Position) restoreState() {
	p.stateList.size--
	entry := &p.stateList.entries[p.stateList.size]
	p.CastlingRights = entry.castlingRights
	p.EnPassantSquare = entry.enPassantSquare
	p.HalfmoveClock = entry.halfmoveClock
}

func (p *Position) Get(sq Square) Piece {
	return p.board[sq]
}

func (p *Position) Put(pc Piece, sq Square) {
	col := PieceColorOf(pc)
	pt := PieceTypeOf(pc)

	p.board[sq] = pc
	p.pieces[col][pt] = addSquare(sq, p.pieces[col][pt])
}

func (p *Position) Remove(sq Square) Piece {
	pc := p.board[sq]
	col := PieceColorOf(pc)
	pt := PieceTypeOf(pc)

	p.board[sq] = NoPiece
	p.pieces[col][pt] = removeSquare(sq, p.pieces[col][pt])

	return pc
}

func (p *Position) SetCastlingRight(cast Castling) {
	p.CastlingRights |= cast
}

func (p *Position) clearCastling(sq Square) {
	switch sq {
	case A1:
		p.CastlingRights = p.CastlingRights & ^WhiteQueenside
	case A8:
		p.CastlingRights = p.CastlingRights & ^BlackQueenside
	case H1:
		p.CastlingRights = p.CastlingRights & ^WhiteKingside
	case H8:
		p.CastlingRights = p.CastlingRights & ^BlackKingside
	case E1:
		p.CastlingRights = p.CastlingRights & ^(WhiteKingside | WhiteQueenside)
	case E8:
		p.CastlingRights = p.CastlingRights & ^(BlackKingside | BlackQueenside)
	}
}

type stateList struct {
	size    int
	entries [MaxPly]stateEntry
}

type stateEntry struct {
	castlingRights  Castling
	enPassantSquare Square
	halfmoveClock   int
}
