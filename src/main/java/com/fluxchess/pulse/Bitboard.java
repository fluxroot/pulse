/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import static java.lang.Long.bitCount;
import static java.lang.Long.numberOfTrailingZeros;

final class Bitboard {

	private Bitboard() {
	}

	static long add(int square, long bitboard) {
		return bitboard | 1L << toBitSquare(square);
	}

	static long remove(int square, long bitboard) {
		return bitboard & ~(1L << toBitSquare(square));
	}

	static int next(long bitboard) {
		return toX88Square(numberOfTrailingZeros(bitboard));
	}

	static long remainder(long bitboard) {
		return bitboard & (bitboard - 1);
	}

	static int size(long bitboard) {
		return bitCount(bitboard);
	}

	private static int toX88Square(int square) {
		return ((square & ~7) << 1) | (square & 7);
	}

	private static int toBitSquare(int square) {
		return ((square & ~7) >>> 1) | (square & 7);
	}
}
