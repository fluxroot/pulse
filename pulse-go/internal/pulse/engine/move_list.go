/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

type MoveList struct {
	Size    int
	Entries [MaxPly]MoveEntry
}

func (ml *MoveList) reset() {
	ml.Size = 0
}

func (ml *MoveList) add(m Move) {
	ml.Entries[ml.Size].Move = m
	ml.Size++
}

func (ml *MoveList) rateByMVVLVA() {
	for i := 0; i < ml.Size; i++ {
		m := ml.Entries[i].Move
		v := 0

		pieceTypeValue := pieceTypeValueOf(PieceTypeOf(OriginPieceOf(m)))
		v += kingValue / pieceTypeValue

		targetPc := TargetPieceOf(m)
		if targetPc != NoPiece {
			v += 10 * pieceTypeValueOf(PieceTypeOf(targetPc))
		}

		ml.Entries[i].value = v
	}
}

func (ml *MoveList) sort() {
	for i := 0; i < ml.Size; i++ {
		entry := ml.Entries[i]

		j := i
		for j > 0 && ml.Entries[j-1].value < entry.value {
			ml.Entries[j] = ml.Entries[j-1]
			j--
		}

		ml.Entries[j] = entry
	}
}

type MoveEntry struct {
	Move  Move
	value value
}
