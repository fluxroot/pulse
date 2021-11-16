/*
 * Copyright (C) 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

import static com.fluxchess.pulse.model.CastlingType.KINGSIDE;
import static com.fluxchess.pulse.model.CastlingType.QUEENSIDE;
import static com.fluxchess.pulse.model.Color.BLACK;
import static com.fluxchess.pulse.model.Color.WHITE;

public final class Castling {

	public static final int WHITE_KINGSIDE = 1; // 1 << 0
	public static final int WHITE_QUEENSIDE = 1 << 1;
	public static final int BLACK_KINGSIDE = 1 << 2;
	public static final int BLACK_QUEENSIDE = 1 << 3;

	public static final int NOCASTLING = 0;

	public static final int VALUES_LENGTH = 16;

	private Castling() {
	}

	public static int valueOf(int color, int castlingtype) {
		switch (color) {
			case WHITE:
				switch (castlingtype) {
					case KINGSIDE:
						return WHITE_KINGSIDE;
					case QUEENSIDE:
						return WHITE_QUEENSIDE;
					default:
						throw new IllegalArgumentException();
				}
			case BLACK:
				switch (castlingtype) {
					case KINGSIDE:
						return BLACK_KINGSIDE;
					case QUEENSIDE:
						return BLACK_QUEENSIDE;
					default:
						throw new IllegalArgumentException();
				}
			default:
				throw new IllegalArgumentException();
		}
	}
}
