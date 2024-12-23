/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import "fmt"

type Color = int

const (
	White Color = 0
	Black Color = 1

	NoColor Color = 2
)

var (
	Colors = [2]Color{White, Black}
)

func IsValidColor(col Color) bool {
	switch col {
	case White, Black:
		return true
	default:
		return false
	}
}

func OppositeOf(col Color) Color {
	switch col {
	case White:
		return Black
	case Black:
		return White
	default:
		panic(fmt.Sprintf("Invalid color: %v", col))
	}
}
