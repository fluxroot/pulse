// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include <exception>
#include "color.h"

namespace pulse::color {

int opposite(int color) {
	switch (color) {
		case WHITE:
			return BLACK;
		case BLACK:
			return WHITE;
		default:
			throw std::exception();
	}
}
}
