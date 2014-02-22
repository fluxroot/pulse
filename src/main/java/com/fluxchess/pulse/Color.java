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

import com.fluxchess.jcpi.models.GenericColor;

/**
 * This class encodes color information as an int value. The data is
 * encoded as follows:<br/>
 * <br/>
 * <code>Bit 0 - 1</code>: the color (required)<br/>
 */
public final class Color {

  public static final int MASK = 0x3;

  public static final int WHITE = 0;
  public static final int BLACK = 1;
  public static final int NOCOLOR = 2;

  public static final int[] values = {
      WHITE, BLACK
  };

  private Color() {
  }

  public static int valueOf(GenericColor genericColor) {
    if (genericColor == null) throw new IllegalArgumentException();

    switch (genericColor) {
      case WHITE:
        return WHITE;
      case BLACK:
        return BLACK;
      default:
        throw new IllegalArgumentException();
    }
  }

  public static GenericColor toGenericColor(int color) {
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

  public static boolean isValid(int color) {
    switch (color) {
      case WHITE:
      case BLACK:
        return true;
      case NOCOLOR:
        return false;
      default:
        throw new IllegalArgumentException();
    }
  }

  public static int opposite(int color) {
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
