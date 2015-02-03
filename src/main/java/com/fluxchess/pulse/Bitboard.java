/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import static java.lang.Long.bitCount;
import static java.lang.Long.numberOfTrailingZeros;

/**
 * Bitboard stores squares as bits in a 64-bit long. We provide methods to
 * convert bit squares to 0x88 squares and vice versa.
 */
final class Bitboard {

  long squares = 0;

  static int next(long squares) {
    return toX88Square(numberOfTrailingZeros(squares));
  }

  static long remainder(long squares) {
    assert squares != 0;

    return squares & (squares - 1);
  }

  private static int toX88Square(int square) {
    assert square >= 0 && square < Long.SIZE;

    return ((square & ~7) << 1) | (square & 7);
  }

  private static int toBitSquare(int square) {
    assert Square.isValid(square);

    return ((square & ~7) >>> 1) | (square & 7);
  }

  int size() {
    return bitCount(squares);
  }

  void add(int square) {
    assert Square.isValid(square);
    assert (squares & (1L << toBitSquare(square))) == 0;

    squares |= 1L << toBitSquare(square);
  }

  void remove(int square) {
    assert Square.isValid(square);
    assert (squares & (1L << toBitSquare(square))) != 0;

    squares &= ~(1L << toBitSquare(square));
  }

}
