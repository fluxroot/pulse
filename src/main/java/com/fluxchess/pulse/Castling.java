/*
 * Copyright 2013-2014 the original author or authors.
 *
 * This file is part of Pulse Chess.
 *
 * Pulse Chess is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pulse Chess is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pulse Chess.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericCastling;

/**
 * This class encodes castling information as an int value. The data is
 * encoded as follows:<br/>
 * <br/>
 * <code>Bit 0 - 1</code>: the castling (required)<br/>
 */
public final class Castling {

  public static final int MASK = 0x3;

  public static final int KINGSIDE = 0;
  public static final int QUEENSIDE = 1;
  public static final int NOCASTLING = 2;

  public static final int[] values = {
      KINGSIDE, QUEENSIDE
  };

  private Castling() {
  }

  public static int valueOf(GenericCastling genericCastling) {
    assert genericCastling != null;

    switch (genericCastling) {
      case KINGSIDE:
        return KINGSIDE;
      case QUEENSIDE:
        return QUEENSIDE;
      default:
        throw new IllegalArgumentException();
    }
  }

  public static GenericCastling toGenericCastling(int castling) {
    switch (castling) {
      case KINGSIDE:
        return GenericCastling.KINGSIDE;
      case QUEENSIDE:
        return GenericCastling.QUEENSIDE;
      case NOCASTLING:
      default:
        throw new IllegalArgumentException();
    }
  }

  public static boolean isValid(int castling) {
    switch (castling) {
      case KINGSIDE:
      case QUEENSIDE:
        return true;
      case NOCASTLING:
        return false;
      default:
        throw new IllegalArgumentException();
    }
  }

}
