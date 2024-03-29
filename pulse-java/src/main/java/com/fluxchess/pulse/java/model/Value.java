/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java.model;

import static com.fluxchess.pulse.java.model.Depth.MAX_PLY;
import static java.lang.Math.abs;

public final class Value {

	public static final int INFINITE = 200000;
	public static final int CHECKMATE = 100000;
	public static final int CHECKMATE_THRESHOLD = CHECKMATE - MAX_PLY;
	public static final int DRAW = 0;

	public static final int NOVALUE = 300000;

	private Value() {
	}

	public static boolean isCheckmate(int value) {
		int absvalue = abs(value);
		return absvalue >= CHECKMATE_THRESHOLD && absvalue <= CHECKMATE;
	}
}
