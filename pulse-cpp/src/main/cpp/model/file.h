// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.
#pragma once

#include <array>

namespace pulse::file {

constexpr int a = 0;
constexpr int b = 1;
constexpr int c = 2;
constexpr int d = 3;
constexpr int e = 4;
constexpr int f = 5;
constexpr int g = 6;
constexpr int h = 7;

constexpr int NOFILE = 8;

constexpr int VALUES_SIZE = 8;
constexpr std::array<int, VALUES_SIZE> values = {
		a, b, c, d, e, f, g, h
};

bool isValid(int file);
}
