/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type Color = int

const (
	White Color = 0
	Black Color = 1

	NoColor Color = 2
)

var (
	Colors = [2]Color{White, Black}
)
