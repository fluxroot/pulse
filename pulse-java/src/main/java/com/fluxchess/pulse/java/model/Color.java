/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java.model;

public final class Color {

	public static final int WHITE = 0;
	public static final int BLACK = 1;

	public static final int NOCOLOR = 2;

	public static final int[] values = {
		WHITE, BLACK
	};

	private Color() {
	}

	public static int opposite(int color) {
		switch (color) {
			case WHITE:
				return BLACK;
			case BLACK:
				return WHITE;
			default:
				throw new IllegalArgumentException();
		}
	}
}
