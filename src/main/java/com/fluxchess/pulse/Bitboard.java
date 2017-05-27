/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import static java.lang.Long.bitCount;
import static java.lang.Long.numberOfTrailingZeros;

/**
 * Bitboard stores squares as bits in a 64-bit long. We provide methods to
 * convert bit squares to 0x88 squares and vice versa.
 */
final class Bitboard {

	long squares = 0;

	static int next(long squares) {
		return toX88Square(numberOfTrailingZeros(squares));
	}

	static long remainder(long squares) {
		return squares & (squares - 1);
	}

	private static int toX88Square(int square) {
		return ((square & ~7) << 1) | (square & 7);
	}

	private static int toBitSquare(int square) {
		return ((square & ~7) >>> 1) | (square & 7);
	}

	int size() {
		return bitCount(squares);
	}

	void add(int square) {
		squares |= 1L << toBitSquare(square);
	}

	void remove(int square) {
		squares &= ~(1L << toBitSquare(square));
	}
}
