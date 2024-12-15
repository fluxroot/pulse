/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

import (
	"testing"
)

func TestSquare(t *testing.T) {
	t.Run("Square array should be valid", func(t *testing.T) {
		for rank := range Ranks {
			for file := range Files {
				index := rank*len(Ranks) + file
				square := SquareOf(file, rank)
				if Squares[index] != square {
					t.Errorf("wanted squares at index %d to be %v, got %v", index, square, Squares[index])
				}
			}
		}
	})

	t.Run("When creating a square it should save and return file and rank correctly", func(t *testing.T) {
		for _, rank := range Ranks {
			for _, file := range Files {
				square := SquareOf(file, rank)
				if FileOf(square) != file {
					t.Errorf("wanted file of square to be %v, got %v", file, FileOf(square))
				}
				if RankOf(square) != rank {
					t.Errorf("wanted rank of square to be %v, got %v", rank, RankOf(square))
				}
			}
		}
	})
}
