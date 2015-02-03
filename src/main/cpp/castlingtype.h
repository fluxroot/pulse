/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_CASTLINGTYPE_H
#define PULSE_CASTLINGTYPE_H

#include <array>

namespace pulse {

class CastlingType {
public:
  static const int KINGSIDE = 0;
  static const int QUEENSIDE = 1;

  static const int NOCASTLINGTYPE = 2;

  static const int VALUES_SIZE = 2;
  static const std::array<int, VALUES_SIZE> values;

private:
  CastlingType();
  ~CastlingType();
};

}

#endif
