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

func TestSquare(t *testing.T) {
	t.Run("Squares array should be valid", func(t *testing.T) {
		for r := range Ranks {
			for f := range Files {
				index := r*len(Ranks) + f
				sq := SquareOf(f, r)
				if Squares[index] != sq {
					t.Errorf("wanted Squares at index %d to be %v, but got %v", index, sq, Squares[index])
				}
				if !IsValidSquare(sq) {
					t.Errorf("wanted %v to be valid", sq)
				}
			}
		}
	})
}

func TestSquare_SquareOf(t *testing.T) {
	t.Run("When creating a square it should save and return file and rank correctly", func(t *testing.T) {
		for _, r := range Ranks {
			for _, f := range Files {
				sq := SquareOf(f, r)
				squareFile := FileOf(sq)
				if squareFile != f {
					t.Errorf("wanted file of square to be %v, but got %v", f, squareFile)
				}
				squareRank := RankOf(sq)
				if squareRank != r {
					t.Errorf("wanted rank of square to be %v, but got %v", r, squareRank)
				}
			}
		}
	})
}
