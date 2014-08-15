/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericColor;

final class Color {

  static final int WHITE = 0;
  static final int BLACK = 1;
  static final int NOCOLOR = 2;

  static final int[] values = {
      WHITE, BLACK
  };

  private Color() {
  }

  static boolean isValid(int color) {
    switch (color) {
      case WHITE:
      case BLACK:
        return true;
      case NOCOLOR:
      default:
        return false;
    }
  }

  static int valueOf(GenericColor genericColor) {
    assert genericColor != null;

    switch (genericColor) {
      case WHITE:
        return WHITE;
      case BLACK:
        return BLACK;
      default:
        throw new IllegalArgumentException();
    }
  }

  static GenericColor toGenericColor(int color) {
    switch (color) {
      case WHITE:
        return GenericColor.WHITE;
      case BLACK:
        return GenericColor.BLACK;
      case NOCOLOR:
      default:
        throw new IllegalArgumentException();
    }
  }

  static int opposite(int color) {
    switch (color) {
      case WHITE:
        return BLACK;
      case BLACK:
        return WHITE;
      case NOCOLOR:
      default:
        throw new IllegalArgumentException();
    }
  }

}
