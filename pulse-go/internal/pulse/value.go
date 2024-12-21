/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

type Value = int

const (
	Infinite           Value = 200000
	Checkmate          Value = 100000
	CheckmateThreshold Value = Checkmate - MaxPly
	Draw               Value = 0

	NoValue Value = 300000
)
