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

import com.fluxchess.jcpi.models.GenericChessman;

/**
 * This class encodes chessman information as an int value. The data is
 * encoded as follows:<br/>
 * <br/>
 * <code>Bit 0 - 2</code>: the chessman (required)<br/>
 */
public final class PieceType {

  public static final int MASK = 0x7;

  public static final int PAWN = 0;
  public static final int KNIGHT = 1;
  public static final int BISHOP = 2;
  public static final int ROOK = 3;
  public static final int QUEEN = 4;
  public static final int KING = 5;
  public static final int NOCHESSMAN = 6;

  public static final int[] values = {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
  };

  public static final int[] promotions = {
    KNIGHT, BISHOP, ROOK, QUEEN
  };

  private PieceType() {
  }

  public static int valueOf(GenericChessman genericChessman) {
    if (genericChessman == null) throw new IllegalArgumentException();

    switch (genericChessman) {
      case PAWN:
        return PAWN;
      case KNIGHT:
        return KNIGHT;
      case BISHOP:
        return BISHOP;
      case ROOK:
        return ROOK;
      case QUEEN:
        return QUEEN;
      case KING:
        return KING;
      default:
        throw new IllegalArgumentException();
    }
  }

  public static int valueOfPromotion(GenericChessman genericChessman) {
    if (genericChessman == null) throw new IllegalArgumentException();

    switch (genericChessman) {
      case KNIGHT:
        return KNIGHT;
      case BISHOP:
        return BISHOP;
      case ROOK:
        return ROOK;
      case QUEEN:
        return QUEEN;
      case PAWN:
      case KING:
      default:
        throw new IllegalArgumentException();
    }
  }

  public static GenericChessman toGenericChessman(int chessman) {
    switch (chessman) {
      case PAWN:
        return GenericChessman.PAWN;
      case KNIGHT:
        return GenericChessman.KNIGHT;
      case BISHOP:
        return GenericChessman.BISHOP;
      case ROOK:
        return GenericChessman.ROOK;
      case QUEEN:
        return GenericChessman.QUEEN;
      case KING:
        return GenericChessman.KING;
      case NOCHESSMAN:
      default:
        throw new IllegalArgumentException();
    }
  }

  public static boolean isValid(int chessman) {
    switch (chessman) {
      case PAWN:
      case KNIGHT:
      case BISHOP:
      case ROOK:
      case QUEEN:
      case KING:
        return true;
      case NOCHESSMAN:
        return false;
      default:
        throw new IllegalArgumentException();
    }
  }

  public static boolean isValidPromotion(int chessman) {
    switch (chessman) {
      case KNIGHT:
      case BISHOP:
      case ROOK:
      case QUEEN:
        return true;
      case PAWN:
      case KING:
        return false;
      case NOCHESSMAN:
      default:
        throw new IllegalArgumentException();
    }
  }

  public static boolean isSliding(int chessman) {
    switch (chessman) {
      case BISHOP:
      case ROOK:
      case QUEEN:
        return true;
      case PAWN:
      case KNIGHT:
      case KING:
        return false;
      case NOCHESSMAN:
      default:
        throw new IllegalArgumentException();
    }
  }

}
