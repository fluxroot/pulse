/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import (
	"math/bits"
)

type bitboard = uint64
type bitSquare = int

func addSquare(sq Square, bb bitboard) bitboard {
	return bb | (1 << toBitSquare(sq))
}

func removeSquare(sq Square, bb bitboard) bitboard {
	return bb & ^(1 << toBitSquare(sq))
}

func next(bb bitboard) Square {
	return toX88Square(bits.TrailingZeros64(bb))

}

func remainder(bb bitboard) bitboard {
	return bb & (bb - 1)
}

func toBitSquare(sq Square) bitSquare {
	return ((sq & ^7) >> 1) | (sq & 7)
}

func toX88Square(sq bitSquare) Square {
	return ((sq & ^7) << 1) | (sq & 7)
}
