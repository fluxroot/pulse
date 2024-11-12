/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.uci

interface Receiver {
	fun run()
}

class DefaultReceiver(
	private val reader: Reader,
	private val sender: DefaultSender,
	private val engine: Engine,
) : Receiver {
	override fun run() {
		while (true) {
			val line = reader.readln()
			if (line == null) {
				engine.quit()
				return
			}
			val tokens = line.trim().split("\\s+".toRegex(), 2)
			if (tokens.isEmpty()) {
				continue
			}
			when (tokens[0]) {
				"uci" -> engine.initialize()
				"debug" -> parseDebug(tokens)
				"isready" -> engine.ready()
				"setoption" -> parseSetOption(tokens)
				"register" -> sender.debug("Unsupported command: register")
				"ucinewgame" -> engine.newGame()
				"position" -> parsePosition()
				"go" -> parseGo()
				"stop" -> engine.stop()
				"ponderhit" -> engine.ponderHit()
				"quit" -> {
					engine.quit()
					return
				}

				else -> sender.debug("Unknown command: ${tokens[0]}")
			}
		}
	}

	private fun parseDebug(tokens: List<String>) {
		when (tokens.size) {
			1 -> sender.debugMode = !sender.debugMode // Toggle debug
			2 -> when {
				tokens[1] == "on" -> sender.debugMode = true
				tokens[1] == "off" -> sender.debugMode = false
				else -> sender.debug("Unknown argument: ${tokens[1]}")
			}
		}
	}

	private fun parseSetOption(tokens: List<String>) {
		if (tokens.size != 2) {
			sender.debug("Argument required")
			return
		}
		val nameValueMatch = Regex("^name\\s+(?<name>.+?)\\s+value\\s+(?<value>.+)").find(tokens[1])
		if (nameValueMatch != null) {
			val (name, value) = nameValueMatch.destructured
			engine.setOption(name, value)
			return
		}
		val nameOnlyMatch = Regex("^name\\s+(?<name>.+)").find(tokens[1])
		if (nameOnlyMatch != null) {
			val (name) = nameOnlyMatch.destructured
			engine.setOption(name)
			return
		}
		sender.debug("Error parsing argument: ${tokens[1]}")
	}

	private fun parsePosition() {
		engine.position()
		TODO("Not implemented")
	}

	private fun parseGo() {
		engine.start()
		TODO("Not implemented")
	}
}
