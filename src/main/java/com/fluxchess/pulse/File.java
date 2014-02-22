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
public final class File {

  public static final int MASK = 0xF;

  public static final int Fa = 0;
  public static final int Fb = 1;
  public static final int Fc = 2;
  public static final int Fd = 3;
  public static final int Fe = 4;
  public static final int Ff = 5;
  public static final int Fg = 6;
  public static final int Fh = 7;
  public static final int NOFILE = 8;

  public static final int[] values = {
    Fa, Fb, Fc, Fd, Fe, Ff, Fg, Fh
  };

  private File() {
  }

  public static int valueOf(GenericFile genericFile) {
    if (genericFile == null) throw new IllegalArgumentException();

    switch (genericFile) {
      case Fa:
        return Fa;
      case Fb:
        return Fb;
      case Fc:
        return Fc;
      case Fd:
        return Fd;
      case Fe:
        return Fe;
      case Ff:
        return Ff;
      case Fg:
        return Fg;
      case Fh:
        return Fh;
      default:
        throw new IllegalArgumentException();
    }
  }

  public static GenericFile toGenericFile(int file) {
    switch (file) {
      case Fa:
        return GenericFile.Fa;
      case Fb:
        return GenericFile.Fb;
      case Fc:
        return GenericFile.Fc;
      case Fd:
        return GenericFile.Fd;
      case Fe:
        return GenericFile.Fe;
      case Ff:
        return GenericFile.Ff;
      case Fg:
        return GenericFile.Fg;
      case Fh:
        return GenericFile.Fh;
      case NOFILE:
      default:
        throw new IllegalArgumentException();
    }
  }

  public static boolean isValid(int file) {
    switch (file) {
      case Fa:
      case Fb:
      case Fc:
      case Fd:
      case Fe:
      case Ff:
      case Fg:
      case Fh:
        return true;
      case NOFILE:
        return false;
      default:
        throw new IllegalArgumentException();
    }
  }

}
