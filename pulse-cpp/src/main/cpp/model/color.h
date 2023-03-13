// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.
#pragma once

#include <array>

namespace pulse::color {

constexpr int WHITE = 0;
constexpr int BLACK = 1;

constexpr int NOCOLOR = 2;

constexpr int VALUES_SIZE = 2;
constexpr std::array<int, VALUES_SIZE> values = {
		WHITE, BLACK
};

int opposite(int color);
}
