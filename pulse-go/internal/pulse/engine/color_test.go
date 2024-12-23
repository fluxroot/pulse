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

func TestColor(t *testing.T) {
	t.Run("Colors array should be valid", func(t *testing.T) {
		for col := range Colors {
			if Colors[col] != col {
				t.Errorf("wanted Colors at index %d to be %v, but got %v", col, col, Colors[col])
			}
		}
	})
}

func TestColor_IsValidColor(t *testing.T) {
	tests := []struct {
		color Color
		want  bool
	}{
		{color: White, want: true},
		{color: Black, want: true},
		{color: NoColor, want: false},
	}
	for _, tt := range tests {
		t.Run("IsValidColor should return true if color is valid, false otherwise", func(t *testing.T) {
			valid := IsValidColor(tt.color)
			if valid != tt.want {
				t.Errorf("IsValidColor(%d) = %v, want %v", tt.color, valid, tt.want)
			}
		})
	}
}

func TestColor_OppositeOf(t *testing.T) {
	tests := []struct {
		color Color
		want  Color
	}{
		{color: White, want: Black},
		{color: Black, want: White},
	}
	for _, tt := range tests {
		t.Run("OppositeOf should return the opposite color", func(t *testing.T) {
			opposite := OppositeOf(tt.color)
			if opposite != tt.want {
				t.Errorf("wanted opposite color of %v to be %v, but got %v", tt.color, tt.want, opposite)
			}
		})
	}
}
