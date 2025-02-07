/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import "testing"

func TestPosition_ActiveColor(t *testing.T) {
	tests := []struct {
		name        string
		setup       func(p *Position)
		move        Move
		activeColor Color
		want        Color
	}{
		{
			name: "Moving a white piece should set the active color to black",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(BlackKing, E8)
			},
			move:        MoveOf(NormalMove, E1, E2, WhiteKing, NoPiece, NoPieceType),
			activeColor: White,
			want:        Black,
		},
		{
			name: "Moving a black piece should set the active color to white",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(BlackKing, E8)
			},
			move:        MoveOf(NormalMove, E8, E7, BlackKing, NoPiece, NoPieceType),
			activeColor: Black,
			want:        White,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			p := &Position{
				ActiveColor: tt.activeColor,
			}
			tt.setup(p)
			p.MakeMove(tt.move)
			if p.ActiveColor != tt.want {
				t.Errorf("wanted active color to be %v, but got %v", tt.want, p.ActiveColor)
			}
			p.UndoMove(tt.move)
			if p.ActiveColor != tt.activeColor {
				t.Errorf("wanted active color to be %v, but got %v", tt.activeColor, p.ActiveColor)
			}
		})
	}
}

func TestPosition_CastlingRights(t *testing.T) {
	tests := []struct {
		name           string
		setup          func(p *Position)
		move           Move
		activeColor    Color
		castlingRights Castling
		want           Castling
	}{
		{
			name: "Moving white's kingside rook should remove white's kingside castling right",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhiteRook, A1)
				p.Put(WhiteRook, H1)
				p.Put(BlackKing, E8)
				p.Put(BlackRook, A8)
				p.Put(BlackRook, H8)
			},
			move:           MoveOf(NormalMove, H1, H2, WhiteRook, NoPiece, NoPieceType),
			activeColor:    White,
			castlingRights: WhiteKingside | WhiteQueenside | BlackKingside | BlackQueenside,
			want:           WhiteQueenside | BlackKingside | BlackQueenside,
		},
		{
			name: "Moving white's queenside rook should remove white's queenside castling right",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhiteRook, A1)
				p.Put(WhiteRook, H1)
				p.Put(BlackKing, E8)
				p.Put(BlackRook, A8)
				p.Put(BlackRook, H8)
			},
			move:           MoveOf(NormalMove, A1, A2, WhiteRook, NoPiece, NoPieceType),
			activeColor:    White,
			castlingRights: WhiteKingside | WhiteQueenside | BlackKingside | BlackQueenside,
			want:           WhiteKingside | BlackKingside | BlackQueenside,
		},
		{
			name: "Moving white's king should remove white's kingside and queenside castling right",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhiteRook, A1)
				p.Put(WhiteRook, H1)
				p.Put(BlackKing, E8)
				p.Put(BlackRook, A8)
				p.Put(BlackRook, H8)
			},
			move:           MoveOf(NormalMove, E1, E2, WhiteKing, NoPiece, NoPieceType),
			activeColor:    White,
			castlingRights: WhiteKingside | WhiteQueenside | BlackKingside | BlackQueenside,
			want:           BlackKingside | BlackQueenside,
		},
		{
			name: "Moving black's kingside rook should remove black's kingside castling right",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhiteRook, A1)
				p.Put(WhiteRook, H1)
				p.Put(BlackKing, E8)
				p.Put(BlackRook, A8)
				p.Put(BlackRook, H8)
			},
			move:           MoveOf(NormalMove, H8, H7, BlackRook, NoPiece, NoPieceType),
			activeColor:    Black,
			castlingRights: WhiteKingside | WhiteQueenside | BlackKingside | BlackQueenside,
			want:           WhiteKingside | WhiteQueenside | BlackQueenside,
		},
		{
			name: "Moving black's queenside rook should remove blacks's queenside castling right",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhiteRook, A1)
				p.Put(WhiteRook, H1)
				p.Put(BlackKing, E8)
				p.Put(BlackRook, A8)
				p.Put(BlackRook, H8)
			},
			move:           MoveOf(NormalMove, A8, A7, BlackRook, NoPiece, NoPieceType),
			activeColor:    Black,
			castlingRights: WhiteKingside | WhiteQueenside | BlackKingside | BlackQueenside,
			want:           WhiteKingside | WhiteQueenside | BlackKingside,
		},
		{
			name: "Moving black's king should remove blacks's kingside and queenside castling right",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhiteRook, A1)
				p.Put(WhiteRook, H1)
				p.Put(BlackKing, E8)
				p.Put(BlackRook, A8)
				p.Put(BlackRook, H8)
			},
			move:           MoveOf(NormalMove, E8, E7, BlackKing, NoPiece, NoPieceType),
			activeColor:    Black,
			castlingRights: WhiteKingside | WhiteQueenside | BlackKingside | BlackQueenside,
			want:           WhiteKingside | WhiteQueenside,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			p := &Position{
				ActiveColor:    tt.activeColor,
				CastlingRights: tt.castlingRights,
			}
			tt.setup(p)
			p.MakeMove(tt.move)
			if p.CastlingRights != tt.want {
				t.Errorf("wanted castling rights to be %v, but got %v", tt.want, p.CastlingRights)
			}
			p.UndoMove(tt.move)
			if p.CastlingRights != tt.castlingRights {
				t.Errorf("wanted castling rights to be %v, but got %v", tt.castlingRights, p.CastlingRights)
			}
		})
	}
}

