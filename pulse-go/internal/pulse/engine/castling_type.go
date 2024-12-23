/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

type CastlingType = int

const (
	Kingside  CastlingType = 0
	Queenside CastlingType = 1

	NoCastlingType CastlingType = 2
)

var (
	CastlingTypes = [2]CastlingType{Kingside, Queenside}
)

func IsValidCastlingType(ct CastlingType) bool {
	switch ct {
	case Kingside, Queenside:
		return true
	default:
		return false
	}
}
