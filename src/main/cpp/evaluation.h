/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

#include "position.h"

namespace pulse {

class Evaluation {
public:
	static const int TEMPO = 1;

	static int materialWeight;
	static int mobilityWeight;

	int evaluate(Position& position);

private:
	static const int MAX_WEIGHT = 100;

	int evaluateMaterial(int color, Position& position);

	int evaluateMobility(int color, Position& position);

	int evaluateMobility(int color, Position& position, int square, const std::vector<int>& directions);
};

}
