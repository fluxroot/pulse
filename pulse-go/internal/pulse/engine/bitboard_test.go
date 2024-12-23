/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import (
	"math"
	"math/rand"
	"testing"
)

func TestBitboard_addSquare(t *testing.T) {
	t.Run("It should add all squares", func(t *testing.T) {
		bb := bitboard(0)
		for _, sq := range shuffledSquares() {
			bb = addSquare(sq, bb)
		}
		if math.MaxUint64 != bb {
			t.Errorf("addSquare() = %v, want %d", bb, bitboard(math.MaxUint64))
		}
	})
}

func TestBitboard_removeSquare(t *testing.T) {
	t.Run("It should remove all squares", func(t *testing.T) {
		bb := bitboard(math.MaxUint64)
		for _, sq := range shuffledSquares() {
			bb = removeSquare(sq, bb)
		}
		if 0 != bb {
			t.Errorf("removeSquare() = %v, want %d", bb, 0)
		}
	})
}

func TestBitboard_next(t *testing.T) {
	t.Run("It should return the next square", func(t *testing.T) {
		bb := addSquare(A6, 0)
		sq := next(bb)
		if A6 != sq {
			t.Errorf("next() = %v, want %v", sq, A6)
		}
	})
}

func TestBitboard_remainder(t *testing.T) {
	t.Run("It should return the remainder", func(t *testing.T) {
		bb := bitboard(0b1110100)
		rem := remainder(bb)
		if 0b1110000 != rem {
			t.Errorf("remainder() = %v, want %v", rem, 0b1110000)
		}
	})
}

func shuffledSquares() []Square {
	sqs := make([]Square, len(Squares))
	for i, v := range rand.Perm(len(Squares)) {
		sqs[v] = Squares[i]
	}
	return sqs
}
