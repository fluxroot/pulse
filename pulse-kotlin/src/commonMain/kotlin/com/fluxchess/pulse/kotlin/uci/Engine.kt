/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.uci

interface Engine {
	fun initialize()
	fun ready()
	fun setOption(name: String)
	fun setOption(name: String, value: String)
	fun newGame()
	fun position()
	fun start()
	fun stop()
	fun ponderHit()
	fun quit()
}
