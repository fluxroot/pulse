/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import static com.fluxchess.pulse.CastlingType.KINGSIDE;
import static com.fluxchess.pulse.CastlingType.QUEENSIDE;
import static com.fluxchess.pulse.Color.BLACK;
import static com.fluxchess.pulse.Color.WHITE;

final class Castling {

  static final int WHITE_KINGSIDE = 1 << 0;
  static final int WHITE_QUEENSIDE = 1 << 1;
  static final int BLACK_KINGSIDE = 1 << 2;
  static final int BLACK_QUEENSIDE = 1 << 3;

  static final int NOCASTLING = 0;

  static final int VALUES_LENGTH = 16;

  private Castling() {
  }

  static boolean isValid(int castling) {
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

  static int valueOf(int color, int castlingtype) {
    switch (color) {
      case WHITE:
        switch (castlingtype) {
          case KINGSIDE:
            return WHITE_KINGSIDE;
          case QUEENSIDE:
            return WHITE_QUEENSIDE;
          default:
            throw new IllegalArgumentException();
        }
      case BLACK:
        switch (castlingtype) {
          case KINGSIDE:
            return BLACK_KINGSIDE;
          case QUEENSIDE:
            return BLACK_QUEENSIDE;
          default:
            throw new IllegalArgumentException();
        }
      default:
        throw new IllegalArgumentException();
    }
  }

}
