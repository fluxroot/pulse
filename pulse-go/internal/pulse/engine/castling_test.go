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

func TestCastling_IsValidCastling(t *testing.T) {
	tests := []struct {
		castling Castling
		want     bool
	}{
		{castling: WhiteKingside, want: true},
		{castling: WhiteQueenside, want: true},
		{castling: BlackKingside, want: true},
		{castling: BlackQueenside, want: true},
		{castling: NoCastling, want: false},
	}
	for _, tt := range tests {
		t.Run("IsValidCastling should return true if castling is valid, false otherwise", func(t *testing.T) {
			valid := IsValidCastling(tt.castling)
			if valid != tt.want {
				t.Errorf("IsValidCastling(%d) = %v, want %v", tt.castling, valid, tt.want)
			}
		})
	}
}

func TestCastling_CastlingOf(t *testing.T) {
	tests := []struct {
		color        Color
		castlingType CastlingType
		want         Castling
	}{
		{color: White, castlingType: Kingside, want: WhiteKingside},
		{color: White, castlingType: Queenside, want: WhiteQueenside},
		{color: Black, castlingType: Kingside, want: BlackKingside},
		{color: Black, castlingType: Queenside, want: BlackQueenside},
	}
	for _, tt := range tests {
		t.Run("CastlingOf should return the castling", func(t *testing.T) {
			cast := CastlingOf(tt.color, tt.castlingType)
			if cast != tt.want {
				t.Errorf("wanted castling of color %v and castling type %v to be %v, but got %v", tt.color, tt.castlingType, tt.want, cast)
			}
		})
	}
}

func TestCastling_CastlingColorOf(t *testing.T) {
	tests := []struct {
		castling Castling
		want     Color
	}{
		{castling: WhiteKingside, want: White},
		{castling: WhiteQueenside, want: White},
		{castling: BlackKingside, want: Black},
		{castling: BlackQueenside, want: Black},
	}
	for _, tt := range tests {
		t.Run("CastlingColorOf should return the castling color", func(t *testing.T) {
			col := CastlingColorOf(tt.castling)
			if col != tt.want {
				t.Errorf("wanted color of %v to be %v, but got %v", tt.castling, tt.want, col)
			}
		})
	}
}

func TestCastling_CastlingTypeOf(t *testing.T) {
	tests := []struct {
		castling Castling
		want     CastlingType
	}{
		{castling: WhiteKingside, want: Kingside},
		{castling: WhiteQueenside, want: Queenside},
		{castling: BlackKingside, want: Kingside},
		{castling: BlackQueenside, want: Queenside},
	}
	for _, tt := range tests {
		t.Run("CastlingTypeOf should return the castling type", func(t *testing.T) {
			pt := CastlingTypeOf(tt.castling)
			if pt != tt.want {
				t.Errorf("wanted castling type of %v to be %v, but got %v", tt.castling, tt.want, pt)
			}
		})
	}
}
