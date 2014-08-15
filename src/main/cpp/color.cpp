/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "color.h"

#include <cctype>

namespace pulse {

const std::array<int, Color::SIZE> Color::values = {
  WHITE, BLACK
};

bool Color::isValid(int color) {
  switch (color) {
    case WHITE:
    case BLACK:
      return true;
    case NOCOLOR:
    default:
      return false;
  }
}

int Color::fromNotation(char notation) {
  char lowercaseNotation = std::tolower(notation);
  switch (lowercaseNotation) {
    case WHITE_NOTATION:
      return WHITE;
    case BLACK_NOTATION:
      return BLACK;
    default:
      return NOCOLOR;
  }
}

char Color::toNotation(int color) {
  switch (color) {
    case WHITE:
      return WHITE_NOTATION;
    case BLACK:
      return BLACK_NOTATION;
    case NOCOLOR:
    default:
      throw std::exception();
  }
}

int Color::colorOf(char notation) {
  if (std::islower(notation)) {
    return BLACK;
  } else {
    return WHITE;
  }
}

char Color::transform(char notation, int color) {
  switch (color) {
    case WHITE:
      return std::toupper(notation);
    case BLACK:
      return std::tolower(notation);
    case NOCOLOR:
    default:
      throw std::exception();
  }
}

int Color::opposite(int color) {
  switch (color) {
    case WHITE:
      return BLACK;
    case BLACK:
      return WHITE;
    case NOCOLOR:
    default:
      throw std::exception();
  }
}

}
