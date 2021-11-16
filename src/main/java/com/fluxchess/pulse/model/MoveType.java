/*
 * Copyright (C) 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

public final class MoveType {

	public static final int MASK = 0x7;

	public static final int NORMAL = 0;
	public static final int PAWNDOUBLE = 1;
	public static final int PAWNPROMOTION = 2;
	public static final int ENPASSANT = 3;
	public static final int CASTLING = 4;

	public static final int NOMOVETYPE = 5;

	private MoveType() {
	}
}
