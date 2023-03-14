// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.
#pragma once

#include "movegenerator.h"

namespace pulse {

class Perft final {
public:
	void run();

private:
	static const int MAX_DEPTH = 6;

	std::array<MoveGenerator, MAX_DEPTH> moveGenerators;

	uint64_t miniMax(int depth, Position& position, int ply);
};
}
