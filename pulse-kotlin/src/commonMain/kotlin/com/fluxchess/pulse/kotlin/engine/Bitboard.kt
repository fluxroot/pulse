/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

internal typealias Bitboard = ULong
internal typealias BitboardArray = ULongArray
internal typealias BitSquare = Int

internal inline fun BitboardArray(size: Int, init: (Int) -> Bitboard): BitboardArray =
	ULongArray(size) { index -> init(index) }

internal fun addSquare(square: Square, bitboard: Bitboard): Bitboard =
	bitboard or (1uL shl toBitSquare(square))

internal fun removeSquare(square: Square, bitboard: Bitboard): Bitboard =
	bitboard and (1uL shl toBitSquare(square)).inv()

internal fun next(bitboard: Bitboard): Square =
	toX88Square(bitboard.countTrailingZeroBits())

internal fun remainder(bitboard: Bitboard): Bitboard =
	bitboard and (bitboard - 1u)

private fun toBitSquare(square: Square): BitSquare =
	((square and 7.inv()) shr 1) or (square and 7)

private fun toX88Square(square: BitSquare): Square =
	((square and 7.inv()) shl 1) or (square and 7)
