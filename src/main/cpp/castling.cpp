/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "castling.h"
#include "color.h"
#include "castlingtype.h"

#include <cassert>

namespace pulse {

const std::array<int, Castling::SIZE> Castling::values = {
  WHITE_KINGSIDE, WHITE_QUEENSIDE,
  BLACK_KINGSIDE, BLACK_QUEENSIDE
};

bool Castling::isValid(int castling) {
  switch (castling) {
    case WHITE_KINGSIDE:
    case WHITE_QUEENSIDE:
    case BLACK_KINGSIDE:
    case BLACK_QUEENSIDE:
      return true;
    case NOCASTLING:
    default:
      return false;
  }
}

int Castling::fromNotation(char notation) {
  int color = Color::colorOf(notation);
  int castlingtype = CastlingType::fromNotation(notation);

  if (castlingtype != CastlingType::NOCASTLINGTYPE) {
    return valueOf(color, castlingtype);
  } else {
    return NOCASTLING;
  }
}

char Castling::toNotation(int castling) {
  assert(isValid(castling));

  return CastlingType::toNotation(getType(castling), getColor(castling));
}

int Castling::valueOf(int color, int castlingType) {
  switch (color) {
    case Color::WHITE:
      switch (castlingType) {
        case CastlingType::KINGSIDE:
          return WHITE_KINGSIDE;
        case CastlingType::QUEENSIDE:
          return WHITE_QUEENSIDE;
        case CastlingType::NOCASTLINGTYPE:
        default:
          throw std::exception();
      }
    case Color::BLACK:
      switch (castlingType) {
        case CastlingType::KINGSIDE:
          return BLACK_KINGSIDE;
        case CastlingType::QUEENSIDE:
          return BLACK_QUEENSIDE;
        case CastlingType::NOCASTLINGTYPE:
        default:
          throw std::exception();
      }
    case Color::NOCOLOR:
    default:
      throw std::exception();
  }
}

int Castling::getType(int castling) {
  switch (castling) {
    case WHITE_KINGSIDE:
    case BLACK_KINGSIDE:
      return CastlingType::KINGSIDE;
    case WHITE_QUEENSIDE:
    case BLACK_QUEENSIDE:
      return CastlingType::QUEENSIDE;
    case NOCASTLING:
    default:
      throw std::exception();
  }
}

int Castling::getColor(int castling) {
  switch (castling) {
    case WHITE_KINGSIDE:
    case WHITE_QUEENSIDE:
      return Color::WHITE;
    case BLACK_KINGSIDE:
    case BLACK_QUEENSIDE:
      return Color::BLACK;
    case NOCASTLING:
    default:
      throw std::exception();
  }
}

}
