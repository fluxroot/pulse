/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.engine

class MoveList {
	var size: Int = 0
	val entries: Array<MoveEntry> = Array(MAX_PLY) { MoveEntry() }

	internal fun reset() {
		size = 0
	}

	internal fun add(move: Move) {
		entries[size++].move = move
	}

	internal fun rateByMVVLVA() {
		for (i in 0 until size) {
			val move = entries[i].move
			var value = 0

			val pieceTypeValue = pieceTypeValueOf(pieceTypeOf(originPieceOf(move)))
			value += KING_VALUE / pieceTypeValue

			val targetPiece = targetPieceOf(move)
			if (targetPiece != NO_PIECE) {
				value += 10 * pieceTypeValueOf(pieceTypeOf(targetPiece))
			}

			entries[i].value = value
		}
	}

	internal fun sort() {
		for (i in 0 until size) {
			val entry = entries[i]

			var j = i
			while (j > 0 && entries[j - 1].value < entry.value) {
				entries[j] = entries[j - 1]
				j--
			}

			entries[j] = entry
		}
	}
}

class MoveEntry {
	var move: Move = NO_MOVE
	internal var value: Value = NO_VALUE
}
