// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "bitboard.h"

namespace pulse::bitboard {
namespace {
constexpr uint64_t DEBRUIJN64 = 0x03F79D71B4CB0A89ULL;
constexpr std::array<int, 64> lsbTable = {
		0, 47, 1, 56, 48, 27, 2, 60,
		57, 49, 41, 37, 28, 16, 3, 61,
		54, 58, 35, 52, 50, 42, 21, 44,
		38, 32, 29, 23, 17, 11, 4, 62,
		46, 55, 26, 59, 40, 36, 15, 53,
		34, 51, 20, 43, 31, 22, 10, 45,
		25, 39, 14, 33, 19, 30, 9, 24,
		13, 18, 8, 12, 7, 6, 5, 63
};

int toX88Square(int square) {
	return ((square & ~7) << 1) | (square & 7);
}

int toBitSquare(int square) {
	return ((square & ~7) >> 1) | (square & 7);
}
}

uint64_t add(int square, uint64_t bitboard) {
	return bitboard | 1ULL << toBitSquare(square);
}

uint64_t remove(int square, uint64_t bitboard) {
	return bitboard & ~(1ULL << toBitSquare(square));
}

int next(uint64_t bitboard) {
	return toX88Square(numberOfTrailingZeros(bitboard));
}

uint64_t remainder(uint64_t bitboard) {
	return bitboard & (bitboard - 1);
}

int size(uint64_t bitboard) {
	return bitCount(bitboard);
}

int numberOfTrailingZeros(uint64_t b) {
	return lsbTable[((b ^ (b - 1)) * DEBRUIJN64) >> 58];
}

int bitCount(uint64_t b) {
	b = b - ((b >> 1) & 0x5555555555555555ULL);
	b = (b & 0x3333333333333333ULL) + ((b >> 2) & 0x3333333333333333ULL);
	b = (b + (b >> 4)) & 0x0F0F0F0F0F0F0F0FULL;
	return (b * 0x0101010101010101ULL) >> 56;
}
}
