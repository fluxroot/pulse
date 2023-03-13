// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.
#pragma once

#include <array>

namespace pulse::castling {

constexpr int WHITE_KINGSIDE = 1 << 0;
constexpr int WHITE_QUEENSIDE = 1 << 1;
constexpr int BLACK_KINGSIDE = 1 << 2;
constexpr int BLACK_QUEENSIDE = 1 << 3;

constexpr int NOCASTLING = 0;

constexpr int VALUES_LENGTH = 16;

int valueOf(int color, int castlingtype);

int getType(int castling);

int getColor(int castling);
}
