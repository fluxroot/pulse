// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.
#pragma once

namespace pulse::movetype {

constexpr int MASK = 0x7;

constexpr int NORMAL = 0;
constexpr int PAWNDOUBLE = 1;
constexpr int PAWNPROMOTION = 2;
constexpr int ENPASSANT = 3;
constexpr int CASTLING = 4;

constexpr int NOMOVETYPE = 5;
}
