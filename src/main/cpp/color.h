/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_COLOR_H
#define PULSE_COLOR_H

#include <array>

namespace pulse {

class Color {
public:
  static const int WHITE = 0;
  static const int BLACK = 1;

  static const int NOCOLOR = 2;

  static const int VALUES_SIZE = 2;
  static const std::array<int, VALUES_SIZE> values;

  static bool isValid(int color);
  static int opposite(int color);

private:
  Color();
  ~Color();
};

}

#endif
