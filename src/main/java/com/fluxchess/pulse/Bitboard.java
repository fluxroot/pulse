/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

/**
 * Bitboard stores squares as bits in a 64-bit long. We provide methods to
 * convert bit squares to 0x88 squares and vice versa.
 */
final class Bitboard {

  long squares = 0;

  static int next(long squares) {
    return Square.toX88Square(Long.numberOfTrailingZeros(squares));
  }

  int size() {
    return Long.bitCount(squares);
  }

  void add(int square) {
    assert Square.isValid(square);
    assert (squares & (1L << Square.toBitSquare(square))) == 0
        : String.format("squares = %d, 0x88 square = %d, bit square = %d",
        squares, square, Square.toBitSquare(square)
    );

    squares |= 1L << Square.toBitSquare(square);
  }

  void remove(int square) {
    assert Square.isValid(square);
    assert (squares & (1L << Square.toBitSquare(square))) != 0
        : String.format("squares = %d, 0x88 square = %d, bit square = %d",
        squares, square, Square.toBitSquare(square)
    );

    squares &= ~(1L << Square.toBitSquare(square));
  }

}
