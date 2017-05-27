/*
 * Copyright (C) 2013-2016 Phokham Nonava
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

	static const int a1 = 0, a2 = 16;
	static const int b1 = 1, b2 = 17;
	static const int c1 = 2, c2 = 18;
	static const int d1 = 3, d2 = 19;
	static const int e1 = 4, e2 = 20;
	static const int f1 = 5, f2 = 21;
	static const int g1 = 6, g2 = 22;
	static const int h1 = 7, h2 = 23;

	static const int a3 = 32, a4 = 48;
	static const int b3 = 33, b4 = 49;
	static const int c3 = 34, c4 = 50;
	static const int d3 = 35, d4 = 51;
	static const int e3 = 36, e4 = 52;
	static const int f3 = 37, f4 = 53;
	static const int g3 = 38, g4 = 54;
	static const int h3 = 39, h4 = 55;

	static const int a5 = 64, a6 = 80;
	static const int b5 = 65, b6 = 81;
	static const int c5 = 66, c6 = 82;
	static const int d5 = 67, d6 = 83;
	static const int e5 = 68, e6 = 84;
	static const int f5 = 69, f6 = 85;
	static const int g5 = 70, g6 = 86;
	static const int h5 = 71, h6 = 87;

	static const int a7 = 96, a8 = 112;
	static const int b7 = 97, b8 = 113;
	static const int c7 = 98, c8 = 114;
	static const int d7 = 99, d8 = 115;
	static const int e7 = 100, e8 = 116;
	static const int f7 = 101, f8 = 117;
	static const int g7 = 102, g8 = 118;
	static const int h7 = 103, h8 = 119;

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
