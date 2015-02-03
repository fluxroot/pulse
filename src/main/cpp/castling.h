/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_CASTLING_H
#define PULSE_CASTLING_H

#include <array>

namespace pulse {

class Castling {
public:
  static const int WHITE_KINGSIDE = 1 << 0;
  static const int WHITE_QUEENSIDE = 1 << 1;
  static const int BLACK_KINGSIDE = 1 << 2;
  static const int BLACK_QUEENSIDE = 1 << 3;

  static const int NOCASTLING = 0;

  static const int VALUES_LENGTH = 16;

  static bool isValid(int castling);
  static int valueOf(int color, int castlingtype);
  static int getType(int castling);
  static int getColor(int castling);

private:
  Castling();
  ~Castling();
};

}

#endif
