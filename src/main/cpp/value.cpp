/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "value.h"

#include <cmath>

namespace pulse {

bool Value::isCheckmate(int value) {
	int absvalue = std::abs(value);
	return absvalue >= CHECKMATE_THRESHOLD && absvalue <= CHECKMATE;
}

}
