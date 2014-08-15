/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericCastling;

final class CastlingType {

  static final int KINGSIDE = 0;
  static final int QUEENSIDE = 1;
  static final int NOCASTLINGTYPE = 2;

  static final int[] values = {
      KINGSIDE, QUEENSIDE
  };

  private CastlingType() {
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
