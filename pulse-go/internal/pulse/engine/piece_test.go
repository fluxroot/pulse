/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import (
	"testing"
)

func TestPiece(t *testing.T) {
	t.Run("Pieces array should be valid", func(t *testing.T) {
		for pc := range Pieces {
			if Pieces[pc] != pc {
				t.Errorf("wanted Pieces at index %d to be %v, but got %v", pc, pc, Pieces[pc])
			}
		}
	})
}

func TestPiece_IsValidPiece(t *testing.T) {
	tests := []struct {
		piece Piece
		want  bool
	}{
		{piece: WhitePawn, want: true},
		{piece: WhiteKnight, want: true},
		{piece: WhiteBishop, want: true},
		{piece: WhiteRook, want: true},
		{piece: WhiteQueen, want: true},
		{piece: WhiteKing, want: true},
		{piece: BlackPawn, want: true},
		{piece: BlackKnight, want: true},
		{piece: BlackBishop, want: true},
		{piece: BlackRook, want: true},
		{piece: BlackQueen, want: true},
		{piece: BlackKing, want: true},
		{piece: NoPiece, want: false},
	}
	for _, tt := range tests {
		t.Run("IsValidPiece should return true if piece is valid, false otherwise", func(t *testing.T) {
			valid := IsValidPiece(tt.piece)
			if valid != tt.want {
				t.Errorf("IsValidPiece(%d) = %v, want %v", tt.piece, valid, tt.want)
			}
		})
	}
}

func TestPiece_PieceOf(t *testing.T) {
	tests := []struct {
		color     Color
		pieceType PieceType
		want      Piece
	}{
		{color: White, pieceType: Pawn, want: WhitePawn},
		{color: White, pieceType: Knight, want: WhiteKnight},
		{color: White, pieceType: Bishop, want: WhiteBishop},
		{color: White, pieceType: Rook, want: WhiteRook},
		{color: White, pieceType: Queen, want: WhiteQueen},
		{color: White, pieceType: King, want: WhiteKing},
		{color: Black, pieceType: Pawn, want: BlackPawn},
		{color: Black, pieceType: Knight, want: BlackKnight},
		{color: Black, pieceType: Bishop, want: BlackBishop},
		{color: Black, pieceType: Rook, want: BlackRook},
		{color: Black, pieceType: Queen, want: BlackQueen},
		{color: Black, pieceType: King, want: BlackKing},
	}
	for _, tt := range tests {
		t.Run("PieceOf should return the piece", func(t *testing.T) {
			pc := PieceOf(tt.color, tt.pieceType)
			if pc != tt.want {
				t.Errorf("wanted piece of color %v and piece type %v to be %v, but got %v", tt.color, tt.pieceType, tt.want, pc)
			}
		})
	}
}

func TestPiece_PieceColorOf(t *testing.T) {
	tests := []struct {
		piece Piece
		want  Color
	}{
		{piece: WhitePawn, want: White},
		{piece: WhiteKnight, want: White},
		{piece: WhiteBishop, want: White},
		{piece: WhiteRook, want: White},
		{piece: WhiteQueen, want: White},
		{piece: WhiteKing, want: White},
		{piece: BlackPawn, want: Black},
		{piece: BlackKnight, want: Black},
		{piece: BlackBishop, want: Black},
		{piece: BlackRook, want: Black},
		{piece: BlackQueen, want: Black},
		{piece: BlackKing, want: Black},
	}
	for _, tt := range tests {
		t.Run("PieceColorOf should return the piece color", func(t *testing.T) {
			col := PieceColorOf(tt.piece)
			if col != tt.want {
				t.Errorf("wanted color of %v to be %v, but got %v", tt.piece, tt.want, col)
			}
		})
	}
}

func TestPiece_PieceTypeOf(t *testing.T) {
	tests := []struct {
		piece Piece
		want  PieceType
	}{
		{piece: WhitePawn, want: Pawn},
		{piece: WhiteKnight, want: Knight},
		{piece: WhiteBishop, want: Bishop},
		{piece: WhiteRook, want: Rook},
		{piece: WhiteQueen, want: Queen},
		{piece: WhiteKing, want: King},
		{piece: BlackPawn, want: Pawn},
		{piece: BlackKnight, want: Knight},
		{piece: BlackBishop, want: Bishop},
		{piece: BlackRook, want: Rook},
		{piece: BlackQueen, want: Queen},
		{piece: BlackKing, want: King},
	}
	for _, tt := range tests {
		t.Run("PieceTypeOf should return the piece type", func(t *testing.T) {
			pt := PieceTypeOf(tt.piece)
			if pt != tt.want {
				t.Errorf("wanted piece type of %v to be %v, but got %v", tt.piece, tt.want, pt)
			}
		})
	}
}
