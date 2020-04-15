/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

#include "depth.h"

namespace pulse {

class Value {
public:
	static const int INFINITE = 200000;
	static const int CHECKMATE = 100000;
	static const int CHECKMATE_THRESHOLD = CHECKMATE - Depth::MAX_PLY;
	static const int DRAW = 0;

	static const int NOVALUE = 300000;

	static bool isCheckmate(int value);

private:
	Value();

	~Value();
};

}
