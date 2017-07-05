/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

public final class Main {

	public static void main(String[] args) {
		// Don't do any fancy stuff here. Just create our engine and
		// run it. JCPI takes care of the rest. It waits for the GUI
		// to issue commands which will call our methods using the
		// visitor pattern.
		if (args.length == 0) {
			new Pulse().run();
		} else if (args.length == 1 && args[0].equalsIgnoreCase("perft")) {
			new Perft().run();
		} else {
			printUsage();
		}
	}

	private static void printUsage() {
		System.out.println("Usage: pulse [perft]");
		System.exit(1);
	}
}
