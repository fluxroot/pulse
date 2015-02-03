/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_MOVELIST_H
#define PULSE_MOVELIST_H

#include "value.h"
#include "move.h"

#include <array>
#include <memory>

namespace pulse {

/**
 * This class stores our moves for a specific position. For the root node we
 * will populate pv for every root move.
 */
template<class T>
class MoveList {
private:
  static const int MAX_MOVES = 256;

public:
  std::array<std::shared_ptr<T>, MAX_MOVES> entries;
  int size = 0;

  MoveList();

  void sort();
  void rateFromMVVLVA();
};

class MoveVariation {
public:
  std::array<int, Depth::MAX_PLY> moves;
  int size = 0;
};

class MoveEntry {
public:
  int move = Move::NOMOVE;
  int value = Value::NOVALUE;
};

class RootEntry : public MoveEntry {
public:
  MoveVariation pv;
};

}

#endif
