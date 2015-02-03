/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

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
      default:
        return false;
    }
  }

}
