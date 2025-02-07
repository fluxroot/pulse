/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin

import com.fluxchess.pulse.kotlin.engine.Position
import com.fluxchess.pulse.kotlin.uci.Engine
import com.fluxchess.pulse.kotlin.uci.Sender

class Pulse(
	private val sender: Sender,
) : Engine {
	override fun initialize() {
		stop()
		sender.id("Pulse Kotlin 2.0.0", "Phokham Nonava")
		sender.ok()
	}

	override fun ready() {
		sender.readyOk()
	}

	override fun setOption(name: String) {
		TODO("Not yet implemented")
	}

	override fun setOption(name: String, value: String) {
		TODO("Not yet implemented")
	}

	override fun newGame() {
		TODO("Not yet implemented")
	}

	override fun position(position: Position) {
		TODO("Not yet implemented")
	}

	override fun start() {
		TODO("Not yet implemented")
	}

	override fun stop() {
		TODO("Not yet implemented")
	}

	override fun ponderHit() {
		TODO("Not yet implemented")
	}

	override fun quit() {
		TODO("Not yet implemented")
	}
}
