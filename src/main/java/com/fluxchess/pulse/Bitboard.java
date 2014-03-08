/*
 * Copyright 2013-2014 the original author or authors.
 *
 * This file is part of Pulse Chess.
 *
 * Pulse Chess is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pulse Chess is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pulse Chess.  If not, see <http://www.gnu.org/licenses/>.
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
