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

	args.size == 1 && "perft".equals(args[0], ignoreCase = true) -> {
		Perft().run()
	}

	else -> {
		printUsage()
		exitProcess(1)
	}
}

private fun printUsage() {
	val message = "Usage: pulse-kotlin [perft]"
	platform.posix.fprintf(platform.posix.stderr, "%s\n", message)
	platform.posix.fflush(platform.posix.stderr)
}
