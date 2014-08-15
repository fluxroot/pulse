/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "castlingtype.h"
#include "color.h"

#include <cassert>
#include <cctype>

namespace pulse {

const std::array<int, CastlingType::SIZE> CastlingType::values = {
  KINGSIDE, QUEENSIDE
};

int CastlingType::fromNotation(char notation) {
  char uppercaseNotation = std::toupper(notation);
  switch (uppercaseNotation) {
    case KINGSIDE_NOTATION:
      return KINGSIDE;
    case QUEENSIDE_NOTATION:
      return QUEENSIDE;
    default:
      return NOCASTLINGTYPE;
  }
}

char CastlingType::toNotation(int castlingtype, int color) {
  assert(Color::isValid(color));

  switch (castlingtype) {
    case KINGSIDE:
      return Color::transform(KINGSIDE_NOTATION, color);
    case QUEENSIDE:
      return Color::transform(QUEENSIDE_NOTATION, color);
    case NOCASTLINGTYPE:
    default:
      throw std::exception();
  }
}

}
