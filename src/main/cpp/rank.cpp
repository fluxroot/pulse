/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "rank.h"

namespace pulse::rank {

bool isValid(int rank) {
	switch (rank) {
		case r1:
		case r2:
		case r3:
		case r4:
		case r5:
		case r6:
		case r7:
		case r8:
			return true;
		default:
			return false;
	}
}
}
