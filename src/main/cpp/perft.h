/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_PERFT_H
#define PULSE_PERFT_H

#include "movegenerator.h"

namespace pulse {

class Perft {
public:
  void run();

private:
  static const int MAX_DEPTH = 6;
  
  std::array<MoveGenerator, MAX_DEPTH> moveGenerators;

  uint64_t miniMax(int depth, Position& position, int ply);
};

}

#endif
