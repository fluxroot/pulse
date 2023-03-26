/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin

import kotlin.system.exitProcess

fun main() {
	printUsage()
}

private fun printUsage() {
	println("Usage: pulse [perft]")
	exitProcess(1)
}
