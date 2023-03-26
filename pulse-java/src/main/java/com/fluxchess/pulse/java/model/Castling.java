/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java.model;

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
			case Color.WHITE:
				switch (castlingtype) {
					case CastlingType.KINGSIDE:
						return WHITE_KINGSIDE;
					case CastlingType.QUEENSIDE:
						return WHITE_QUEENSIDE;
					default:
						throw new IllegalArgumentException();
				}
			case Color.BLACK:
				switch (castlingtype) {
					case CastlingType.KINGSIDE:
						return BLACK_KINGSIDE;
					case CastlingType.QUEENSIDE:
						return BLACK_QUEENSIDE;
					default:
						throw new IllegalArgumentException();
				}
			default:
				throw new IllegalArgumentException();
		}
	}
}
