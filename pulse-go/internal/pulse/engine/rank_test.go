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

func TestRank(t *testing.T) {
	t.Run("Ranks array should be valid", func(t *testing.T) {
		for r := range Ranks {
			if Ranks[r] != r {
				t.Errorf("wanted Ranks at index %d to be %v, but got %v", r, r, Ranks[r])
			}
		}
	})
}

func TestRank_IsValidRank(t *testing.T) {
	tests := []struct {
		rank Rank
		want bool
	}{
		{rank: Rank1, want: true},
		{rank: Rank2, want: true},
		{rank: Rank3, want: true},
		{rank: Rank4, want: true},
		{rank: Rank5, want: true},
		{rank: Rank6, want: true},
		{rank: Rank7, want: true},
		{rank: Rank8, want: true},
		{rank: NoRank, want: false},
	}
	for _, tt := range tests {
		t.Run("IsValidRank should return true if rank is valid, false otherwise", func(t *testing.T) {
			valid := IsValidRank(tt.rank)
			if valid != tt.want {
				t.Errorf("IsValidRank(%d) = %v, want %v", tt.rank, valid, tt.want)
			}
		})
	}
}
