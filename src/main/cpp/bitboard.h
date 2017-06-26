/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_BITBOARD_H
#define PULSE_BITBOARD_H

#include <array>
#include <cstdint>

namespace pulse {

class Bitboard {
public:
	static uint64_t add(int square, uint64_t bitboard);

	static uint64_t remove(int square, uint64_t bitboard);

	static int next(uint64_t bitboard);

	static uint64_t remainder(uint64_t bitboard);

	static int size(uint64_t bitboard);

	static int numberOfTrailingZeros(uint64_t b);

	static int bitCount(uint64_t b);

private:
	static const uint64_t DEBRUIJN64 = 0x03F79D71B4CB0A89ULL;
	static const std::array<int, 64> lsbTable;

	static int toX88Square(int square);

	static int toBitSquare(int square);
};

}

#endif
