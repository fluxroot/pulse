/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_PIECE_H
#define PULSE_PIECE_H

#include <array>

namespace pulse {

class Piece {
public:
  static const int MASK = 0x1F;

  static const int WHITE_PAWN = 0;
  static const int WHITE_KNIGHT = 1;
  static const int WHITE_BISHOP = 2;
  static const int WHITE_ROOK = 3;
  static const int WHITE_QUEEN = 4;
  static const int WHITE_KING = 5;
  static const int BLACK_PAWN = 6;
  static const int BLACK_KNIGHT = 7;
  static const int BLACK_BISHOP = 8;
  static const int BLACK_ROOK = 9;
  static const int BLACK_QUEEN = 10;
  static const int BLACK_KING = 11;

  static const int NOPIECE = 12;

  static const int VALUES_SIZE = 12;
  static const std::array<int, VALUES_SIZE> values;

  static bool isValid(int piece);
  static int valueOf(int piecetype, int color);
  static int getType(int piece);
  static int getColor(int piece);

private:
  Piece();
  ~Piece();
};

}

#endif
