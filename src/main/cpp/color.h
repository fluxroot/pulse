/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

#include <array>

namespace pulse {

class Color {
public:
	static const int WHITE = 0;
	static const int BLACK = 1;

	static const int NOCOLOR = 2;

	static const int VALUES_SIZE = 2;
	static const std::array<int, VALUES_SIZE> values;

	static int opposite(int color);

private:
	Color();

	~Color();
};

}
