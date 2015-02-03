/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "castling.h"
#include "color.h"
#include "castlingtype.h"

#include <cassert>

namespace pulse {

bool Castling::isValid(int castling) {
  switch (castling) {
    case WHITE_KINGSIDE:
    case WHITE_QUEENSIDE:
    case BLACK_KINGSIDE:
    case BLACK_QUEENSIDE:
      return true;
    default:
      return false;
  }
}

int Castling::valueOf(int color, int castlingtype) {
  switch (color) {
    case Color::WHITE:
      switch (castlingtype) {
        case CastlingType::KINGSIDE:
          return WHITE_KINGSIDE;
        case CastlingType::QUEENSIDE:
          return WHITE_QUEENSIDE;
        default:
          throw std::exception();
      }
    case Color::BLACK:
      switch (castlingtype) {
        case CastlingType::KINGSIDE:
          return BLACK_KINGSIDE;
        case CastlingType::QUEENSIDE:
          return BLACK_QUEENSIDE;
        default:
          throw std::exception();
      }
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
    default:
      throw std::exception();
  }
}

}
