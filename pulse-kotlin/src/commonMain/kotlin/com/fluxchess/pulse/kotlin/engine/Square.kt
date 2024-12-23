/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

typealias Square = Int

const val A1: Square = 0
const val B1: Square = 1
const val C1: Square = 2
const val D1: Square = 3
const val E1: Square = 4
const val F1: Square = 5
const val G1: Square = 6
const val H1: Square = 7

const val A2: Square = 16
const val B2: Square = 17
const val C2: Square = 18
const val D2: Square = 19
const val E2: Square = 20
const val F2: Square = 21
const val G2: Square = 22
const val H2: Square = 23

const val A3: Square = 32
const val B3: Square = 33
const val C3: Square = 34
const val D3: Square = 35
const val E3: Square = 36
const val F3: Square = 37
const val G3: Square = 38
const val H3: Square = 39

const val A4: Square = 48
const val B4: Square = 49
const val C4: Square = 50
const val D4: Square = 51
const val E4: Square = 52
const val F4: Square = 53
const val G4: Square = 54
const val H4: Square = 55

const val A5: Square = 64
const val B5: Square = 65
const val C5: Square = 66
const val D5: Square = 67
const val E5: Square = 68
const val F5: Square = 69
const val G5: Square = 70
const val H5: Square = 71

const val A6: Square = 80
const val B6: Square = 81
const val C6: Square = 82
const val D6: Square = 83
const val E6: Square = 84
const val F6: Square = 85
const val G6: Square = 86
const val H6: Square = 87

const val A7: Square = 96
const val B7: Square = 97
const val C7: Square = 98
const val D7: Square = 99
const val E7: Square = 100
const val F7: Square = 101
const val G7: Square = 102
const val H7: Square = 103

const val A8: Square = 112
const val B8: Square = 113
const val C8: Square = 114
const val D8: Square = 115
const val E8: Square = 116
const val F8: Square = 117
const val G8: Square = 118
const val H8: Square = 119

const val NO_SQUARE: Square = 127

internal const val SQUARES_MAX_VALUE: Square = 128

val squares = intArrayOf(
	A1, B1, C1, D1, E1, F1, G1, H1,
	A2, B2, C2, D2, E2, F2, G2, H2,
	A3, B3, C3, D3, E3, F3, G3, H3,
	A4, B4, C4, D4, E4, F4, G4, H4,
	A5, B5, C5, D5, E5, F5, G5, H5,
	A6, B6, C6, D6, E6, F6, G6, H6,
	A7, B7, C7, D7, E7, F7, G7, H7,
	A8, B8, C8, D8, E8, F8, G8, H8,
)

fun isValidSquare(square: Square): Boolean = (square and 0x88) == 0

fun squareOf(file: File, rank: Rank): Square = (rank shl 4) + file

fun fileOf(square: Square): File = square and 0xF

fun rankOf(square: Square): Rank = square ushr 4
