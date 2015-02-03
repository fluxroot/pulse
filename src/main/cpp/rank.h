/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_RANK_H
#define PULSE_RANK_H

#include <array>

namespace pulse {

class Rank {
public:
  static const int r1 = 0;
  static const int r2 = 1;
  static const int r3 = 2;
  static const int r4 = 3;
  static const int r5 = 4;
  static const int r6 = 5;
  static const int r7 = 6;
  static const int r8 = 7;

  static const int NORANK = 8;

  static const int VALUES_SIZE = 8;
  static const std::array<int, VALUES_SIZE> values;

  static bool isValid(int rank);

private:
  Rank();
  ~Rank();
};

}

#endif
