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

func TestMoveList_add(t *testing.T) {
	t.Run("add should add a move to the move list", func(t *testing.T) {
		ml := &MoveList{}
		m := moveOf(pawnPromotionMove, A7, B8, WhitePawn, BlackQueen, Knight)

		ml.add(m)

		if ml.Size != 1 {
			t.Errorf("move list should have size 1, but got %v", ml.Size)
		}
		if ml.Entries[0].Move != m {
			t.Errorf("first move list entry should have move %v, but got %v", m, ml.Entries[0].Move)
		}
	})
}

func TestMoveList_rateFromMVVLVA(t *testing.T) {
	tests := []struct {
		name string
		move Move
		want value
	}{
		{
			name: "rateByMVVLVA should rate a non-capturing move",
			move: moveOf(normalMove, D4, D5, WhitePawn, NoPiece, NoPieceType),
			want: kingValue / pawnValue,
		},
		{
			name: "rateByMVVLVA should rate a capturing move",
			move: moveOf(normalMove, D1, G4, WhiteQueen, BlackKnight, NoPieceType),
			want: kingValue/queenValue + 10*knightValue,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ml := &MoveList{}
			ml.add(tt.move)
			ml.rateByMVVLVA()
			if ml.Entries[0].value != tt.want {
				t.Errorf("rateByMVVLVA() = %v, want %v", ml.Entries[0].value, tt.want)
			}
		})
	}
}

func TestMoveList_sort(t *testing.T) {
	t.Run("sort should sort all moves", func(t *testing.T) {
		ml := &MoveList{}
		m1 := moveOf(normalMove, D1, G4, WhiteQueen, BlackKnight, NoPieceType)
		m2 := moveOf(normalMove, C1, G5, WhiteBishop, BlackPawn, NoPieceType)
		m3 := moveOf(normalMove, F1, B5, WhiteBishop, NoPiece, NoPieceType)
		m4 := moveOf(normalMove, D4, D5, WhitePawn, NoPiece, NoPieceType)
		ml.add(m4)
		ml.Entries[ml.Size-1].value = 1
		ml.add(m3)
		ml.Entries[ml.Size-1].value = 2
		ml.add(m2)
		ml.Entries[ml.Size-1].value = 3
		ml.add(m1)
		ml.Entries[ml.Size-1].value = 4

		ml.sort()

		if ml.Entries[0].Move != m1 {
			t.Errorf("move at index 0 should be %v, but got %v", m1, ml.Entries[0].Move)
		}
		if ml.Entries[1].Move != m2 {
			t.Errorf("move at index 1 should be %v, but got %v", m2, ml.Entries[1].Move)
		}
		if ml.Entries[2].Move != m3 {
			t.Errorf("move at index 2 should be %v, but got %v", m3, ml.Entries[2].Move)
		}
		if ml.Entries[3].Move != m4 {
			t.Errorf("move at index 3 should be %v, but got %v", m4, ml.Entries[3].Move)
		}
	})
}
