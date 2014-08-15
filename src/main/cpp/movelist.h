/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_MOVELIST_H
#define PULSE_MOVELIST_H

#include "depth.h"
#include "value.h"
#include "move.h"

#include <array>
#include <memory>

namespace pulse {

/**
 * This class stores our moves for a specific position. For the root node we
 * will populate pv for every root move.
 */
class MoveList {
private:
  static const int MAX_MOVES = 256;

public:
  class MoveVariation {
  public:
    std::array<int, Depth::MAX_PLY> moves;
    int size = 0;
  };

  class Entry {
  public:
    int move = Move::NOMOVE;
    int value = Value::NOVALUE;
    MoveVariation pv;
  };

  std::array<std::shared_ptr<Entry>, MAX_MOVES> entries;
  int size = 0;

  MoveList();

  void sort();
  void rateFromMVVLVA();
};

}

#endif
