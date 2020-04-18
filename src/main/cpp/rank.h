/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

#include <array>

namespace pulse::rank {

constexpr int r1 = 0;
constexpr int r2 = 1;
constexpr int r3 = 2;
constexpr int r4 = 3;
constexpr int r5 = 4;
constexpr int r6 = 5;
constexpr int r7 = 6;
constexpr int r8 = 7;

constexpr int NORANK = 8;

constexpr int VALUES_SIZE = 8;
constexpr std::array<int, VALUES_SIZE> values = {
		r1, r2, r3, r4, r5, r6, r7, r8
};

bool isValid(int rank);
}
