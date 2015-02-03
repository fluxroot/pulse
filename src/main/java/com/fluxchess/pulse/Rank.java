/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

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
      default:
        return false;
    }
  }

}
