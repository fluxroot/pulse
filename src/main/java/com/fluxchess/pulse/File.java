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
