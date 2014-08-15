/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "file.h"

#include <cctype>

namespace pulse {

const std::array<int, File::SIZE> File::values = {
  a, b, c, d, e, f, g, h
};

bool File::isValid(int file) {
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

int File::fromNotation(char notation) {
  char lowercaseNotation = std::tolower(notation);
  switch (lowercaseNotation) {
    case a_NOTATION:
      return a;
    case b_NOTATION:
      return b;
    case c_NOTATION:
      return c;
    case d_NOTATION:
      return d;
    case e_NOTATION:
      return e;
    case f_NOTATION:
      return f;
    case g_NOTATION:
      return g;
    case h_NOTATION:
      return h;
    default:
      return NOFILE;
  }
}

char File::toNotation(int file) {
  switch (file) {
    case a:
      return a_NOTATION;
    case b:
      return b_NOTATION;
    case c:
      return c_NOTATION;
    case d:
      return d_NOTATION;
    case e:
      return e_NOTATION;
    case f:
      return f_NOTATION;
    case g:
      return g_NOTATION;
    case h:
      return h_NOTATION;
    case NOFILE:
    default:
      throw std::exception();
  }
}

}
