/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import static com.fluxchess.pulse.Depth.MAX_PLY;
import static java.lang.Math.abs;

final class Value {

  static final int INFINITE = 200000;
  static final int CHECKMATE = 100000;
  static final int CHECKMATE_THRESHOLD = CHECKMATE - MAX_PLY;
  static final int DRAW = 0;

  static final int NOVALUE = 300000;

  private Value() {
  }

  static boolean isValid(int value) {
    int absvalue = abs(value);

    return absvalue <= CHECKMATE || absvalue == INFINITE;
  }

  static boolean isCheckmate(int value) {
    assert isValid(value);

    int absvalue = abs(value);

    return absvalue >= CHECKMATE_THRESHOLD && absvalue <= CHECKMATE;
  }

}
