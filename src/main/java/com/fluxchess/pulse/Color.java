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
