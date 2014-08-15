/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "rank.h"

namespace pulse {

const std::array<int, Rank::SIZE> Rank::values = {
  r1, r2, r3, r4, r5, r6, r7, r8
};

bool Rank::isValid(int rank) {
  switch (rank) {
    case r1:
    case r2:
    case r3:
    case r4:
    case r5:
    case r6:
    case r7:
    case r8:
      return true;
    case NORANK:
    default:
      return false;
  }
}

int Rank::fromNotation(char notation) {
  switch (notation) {
    case r1_NOTATION:
      return r1;
    case r2_NOTATION:
      return r2;
    case r3_NOTATION:
      return r3;
    case r4_NOTATION:
      return r4;
    case r5_NOTATION:
      return r5;
    case r6_NOTATION:
      return r6;
    case r7_NOTATION:
      return r7;
    case r8_NOTATION:
      return r8;
    default:
      return NORANK;
  }
}

char Rank::toNotation(int rank) {
  switch (rank) {
    case r1:
      return r1_NOTATION;
    case r2:
      return r2_NOTATION;
    case r3:
      return r3_NOTATION;
    case r4:
      return r4_NOTATION;
    case r5:
      return r5_NOTATION;
    case r6:
      return r6_NOTATION;
    case r7:
      return r7_NOTATION;
    case r8:
      return r8_NOTATION;
    case NORANK:
    default:
      throw std::exception();
  }
}

}
