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

import com.fluxchess.jcpi.models.GenericFile;

/**
 * This class encodes file information as an int value. The data is
 * encoded as follows:<br/>
 * <br/>
 * <code>Bit 0 - 3</code>: the file (required)<br/>
 */
final class File {

  static final int MASK = 0xF;

  static final int a = 0;
  static final int b = 1;
  static final int c = 2;
  static final int d = 3;
  static final int e = 4;
  static final int f = 5;
  static final int g = 6;
  static final int h = 7;
  static final int NOFILE = 8;

  static final int[] values = {
      a, b, c, d, e, f, g, h
  };

  private File() {
  }

  static int valueOf(GenericFile genericFile) {
    assert genericFile != null;

    switch (genericFile) {
      case Fa:
        return a;
      case Fb:
        return b;
      case Fc:
        return c;
      case Fd:
        return d;
      case Fe:
        return e;
      case Ff:
        return f;
      case Fg:
        return g;
      case Fh:
        return h;
      default:
        throw new IllegalArgumentException();
    }
  }

  static GenericFile toGenericFile(int file) {
    switch (file) {
      case a:
        return GenericFile.Fa;
      case b:
        return GenericFile.Fb;
      case c:
        return GenericFile.Fc;
      case d:
        return GenericFile.Fd;
      case e:
        return GenericFile.Fe;
      case f:
        return GenericFile.Ff;
      case g:
        return GenericFile.Fg;
      case h:
        return GenericFile.Fh;
      case NOFILE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static boolean isValid(int file) {
    switch (file) {
      case a:
      case b:
      case c:
      case d:
      case e:
      case f:
      case g:
      case h:
        return true;
      case NOFILE:
        return false;
      default:
        throw new IllegalArgumentException();
    }
  }

}
