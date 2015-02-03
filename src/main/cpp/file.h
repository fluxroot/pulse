/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_FILE_H
#define PULSE_FILE_H

#include <array>

namespace pulse {

class File {
public:
  static const int a = 0;
  static const int b = 1;
  static const int c = 2;
  static const int d = 3;
  static const int e = 4;
  static const int f = 5;
  static const int g = 6;
  static const int h = 7;

  static const int NOFILE = 8;

  static const int VALUES_SIZE = 8;
  static const std::array<int, VALUES_SIZE> values;

  static bool isValid(int file);

private:
  File();
  ~File();
};

}

#endif
