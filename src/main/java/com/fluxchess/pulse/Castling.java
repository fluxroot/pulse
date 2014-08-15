/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

final class Castling {

  static final int WHITE_KINGSIDE = 0;
  static final int WHITE_QUEENSIDE = 1;
  static final int BLACK_KINGSIDE = 2;
  static final int BLACK_QUEENSIDE = 3;
  static final int NOCASTLING = 4;

  static final int[] values = {
      WHITE_KINGSIDE, WHITE_QUEENSIDE,
      BLACK_KINGSIDE, BLACK_QUEENSIDE
  };

  private Castling() {
  }

  static boolean isValid(int castling) {
    switch (castling) {
      case WHITE_KINGSIDE:
      case WHITE_QUEENSIDE:
      case BLACK_KINGSIDE:
      case BLACK_QUEENSIDE:
        return true;
      case NOCASTLING:
      default:
        return false;
    }
  }

  static int valueOf(int color, int castlingType) {
    switch (color) {
      case Color.WHITE:
        switch (castlingType) {
          case CastlingType.KINGSIDE:
            return WHITE_KINGSIDE;
          case CastlingType.QUEENSIDE:
            return WHITE_QUEENSIDE;
          case CastlingType.NOCASTLINGTYPE:
          default:
            throw new IllegalArgumentException();
        }
      case Color.BLACK:
        switch (castlingType) {
          case CastlingType.KINGSIDE:
            return BLACK_KINGSIDE;
          case CastlingType.QUEENSIDE:
            return BLACK_QUEENSIDE;
          case CastlingType.NOCASTLINGTYPE:
          default:
            throw new IllegalArgumentException();
        }
      case Color.NOCOLOR:
      default:
        throw new IllegalArgumentException();
    }
  }

}
