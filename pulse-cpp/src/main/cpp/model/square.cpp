// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "square.h"

namespace pulse::square {

bool isValid(int square) {
	return (square & 0x88) == 0;
}

int valueOf(int file, int rank) {
	return (rank << 4) + file;
}

int getFile(int square) {
	return square & 0xF;
}

int getRank(int square) {
	return square >> 4;
}
}
