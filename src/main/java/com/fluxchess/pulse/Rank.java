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

import com.fluxchess.jcpi.models.GenericRank;

/**
 * This class encodes rank information as an int value. The data is
 * encoded as follows:<br/>
 * <br/>
 * <code>Bit 0 - 3</code>: the rank (required)<br/>
 */
public final class Rank {

  public static final int MASK = 0xF;

  public static final int R1 = 0;
  public static final int R2 = 1;
  public static final int R3 = 2;
  public static final int R4 = 3;
  public static final int R5 = 4;
  public static final int R6 = 5;
  public static final int R7 = 6;
  public static final int R8 = 7;
  public static final int NORANK = 8;

  public static final int[] values = {
    R1, R2, R3, R4, R5, R6, R7, R8
  };

  private Rank() {
  }

  public static int valueOf(GenericRank genericRank) {
    if (genericRank == null) throw new IllegalArgumentException();

    switch (genericRank) {
      case R1:
        return R1;
      case R2:
        return R2;
      case R3:
        return R3;
      case R4:
        return R4;
      case R5:
        return R5;
      case R6:
        return R6;
      case R7:
        return R7;
      case R8:
        return R8;
      default:
        throw new IllegalArgumentException();
    }
  }

  public static GenericRank toGenericRank(int rank) {
    switch (rank) {
      case R1:
        return GenericRank.R1;
      case R2:
        return GenericRank.R2;
      case R3:
        return GenericRank.R3;
      case R4:
        return GenericRank.R4;
      case R5:
        return GenericRank.R5;
      case R6:
        return GenericRank.R6;
      case R7:
        return GenericRank.R7;
      case R8:
        return GenericRank.R8;
      case NORANK:
      default:
        throw new IllegalArgumentException();
    }
  }

  public static boolean isValid(int rank) {
    switch (rank) {
      case R1:
      case R2:
      case R3:
      case R4:
      case R5:
      case R6:
      case R7:
      case R8:
        return true;
      case NORANK:
        return false;
      default:
        throw new IllegalArgumentException();
    }
  }

}
