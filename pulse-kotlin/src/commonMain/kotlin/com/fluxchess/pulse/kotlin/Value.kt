/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin

typealias Value = Int

const val INFINITE: Value = 200000
const val CHECKMATE: Value = 100000
const val CHECKMATE_THRESHOLD: Value = CHECKMATE - MAX_PLY
const val DRAW: Value = 0

const val NO_VALUE: Value = 300000
