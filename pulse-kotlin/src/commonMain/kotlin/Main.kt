/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

import com.fluxchess.pulse.kotlin.Perft
import com.fluxchess.pulse.kotlin.Pulse
import com.fluxchess.pulse.kotlin.uci.DefaultReceiver
import com.fluxchess.pulse.kotlin.uci.DefaultSender
import com.fluxchess.pulse.kotlin.uci.StandardInputReader
import com.fluxchess.pulse.kotlin.uci.StandardOutputWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) = when {
	args.isEmpty() -> {
		val sender = DefaultSender(StandardOutputWriter())
		val engine = Pulse(sender)
		val receiver = DefaultReceiver(StandardInputReader(), sender, engine)
		receiver.run()
	}

	args.size == 1 && "perft".equals(args[0], ignoreCase = true) -> {
		Perft().run()
	}

	else -> {
		printUsage()
		exitProcess(1)
	}
}

private fun printUsage() {
	println("Usage: pulse-kotlin [perft]")
}
