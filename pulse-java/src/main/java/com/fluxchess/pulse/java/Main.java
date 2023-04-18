/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java;

public final class Main {

	public static void main(String[] args) {
		if (args.length == 0) {
			new Pulse().run();
		} else if (args.length == 1 && "perft".equalsIgnoreCase(args[0])) {
			new Perft().run();
		} else {
			printUsage();
			System.exit(1);
		}
	}

	private static void printUsage() {
		System.err.println("Usage: pulse-java [perft]");
	}
}
