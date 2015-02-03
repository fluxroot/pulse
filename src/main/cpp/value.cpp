/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "value.h"

#include <cassert>
#include <cmath>

namespace pulse {

bool Value::isValid(int value) {
  int absvalue = std::abs(value);

  return absvalue <= CHECKMATE || absvalue == INFINITE;
}

bool Value::isCheckmate(int value) {
  assert(isValid(value));

  int absvalue = std::abs(value);

  return absvalue >= CHECKMATE_THRESHOLD && absvalue <= CHECKMATE;
}

}
