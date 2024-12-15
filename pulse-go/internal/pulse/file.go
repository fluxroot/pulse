/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type File = int

const (
	A File = 0
	B File = 1
	C File = 2
	D File = 3
	E File = 4
	F File = 5
	G File = 6
	H File = 7

	NoFile File = 8
)

var (
	Files = [8]File{A, B, C, D, E, F, G, H}
)
