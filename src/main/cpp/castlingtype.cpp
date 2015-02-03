/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "castlingtype.h"

namespace pulse {

const std::array<int, CastlingType::VALUES_SIZE> CastlingType::values = {
  KINGSIDE, QUEENSIDE
};

}
