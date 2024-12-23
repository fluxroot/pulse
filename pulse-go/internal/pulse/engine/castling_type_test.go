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

func TestCastlingType(t *testing.T) {
	t.Run("CastlingTypes array should be valid", func(t *testing.T) {
		for ct := range CastlingTypes {
			if CastlingTypes[ct] != ct {
				t.Errorf("wanted CastlingTypes at index %d to be %v, but got %v", ct, ct, CastlingTypes[ct])
			}
		}
	})
}

func TestCastlingType_IsValidCastlingType(t *testing.T) {
	tests := []struct {
		castlingType CastlingType
		want         bool
	}{
		{castlingType: Kingside, want: true},
		{castlingType: Queenside, want: true},
		{castlingType: NoCastlingType, want: false},
	}
	for _, tt := range tests {
		t.Run("IsValidCastlingType should return true if castling type is valid, false otherwise", func(t *testing.T) {
			valid := IsValidCastlingType(tt.castlingType)
			if valid != tt.want {
				t.Errorf("IsValidCastlingType(%d) = %v, want %v", tt.castlingType, valid, tt.want)
			}
		})
	}
}
