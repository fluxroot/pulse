/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

import com.fluxchess.pulse.kotlin.Perft
import com.fluxchess.pulse.kotlin.Pulse
import kotlin.system.exitProcess

fun main(args: Array<String>) = when {
	args.isEmpty() -> {
		Pulse().run()
	}

	args.size == 1 && args[0] == "perft" -> {
		Perft().run()
	}

	else -> {
		printUsage()
		exitProcess(1)
	}
}

fun printUsage() {
	val message = "Usage: pulse-cpp [perft]"
	platform.posix.fprintf(platform.posix.stderr, "%s\n", message)
	platform.posix.fflush(platform.posix.stderr)
}
