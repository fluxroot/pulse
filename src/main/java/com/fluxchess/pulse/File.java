/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericFile;

final class File {

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
      default:
        return false;
    }
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

}