func TestPosition_EnPassantSquare(t *testing.T) {
	tests := []struct {
		name            string
		setup           func(p *Position)
		move            Move
		activeColor     Color
		enPassantSquare Square
		want            Square
	}{
		{
			name: "Making a pawn double move for white should set the en passant square",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhitePawn, E2)
				p.Put(BlackKing, E8)
			},
			move:            MoveOf(PawnDoubleMove, E2, E4, WhitePawn, NoPiece, NoPieceType),
			activeColor:     White,
			enPassantSquare: NoSquare,
			want:            E3,
		},
		{
			name: "Making a pawn double move for black should set the en passant square",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(BlackKing, E8)
				p.Put(BlackPawn, E7)
			},
			move:            MoveOf(PawnDoubleMove, E7, E5, BlackPawn, NoPiece, NoPieceType),
			activeColor:     Black,
			enPassantSquare: NoSquare,
			want:            E6,
		},
		{
			name: "Making an en passant move for white should clear the en passant square",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhitePawn, D5)
				p.Put(BlackKing, E8)
				p.Put(BlackPawn, E5)
			},
			move:            MoveOf(EnPassantMove, D5, E6, WhitePawn, NoPiece, NoPieceType),
			activeColor:     White,
			enPassantSquare: E6,
			want:            NoSquare,
		},
		{
			name: "Making an en passant move for black should clear the en passant square",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhitePawn, E4)
				p.Put(BlackKing, E8)
				p.Put(BlackPawn, D4)
			},
			move:            MoveOf(EnPassantMove, D4, E3, BlackPawn, NoPiece, NoPieceType),
			activeColor:     Black,
			enPassantSquare: E3,
			want:            NoSquare,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			p := &Position{
				ActiveColor:     tt.activeColor,
				EnPassantSquare: tt.enPassantSquare,
			}
			tt.setup(p)
			p.MakeMove(tt.move)
			if p.EnPassantSquare != tt.want {
				t.Errorf("wanted en passant square to be %v, but got %v", tt.want, p.EnPassantSquare)
			}
			p.UndoMove(tt.move)
			if p.EnPassantSquare != tt.enPassantSquare {
				t.Errorf("wanted en passant square to be %v, but got %v", tt.enPassantSquare, p.EnPassantSquare)
			}
		})
	}
}

func TestPosition_HalfmoveClock(t *testing.T) {
	tests := []struct {
		name          string
		setup         func(p *Position)
		move          Move
		activeColor   Color
		halfmoveClock int
		want          int
	}{
		{
			name: "Making a move should increment the halfmove clock",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(BlackKing, E8)
			},
			move:          MoveOf(NormalMove, E1, E2, WhiteKing, NoPiece, NoPieceType),
			activeColor:   White,
			halfmoveClock: 0,
			want:          1,
		},
		{
			name: "Moving a pawn should reset the halfmove clock",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhitePawn, E2)
				p.Put(BlackKing, E8)
			},
			move:          MoveOf(NormalMove, E2, E3, WhitePawn, NoPiece, NoPieceType),
			activeColor:   White,
			halfmoveClock: 1,
			want:          0,
		},
		{
			name: "Capturing a piece should reset the halfmove clock",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhiteQueen, D1)
				p.Put(BlackKing, E8)
				p.Put(BlackPawn, D7)
			},
			move:          MoveOf(NormalMove, D1, D7, WhiteQueen, BlackPawn, NoPieceType),
			activeColor:   White,
			halfmoveClock: 1,
			want:          0,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			p := &Position{
				ActiveColor:   tt.activeColor,
				HalfmoveClock: tt.halfmoveClock,
			}
			tt.setup(p)
			p.MakeMove(tt.move)
			if p.HalfmoveClock != tt.want {
				t.Errorf("wanted halfmove clock to be %v, but got %v", tt.want, p.HalfmoveClock)
			}
			p.UndoMove(tt.move)
			if p.HalfmoveClock != tt.halfmoveClock {
				t.Errorf("wanted halfmove clock to be %v, but got %v", tt.halfmoveClock, p.HalfmoveClock)
			}
		})
	}
}

