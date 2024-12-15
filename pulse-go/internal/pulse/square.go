/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type Square = int

const (
	A1 Square = 0
	B1 Square = 1
	C1 Square = 2
	D1 Square = 3
	E1 Square = 4
	F1 Square = 5
	G1 Square = 6
	H1 Square = 7

	A2 Square = 16
	B2 Square = 17
	C2 Square = 18
	D2 Square = 19
	E2 Square = 20
	F2 Square = 21
	G2 Square = 22
	H2 Square = 23

	A3 Square = 32
	B3 Square = 33
	C3 Square = 34
	D3 Square = 35
	E3 Square = 36
	F3 Square = 37
	G3 Square = 38
	H3 Square = 39

	A4 Square = 48
	B4 Square = 49
	C4 Square = 50
	D4 Square = 51
	E4 Square = 52
	F4 Square = 53
	G4 Square = 54
	H4 Square = 55

	A5 Square = 64
	B5 Square = 65
	C5 Square = 66
	D5 Square = 67
	E5 Square = 68
	F5 Square = 69
	G5 Square = 70
	H5 Square = 71

	A6 Square = 80
	B6 Square = 81
	C6 Square = 82
	D6 Square = 83
	E6 Square = 84
	F6 Square = 85
	G6 Square = 86
	H6 Square = 87

	A7 Square = 96
	B7 Square = 97
	C7 Square = 98
	D7 Square = 99
	E7 Square = 100
	F7 Square = 101
	G7 Square = 102
	H7 Square = 103

	A8 Square = 112
	B8 Square = 113
	C8 Square = 114
	D8 Square = 115
	E8 Square = 116
	F8 Square = 117
	G8 Square = 118
	H8 Square = 119

	NoSquare Square = 127
)

var (
	Squares = [64]Square{
		A1, B1, C1, D1, E1, F1, G1, H1,
		A2, B2, C2, D2, E2, F2, G2, H2,
		A3, B3, C3, D3, E3, F3, G3, H3,
		A4, B4, C4, D4, E4, F4, G4, H4,
		A5, B5, C5, D5, E5, F5, G5, H5,
		A6, B6, C6, D6, E6, F6, G6, H6,
		A7, B7, C7, D7, E7, F7, G7, H7,
		A8, B8, C8, D8, E8, F8, G8, H8,
	}
)

func SquareOf(file File, rank Rank) Square {
	return (rank << 4) + file
}

func FileOf(square Square) File {
	return square & 0xF
}

func RankOf(square Square) Rank {
	return square >> 4
}
