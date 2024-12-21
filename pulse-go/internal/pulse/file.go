/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type File = int

const (
	FileA File = 0
	FileB File = 1
	FileC File = 2
	FileD File = 3
	FileE File = 4
	FileF File = 5
	FileG File = 6
	FileH File = 7

	NoFile File = 8
)

var (
	Files = [8]File{FileA, FileB, FileC, FileD, FileE, FileF, FileG, FileH}
)
