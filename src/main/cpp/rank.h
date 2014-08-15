/*
 * Copyright (C) 2013-2014 Phokham Nonava
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

  static const int SIZE = 8;
  static const std::array<int, SIZE> values;

  static bool isValid(int rank);
  static int fromNotation(char notation);
  static char toNotation(int rank);

private:
  static const char r1_NOTATION = '1';
  static const char r2_NOTATION = '2';
  static const char r3_NOTATION = '3';
  static const char r4_NOTATION = '4';
  static const char r5_NOTATION = '5';
  static const char r6_NOTATION = '6';
  static const char r7_NOTATION = '7';
  static const char r8_NOTATION = '8';

  Rank();
  ~Rank();
};

}

#endif
