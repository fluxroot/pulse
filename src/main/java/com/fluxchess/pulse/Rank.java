/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericRank;

final class Rank {

  static final int r1 = 0;
  static final int r2 = 1;
  static final int r3 = 2;
  static final int r4 = 3;
  static final int r5 = 4;
  static final int r6 = 5;
  static final int r7 = 6;
  static final int r8 = 7;
  static final int NORANK = 8;

  static final int[] values = {
      r1, r2, r3, r4, r5, r6, r7, r8
  };

  private Rank() {
  }

  static boolean isValid(int rank) {
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

  static int valueOf(GenericRank genericRank) {
    assert genericRank != null;

    switch (genericRank) {
      case R1:
        return r1;
      case R2:
        return r2;
      case R3:
        return r3;
      case R4:
        return r4;
      case R5:
        return r5;
      case R6:
        return r6;
      case R7:
        return r7;
      case R8:
        return r8;
      default:
        throw new IllegalArgumentException();
    }
  }

  static GenericRank toGenericRank(int rank) {
    switch (rank) {
      case r1:
        return GenericRank.R1;
      case r2:
        return GenericRank.R2;
      case r3:
        return GenericRank.R3;
      case r4:
        return GenericRank.R4;
      case r5:
        return GenericRank.R5;
      case r6:
        return GenericRank.R6;
      case r7:
        return GenericRank.R7;
      case r8:
        return GenericRank.R8;
      case NORANK:
      default:
        throw new IllegalArgumentException();
    }
  }

}
