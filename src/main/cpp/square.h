/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_SQUARE_H
#define PULSE_SQUARE_H

#include <array>
#include <vector>

namespace pulse {

class Square {
public:
  static const int MASK = 0x7F;

  static const int a1 = 0;   static const int a2 = 16;
  static const int b1 = 1;   static const int b2 = 17;
  static const int c1 = 2;   static const int c2 = 18;
  static const int d1 = 3;   static const int d2 = 19;
  static const int e1 = 4;   static const int e2 = 20;
  static const int f1 = 5;   static const int f2 = 21;
  static const int g1 = 6;   static const int g2 = 22;
  static const int h1 = 7;   static const int h2 = 23;

  static const int a3 = 32;  static const int a4 = 48;
  static const int b3 = 33;  static const int b4 = 49;
  static const int c3 = 34;  static const int c4 = 50;
  static const int d3 = 35;  static const int d4 = 51;
  static const int e3 = 36;  static const int e4 = 52;
  static const int f3 = 37;  static const int f4 = 53;
  static const int g3 = 38;  static const int g4 = 54;
  static const int h3 = 39;  static const int h4 = 55;

  static const int a5 = 64;  static const int a6 = 80;
  static const int b5 = 65;  static const int b6 = 81;
  static const int c5 = 66;  static const int c6 = 82;
  static const int d5 = 67;  static const int d6 = 83;
  static const int e5 = 68;  static const int e6 = 84;
  static const int f5 = 69;  static const int f6 = 85;
  static const int g5 = 70;  static const int g6 = 86;
  static const int h5 = 71;  static const int h6 = 87;

  static const int a7 = 96;  static const int a8 = 112;
  static const int b7 = 97;  static const int b8 = 113;
  static const int c7 = 98;  static const int c8 = 114;
  static const int d7 = 99;  static const int d8 = 115;
  static const int e7 = 100; static const int e8 = 116;
  static const int f7 = 101; static const int f8 = 117;
  static const int g7 = 102; static const int g8 = 118;
  static const int h7 = 103; static const int h8 = 119;

  static const int NOSQUARE = 127;

  static const int VALUES_LENGTH = 128;
  static const int VALUES_SIZE = 64;
  static const std::array<int, VALUES_SIZE> values;

  // These are our move directions
  // N = north, E = east, S = south, W = west
  static const int N = 16;
  static const int E = 1;
  static const int S = -16;
  static const int W = -1;
  static const int NE = N + E;
  static const int SE = S + E;
  static const int SW = S + W;
  static const int NW = N + W;

  static const std::vector<std::vector<int>> pawnDirections;
  static const std::vector<int> knightDirections;
  static const std::vector<int> bishopDirections;
  static const std::vector<int> rookDirections;
  static const std::vector<int> queenDirections;
  static const std::vector<int> kingDirections;

  static bool isValid(int square);
  static int valueOf(int file, int rank);
  static int getFile(int square);
  static int getRank(int square);

private:
  Square();
  ~Square();
};

}

#endif
