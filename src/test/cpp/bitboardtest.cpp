/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "bitboard.h"
#include "model/square.h"

#include "gtest/gtest.h"

#include <random>
#include <list>

using namespace pulse;

class BitboardTest : public ::testing::Test {
protected:
	std::list<int> pool;

	virtual void SetUp() {
		std::default_random_engine generator;

		while (pool.size() < 64) {
			std::uniform_int_distribution<int> distribution(0, 63);
			int value = distribution(generator);
			if (std::find(pool.begin(), pool.end(), square::values[value]) == pool.end()) {
				pool.push_back(square::values[value]);
			}
		}
	}
};

TEST_F(BitboardTest, shouldAddAllSquaresCorrectly) {
	uint64_t bitboard = 0;

	for (auto x88square: pool) {
		bitboard = bitboard::add(x88square, bitboard);
	}

	EXPECT_EQ(bitboard, std::numeric_limits<uint64_t>::max());
}

TEST_F(BitboardTest, shouldRemoveAllSquaresCorrectly) {
	uint64_t bitboard = std::numeric_limits<uint64_t>::max();

	for (auto x88square: pool) {
		bitboard = bitboard::remove(x88square, bitboard);
	}

	EXPECT_EQ(bitboard, 0);
}

TEST(bitboardtest, shouldReturnTheNextSquare) {
	uint64_t bitboard = bitboard::add(square::a6, 0);

	int square = bitboard::next(bitboard);

	EXPECT_EQ(square, +square::a6);
}

TEST(bitboardtest, shouldReturnCorrectRemainder) {
	uint64_t bitboard = 0b1110100;

	uint64_t remainder = bitboard::remainder(bitboard);

	EXPECT_EQ(remainder, 0b1110000);
}

TEST(bitboardtest, shouldReturnCorrectSize) {
	uint64_t bitboard = 0b111;

	int size = bitboard::size(bitboard);

	EXPECT_EQ(size, 3);
}

TEST(bitboardtest, testNumberOfTrailingZeros) {
	uint64_t bitboard = 0;
	int i = 0;

	for (auto square: square::values) {
		bitboard = bitboard::add(square, bitboard);

		EXPECT_EQ(i, bitboard::numberOfTrailingZeros(bitboard));

		bitboard = bitboard::remove(square, bitboard);
		i++;
	}
}

TEST(bitboardtest, testBitCount) {
	std::default_random_engine generator;

	for (int i = 0; i < 1000; i++) {
		uint64_t bitboard = 0;
		int count = 0;

		int index = 0;
		while (true) {
			std::uniform_int_distribution<int> distribution(1, 4);
			index += distribution(generator);
			if (index < 64) {
				bitboard |= 1ULL << index;
				count++;
			} else {
				break;
			}
		}

		EXPECT_EQ(count, bitboard::bitCount(bitboard));
	}
}
