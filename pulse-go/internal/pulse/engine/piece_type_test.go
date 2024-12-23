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

func TestPieceType(t *testing.T) {
	t.Run("PieceTypes array should be valid", func(t *testing.T) {
		for pt := range PieceTypes {
			if PieceTypes[pt] != pt {
				t.Errorf("wanted PieceTypes at index %d to be %v, but got %v", pt, pt, PieceTypes[pt])
			}
		}
	})
}

func TestPieceType_IsValidPieceType(t *testing.T) {
	tests := []struct {
		pieceType PieceType
		want      bool
	}{
		{pieceType: Pawn, want: true},
		{pieceType: Knight, want: true},
		{pieceType: Bishop, want: true},
		{pieceType: Rook, want: true},
		{pieceType: Queen, want: true},
		{pieceType: King, want: true},
		{pieceType: NoPieceType, want: false},
	}
	for _, tt := range tests {
		t.Run("IsValidPieceType should return true if piece type is valid, false otherwise", func(t *testing.T) {
			valid := IsValidPieceType(tt.pieceType)
			if valid != tt.want {
				t.Errorf("IsValidPieceType(%d) = %v, want %v", tt.pieceType, valid, tt.want)
			}
		})
	}
}

func TestPieceType_isSliding(t *testing.T) {
	tests := []struct {
		pieceType PieceType
		want      bool
	}{
		{pieceType: Pawn, want: false},
		{pieceType: Knight, want: false},
		{pieceType: Bishop, want: true},
		{pieceType: Rook, want: true},
		{pieceType: Queen, want: true},
		{pieceType: King, want: false},
	}
	for _, tt := range tests {
		t.Run("isSliding should return true when the pieceType is sliding", func(t *testing.T) {
			sliding := isSliding(tt.pieceType)
			if sliding != tt.want {
				t.Errorf("wanted piece type %v to be sliding: %v, but got %v", tt.pieceType, tt.want, sliding)
			}
		})
	}
}

func TestPieceType_pieceTypeValueOf(t *testing.T) {
	tests := []struct {
		pieceType PieceType
		want      value
	}{
		{pieceType: Pawn, want: pawnValue},
		{pieceType: Knight, want: knightValue},
		{pieceType: Bishop, want: bishopValue},
		{pieceType: Rook, want: rookValue},
		{pieceType: Queen, want: queenValue},
		{pieceType: King, want: kingValue},
	}
	for _, tt := range tests {
		t.Run("pieceTypeValueOf should return the piece type value", func(t *testing.T) {
			v := pieceTypeValueOf(tt.pieceType)
			if v != tt.want {
				t.Errorf("wanted value of %v to be %v, but got %v", tt.pieceType, tt.want, v)
			}
		})
	}
}
