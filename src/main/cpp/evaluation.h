/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_EVALUATION_H
#define PULSE_EVALUATION_H

#include "board.h"

namespace pulse {

class Evaluation {
public:
  static const int TEMPO = 1;

  static int materialWeight;
  static int mobilityWeight;

  int evaluate(Board& board);

private:
  static const int MAX_WEIGHT = 100;

  int evaluateMaterial(int color, Board& board);
  int evaluateMobility(int color, Board& board);
  int evaluateMobility(int color, Board& board, int square, const std::vector<int>& moveDelta);
};

}

#endif
