/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

#include <array>
#include <cstdint>

namespace pulse::bitboard {

uint64_t add(int square, uint64_t bitboard);

uint64_t remove(int square, uint64_t bitboard);

int next(uint64_t bitboard);

uint64_t remainder(uint64_t bitboard);

int size(uint64_t bitboard);

int numberOfTrailingZeros(uint64_t b);

int bitCount(uint64_t b);
}
