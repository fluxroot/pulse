/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

#include "depth.h"

namespace pulse::value {

constexpr int INFINITE = 200000;
constexpr int CHECKMATE = 100000;
constexpr int CHECKMATE_THRESHOLD = CHECKMATE - depth::MAX_PLY;
constexpr int DRAW = 0;

constexpr int NOVALUE = 300000;

bool isCheckmate(int value);
}
