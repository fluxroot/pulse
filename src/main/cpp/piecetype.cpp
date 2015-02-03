/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "piecetype.h"
#include "color.h"

#include <cassert>

namespace pulse {

const std::array<int, PieceType::VALUES_SIZE> PieceType::values = {
  PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
};

bool PieceType::isValid(int piecetype) {
  switch (piecetype) {
    case PAWN:
    case KNIGHT:
    case BISHOP:
    case ROOK:
    case QUEEN:
    case KING:
      return true;
    default:
      return false;
  }
}

bool PieceType::isValidPromotion(int piecetype) {
  switch (piecetype) {
    case KNIGHT:
    case BISHOP:
    case ROOK:
    case QUEEN:
      return true;
    default:
      return false;
  }
}

bool PieceType::isSliding(int piecetype) {
  switch (piecetype) {
    case BISHOP:
    case ROOK:
    case QUEEN:
      return true;
    case PAWN:
    case KNIGHT:
    case KING:
      return false;
    default:
      throw std::exception();
  }
}

int PieceType::getValue(int piecetype) {
  switch (piecetype) {
    case PAWN:
      return PAWN_VALUE;
    case KNIGHT:
      return KNIGHT_VALUE;
    case BISHOP:
      return BISHOP_VALUE;
    case ROOK:
      return ROOK_VALUE;
    case QUEEN:
      return QUEEN_VALUE;
    case KING:
      return KING_VALUE;
    default:
      throw std::exception();
  }
}

}
