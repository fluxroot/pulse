/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

#include <array>

namespace pulse::castlingtype {

constexpr int KINGSIDE = 0;
constexpr int QUEENSIDE = 1;

constexpr int NOCASTLINGTYPE = 2;

constexpr int VALUES_SIZE = 2;
constexpr std::array<int, VALUES_SIZE> values = {
		KINGSIDE, QUEENSIDE
};
}
