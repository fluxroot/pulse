/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericCastling;

final class Castling {

  static final class Type {
    static final int KINGSIDE = 0;
    static final int QUEENSIDE = 1;
    static final int NOCASTLINGTYPE = 2;

    static final int[] values = {
        KINGSIDE, QUEENSIDE
    };

    private Type() {
    }

    static GenericCastling toGenericCastling(int castlingType) {
      switch (castlingType) {
        case KINGSIDE:
          return GenericCastling.KINGSIDE;
        case QUEENSIDE:
          return GenericCastling.QUEENSIDE;
        case NOCASTLINGTYPE:
        default:
          throw new IllegalArgumentException();
      }
    }
  }

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

  static int valueOf(int color, int castlingType) {
    switch (color) {
      case Color.WHITE:
        switch (castlingType) {
          case Type.KINGSIDE:
            return WHITE_KINGSIDE;
          case Type.QUEENSIDE:
            return WHITE_QUEENSIDE;
          case Type.NOCASTLINGTYPE:
          default:
            throw new IllegalArgumentException();
        }
      case Color.BLACK:
        switch (castlingType) {
          case Type.KINGSIDE:
            return BLACK_KINGSIDE;
          case Type.QUEENSIDE:
            return BLACK_QUEENSIDE;
          case Type.NOCASTLINGTYPE:
          default:
            throw new IllegalArgumentException();
        }
      case Color.NOCOLOR:
      default:
        throw new IllegalArgumentException();
    }
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

}
