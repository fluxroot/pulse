/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type Castling = int

const (
	WhiteKingside  Castling = 1 // 1 << 0
	WhiteQueenside Castling = 1 << 1
	BlackKingside  Castling = 1 << 2
	BlackQueenside Castling = 1 << 3

	NoCastling Castling = 0
)

var (
	Castlings = [4]Castling{
		WhiteKingside, WhiteQueenside,
		BlackKingside, BlackQueenside,
	}
)
