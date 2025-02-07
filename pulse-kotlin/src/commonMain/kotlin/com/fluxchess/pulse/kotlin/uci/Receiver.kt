/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.uci

import com.fluxchess.pulse.kotlin.engine.MoveList
import com.fluxchess.pulse.kotlin.engine.generateLegalMoves

const val STARTING_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

private val uciTokenRegex = Regex("\\s+")

private val nameValueOptionRegex = Regex("^name\\s+(?<name>.+?)\\s+value\\s+(?<value>.+)")
private val nameOnlyOptionRegex = Regex("^name\\s+(?<name>.+)")

private val startposMovesRegex = Regex("^startpos\\s+moves\\s+(?<moves>.+)")
private val fenMovesRegex = Regex("^fen\\s+(?<fen>.+?)\\s+moves\\s+(?<moves>.+)")
private val startposOnlyRegex = Regex("^startpos")
private val fenOnlyRegex = Regex("^fen\\s+(?<fen>.+)")

private val movesTokenRegex = Regex("\\s+")

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
			val tokens = line.trim().split(uciTokenRegex, 2)
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
				"position" -> parsePosition(tokens)
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
		val nameValueMatch = nameValueOptionRegex.find(tokens[1])
		if (nameValueMatch != null) {
			val (name, value) = nameValueMatch.destructured
			engine.setOption(name, value)
			return
		}
		val nameOnlyMatch = nameOnlyOptionRegex.find(tokens[1])
		if (nameOnlyMatch != null) {
			val (name) = nameOnlyMatch.destructured
			engine.setOption(name)
			return
		}
		sender.debug("Error parsing argument: ${tokens[1]}")
	}

	private fun parsePosition(tokens: List<String>) {
		if (tokens.size != 2) {
			sender.debug("Argument required")
			return
		}
		val startposMovesMatch = startposMovesRegex.find(tokens[1])
		if (startposMovesMatch != null) {
			val (moves) = startposMovesMatch.destructured
			playMoves(STARTING_POSITION_FEN, moves)
			return
		}
		val fenMovesMatch = fenMovesRegex.find(tokens[1])
		if (fenMovesMatch != null) {
			val (fen, moves) = fenMovesMatch.destructured
			playMoves(fen, moves)
			return
		}
		val startposOnlyMatch = startposOnlyRegex.find(tokens[1])
		if (startposOnlyMatch != null) {
			playMoves(STARTING_POSITION_FEN)
			return
		}
		val fenOnlyMatch = fenOnlyRegex.find(tokens[1])
		if (fenOnlyMatch != null) {
			val (fen) = fenOnlyMatch.destructured
			playMoves(fen)
			return
		}
		sender.debug("Error parsing argument: ${tokens[1]}")
	}

	private fun playMoves(fen: String, moves: String = "") {
		val legalMoves = MoveList()
		val position = fen.toPositionOrNull()
		if (position == null) {
			sender.debug("Invalid position: $fen")
			return
		}
		if (moves.isNotBlank()) {
			val moveList = moves.split(movesTokenRegex)
			moveList.forEach { move ->
				generateLegalMoves(legalMoves, position)
				for (i in 0 until legalMoves.size) {
					val legalMove = legalMoves.entries[i].move
					if (legalMove.toNotation() == move) {
						position.makeMove(legalMove)
						return@forEach
					}
				}
				sender.debug("Invalid move: $move, position: $position")
				return
			}
		}
		engine.position(position)
		return
	}

	private fun parseGo() {
	}
}