func TestPosition_HalfmoveNumber(t *testing.T) {
	tests := []struct {
		name           string
		setup          func(p *Position)
		move           Move
		activeColor    Color
		halfmoveNumber int
		want           int
	}{
		{
			name: "Making a move should increment the halfmove number",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(BlackKing, E8)
			},
			move:           MoveOf(NormalMove, E1, E2, WhiteKing, NoPiece, NoPieceType),
			activeColor:    White,
			halfmoveNumber: 0,
			want:           1,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			p := &Position{
				ActiveColor:    tt.activeColor,
				HalfmoveNumber: tt.halfmoveNumber,
			}
			tt.setup(p)
			p.MakeMove(tt.move)
			if p.HalfmoveNumber != tt.want {
				t.Errorf("wanted halfmove number to be %v, but got %v", tt.want, p.HalfmoveNumber)
			}
			p.UndoMove(tt.move)
			if p.HalfmoveNumber != tt.halfmoveNumber {
				t.Errorf("wanted halfmove number to be %v, but got %v", tt.halfmoveNumber, p.HalfmoveNumber)
			}
		})
	}
}

func TestPosition_board(t *testing.T) {
	tests := []struct {
		name                  string
		setup                 func(p *Position)
		move                  Move
		activeColor           Color
		wantOriginSquarePiece Piece
		wantTargetSquarePiece Piece
	}{
		{
			name: "Capturing a piece should replace the target piece by the origin piece",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhiteQueen, D1)
				p.Put(BlackKing, E8)
				p.Put(BlackPawn, D7)
			},
			move:                  MoveOf(NormalMove, D1, D7, WhiteQueen, BlackPawn, NoPieceType),
			activeColor:           White,
			wantOriginSquarePiece: NoPiece,
			wantTargetSquarePiece: WhiteQueen,
		},
		{
			name: "Making a pawn promotion move for white should replace the pawn by the promotion",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(WhitePawn, C7)
				p.Put(BlackKing, E8)
			},
			move:                  MoveOf(PawnPromotionMove, C7, C8, WhitePawn, NoPiece, Queen),
			activeColor:           White,
			wantOriginSquarePiece: NoPiece,
			wantTargetSquarePiece: WhiteQueen,
		},
		{
			name: "Making a pawn promotion move for black should replace the pawn by the promotion",
			setup: func(p *Position) {
				p.Put(WhiteKing, E1)
				p.Put(BlackKing, E8)
				p.Put(BlackPawn, C2)
			},
			move:                  MoveOf(PawnPromotionMove, C2, C1, BlackPawn, NoPiece, Queen),
			activeColor:           Black,
			wantOriginSquarePiece: NoPiece,
			wantTargetSquarePiece: BlackQueen,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			p := &Position{
				ActiveColor: tt.activeColor,
			}
			tt.setup(p)
			p.MakeMove(tt.move)
			originSq := OriginSquareOf(tt.move)
			targetSq := TargetSquareOf(tt.move)
			originPc := OriginPieceOf(tt.move)
			targetPc := TargetPieceOf(tt.move)
			if p.board[originSq] != tt.wantOriginSquarePiece {
				t.Errorf("wanted piece %v to be on origin square, but got %v", tt.wantOriginSquarePiece, p.board[originSq])
			}
			if p.board[targetSq] != tt.wantTargetSquarePiece {
				t.Errorf("wanted piece %v to be on target square, but got %v", tt.wantTargetSquarePiece, p.board[targetSq])
			}
			p.UndoMove(tt.move)
			if p.board[originSq] != originPc {
				t.Errorf("wanted piece %v to be on origin square, but got %v", originPc, p.board[originSq])
			}
			if p.board[targetSq] != targetPc {
				t.Errorf("wanted piece %v to be on target square, but got %v", targetPc, p.board[targetSq])
			}
		})
	}
}
