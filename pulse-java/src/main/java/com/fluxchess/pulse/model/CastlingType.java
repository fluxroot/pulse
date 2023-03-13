/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

public final class CastlingType {

	public static final int KINGSIDE = 0;
	public static final int QUEENSIDE = 1;

	public static final int NOCASTLINGTYPE = 2;

	public static final int[] values = {
			KINGSIDE, QUEENSIDE
	};

	private CastlingType() {
	}
}
