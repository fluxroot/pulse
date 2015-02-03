/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "file.h"

#include <cctype>

namespace pulse {

const std::array<int, File::VALUES_SIZE> File::values = {
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
    default:
      return false;
  }
}

}
