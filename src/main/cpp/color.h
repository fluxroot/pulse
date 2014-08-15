/*
 * Copyright (C) 2013-2014 Phokham Nonava
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

  static const int SIZE = 2;
  static const std::array<int, SIZE> values;

  static bool isValid(int color);
  static int fromNotation(char notation);
  static char toNotation(int color);
  static int colorOf(char notation);
  static char transform(char notation, int color);
  static int opposite(int color);

private:
  static const char WHITE_NOTATION = 'w';
  static const char BLACK_NOTATION = 'b';

  Color();
  ~Color();
};

}

#endif
