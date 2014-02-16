/*
 * Copyright 2007-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    if (genericCastling == null) throw new IllegalArgumentException();

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
