// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.
#pragma once

#include <array>
#include <vector>

namespace pulse::square {

constexpr int MASK = 0x7F;

constexpr int a1 = 0, a2 = 16;
constexpr int b1 = 1, b2 = 17;
constexpr int c1 = 2, c2 = 18;
constexpr int d1 = 3, d2 = 19;
constexpr int e1 = 4, e2 = 20;
constexpr int f1 = 5, f2 = 21;
constexpr int g1 = 6, g2 = 22;
constexpr int h1 = 7, h2 = 23;

constexpr int a3 = 32, a4 = 48;
constexpr int b3 = 33, b4 = 49;
constexpr int c3 = 34, c4 = 50;
constexpr int d3 = 35, d4 = 51;
constexpr int e3 = 36, e4 = 52;
constexpr int f3 = 37, f4 = 53;
constexpr int g3 = 38, g4 = 54;
constexpr int h3 = 39, h4 = 55;

constexpr int a5 = 64, a6 = 80;
constexpr int b5 = 65, b6 = 81;
constexpr int c5 = 66, c6 = 82;
constexpr int d5 = 67, d6 = 83;
constexpr int e5 = 68, e6 = 84;
constexpr int f5 = 69, f6 = 85;
constexpr int g5 = 70, g6 = 86;
constexpr int h5 = 71, h6 = 87;

constexpr int a7 = 96, a8 = 112;
constexpr int b7 = 97, b8 = 113;
constexpr int c7 = 98, c8 = 114;
constexpr int d7 = 99, d8 = 115;
constexpr int e7 = 100, e8 = 116;
constexpr int f7 = 101, f8 = 117;
constexpr int g7 = 102, g8 = 118;
constexpr int h7 = 103, h8 = 119;

constexpr int NOSQUARE = 127;

constexpr int VALUES_LENGTH = 128;
constexpr int VALUES_SIZE = 64;
constexpr std::array<int, VALUES_SIZE> values = {
		a1, b1, c1, d1, e1, f1, g1, h1,
		a2, b2, c2, d2, e2, f2, g2, h2,
		a3, b3, c3, d3, e3, f3, g3, h3,
		a4, b4, c4, d4, e4, f4, g4, h4,
		a5, b5, c5, d5, e5, f5, g5, h5,
		a6, b6, c6, d6, e6, f6, g6, h6,
		a7, b7, c7, d7, e7, f7, g7, h7,
		a8, b8, c8, d8, e8, f8, g8, h8
};

// These are our move directions
// N = north, E = east, S = south, W = west
constexpr int N = 16;
constexpr int E = 1;
constexpr int S = -16;
constexpr int W = -1;
constexpr int NE = N + E;
constexpr int SE = S + E;
constexpr int SW = S + W;
constexpr int NW = N + W;

inline const std::vector<std::vector<int>> pawnDirections = {
		{N, NE, NW}, // color::WHITE
		{S, SE, SW}  // color::BLACK
};
inline const std::vector<int> knightDirections = {
		N + N + E,
		N + N + W,
		N + E + E,
		N + W + W,
		S + S + E,
		S + S + W,
		S + E + E,
		S + W + W
};
inline const std::vector<int> bishopDirections = {
		NE, NW, SE, SW
};
inline const std::vector<int> rookDirections = {
		N, E, S, W
};
inline const std::vector<int> queenDirections = {
		N, E, S, W,
		NE, NW, SE, SW
};
inline const std::vector<int> kingDirections = {
		N, E, S, W,
		NE, NW, SE, SW
};

bool isValid(int square);

int valueOf(int file, int rank);

int getFile(int square);

int getRank(int square);
}
