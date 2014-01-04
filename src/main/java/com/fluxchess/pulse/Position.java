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

import com.fluxchess.jcpi.models.GenericPosition;
import com.fluxchess.jcpi.models.IntFile;
import com.fluxchess.jcpi.models.IntRank;

public final class Position {

  public static final int MASK = 0x7F;

  public static final int a1 = 0;   public static final int a2 = 16;
  public static final int b1 = 1;   public static final int b2 = 17;
  public static final int c1 = 2;   public static final int c2 = 18;
  public static final int d1 = 3;   public static final int d2 = 19;
  public static final int e1 = 4;   public static final int e2 = 20;
  public static final int f1 = 5;   public static final int f2 = 21;
  public static final int g1 = 6;   public static final int g2 = 22;
  public static final int h1 = 7;   public static final int h2 = 23;

  public static final int a3 = 32;  public static final int a4 = 48;
  public static final int b3 = 33;  public static final int b4 = 49;
  public static final int c3 = 34;  public static final int c4 = 50;
  public static final int d3 = 35;  public static final int d4 = 51;
  public static final int e3 = 36;  public static final int e4 = 52;
  public static final int f3 = 37;  public static final int f4 = 53;
  public static final int g3 = 38;  public static final int g4 = 54;
  public static final int h3 = 39;  public static final int h4 = 55;

  public static final int a5 = 64;  public static final int a6 = 80;
  public static final int b5 = 65;  public static final int b6 = 81;
  public static final int c5 = 66;  public static final int c6 = 82;
  public static final int d5 = 67;  public static final int d6 = 83;
  public static final int e5 = 68;  public static final int e6 = 84;
  public static final int f5 = 69;  public static final int f6 = 85;
  public static final int g5 = 70;  public static final int g6 = 86;
  public static final int h5 = 71;  public static final int h6 = 87;

  public static final int a7 = 96;  public static final int a8 = 112;
  public static final int b7 = 97;  public static final int b8 = 113;
  public static final int c7 = 98;  public static final int c8 = 114;
  public static final int d7 = 99;  public static final int d8 = 115;
  public static final int e7 = 100; public static final int e8 = 116;
  public static final int f7 = 101; public static final int f8 = 117;
  public static final int g7 = 102; public static final int g8 = 118;
  public static final int h7 = 103; public static final int h8 = 119;

  public static final int NOPOSITION = 127;

  public static final int[] values = {
    a1, b1, c1, d1, e1, f1, g1, h1,
    a2, b2, c2, d2, e2, f2, g2, h2,
    a3, b3, c3, d3, e3, f3, g3, h3,
    a4, b4, c4, d4, e4, f4, g4, h4,
    a5, b5, c5, d5, e5, f5, g5, h5,
    a6, b6, c6, d6, e6, f6, g6, h6,
    a7, b7, c7, d7, e7, f7, g7, h7,
    a8, b8, c8, d8, e8, f8, g8, h8
  };

  private Position() {
  }

  public static int valueOf(GenericPosition genericPosition) {
    assert genericPosition != null;

    return IntRank.valueOf(genericPosition.rank) * 16 + IntFile.valueOf(genericPosition.file);
  }

  public static GenericPosition toGenericPosition(int position) {
    assert (position & 0x88) == 0;

    return GenericPosition.valueOf(IntFile.toGenericFile(position % 16), IntRank.toGenericRank(position >>> 4));
  }

}
