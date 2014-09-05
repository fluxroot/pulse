/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

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
      case Color.WHITE:
        switch (castlingtype) {
          case CastlingType.KINGSIDE:
            return WHITE_KINGSIDE;
          case CastlingType.QUEENSIDE:
            return WHITE_QUEENSIDE;
          default:
            throw new IllegalArgumentException();
        }
      case Color.BLACK:
        switch (castlingtype) {
          case CastlingType.KINGSIDE:
            return BLACK_KINGSIDE;
          case CastlingType.QUEENSIDE:
            return BLACK_QUEENSIDE;
          default:
            throw new IllegalArgumentException();
        }
      default:
        throw new IllegalArgumentException();
    }
  }

}
