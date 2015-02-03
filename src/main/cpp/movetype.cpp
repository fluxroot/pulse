/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "movetype.h"

namespace pulse {

bool MoveType::isValid(int type) {
  switch (type) {
    case NORMAL:
    case PAWNDOUBLE:
    case PAWNPROMOTION:
    case ENPASSANT:
    case CASTLING:
      return true;
    default:
      return false;
  }
}

}
