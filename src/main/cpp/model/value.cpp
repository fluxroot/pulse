// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "value.h"

#include <cstdlib>

namespace pulse::value {

bool isCheckmate(int value) {
	int absvalue = std::abs(value);
	return absvalue >= CHECKMATE_THRESHOLD && absvalue <= CHECKMATE;
}
}
