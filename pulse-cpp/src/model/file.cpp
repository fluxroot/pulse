// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "file.h"

namespace pulse::file {

bool isValid(int file) {
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
