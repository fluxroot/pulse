/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.uci

interface Sender {
	fun id(name: String, author: String)
	fun ok()
	fun readyOk()
	fun debug(message: String)
}

class DefaultSender(
	private val writer: Writer,
) : Sender {
	var debugMode: Boolean = false

	override fun id(name: String, author: String) {
		writer.writeln("id name $name")
		writer.writeln("id author $author")
	}

	override fun ok() {
		writer.writeln("uciok")
	}

	override fun readyOk() {
		writer.writeln("readyok")
	}

	override fun debug(message: String) {
		if (debugMode) {
			writer.writeln("info string $message")
		}
	}
}
