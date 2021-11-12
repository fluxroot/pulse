/*
 * Copyright (C) 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.pulse.model.Depth.MAX_PLY;
import static com.fluxchess.pulse.model.Move.NOMOVE;
import static com.fluxchess.pulse.model.PieceType.KING_VALUE;
import static com.fluxchess.pulse.model.Value.NOVALUE;

/**
 * This class stores our moves for a specific position. For the root node we
 * will populate pv for every root move.
 */
public final class MoveList<T extends MoveList.MoveEntry> {

	private static final int MAX_MOVES = 256;

	public final T[] entries;
	public int size = 0;

	public static final class MoveVariation {

		public final int[] moves = new int[MAX_PLY];
		public int size = 0;
	}

	public static class MoveEntry {

		public int move = NOMOVE;
		public int value = NOVALUE;
	}

	public static final class RootEntry extends MoveEntry {

		public final MoveVariation pv = new MoveVariation();
	}

	public MoveList(Class<T> clazz) {
		@SuppressWarnings("unchecked") final T[] entries = (T[]) Array.newInstance(clazz, MAX_MOVES);
		this.entries = entries;
		try {
			for (int i = 0; i < entries.length; i++) {
				entries[i] = clazz.getDeclaredConstructor().newInstance();
			}
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Sorts the move list using a stable insertion sort.
	 */
	public void sort() {
		for (int i = 1; i < size; i++) {
			T entry = entries[i];

			int j = i;
			while ((j > 0) && (entries[j - 1].value < entry.value)) {
				entries[j] = entries[j - 1];
				j--;
			}

			entries[j] = entry;
		}
	}

	/**
	 * Rates the moves in the list according to "Most Valuable Victim - Least Valuable Aggressor".
	 */
	public void rateFromMVVLVA() {
		for (int i = 0; i < size; i++) {
			int move = entries[i].move;
			int value = 0;

			int piecetypeValue = PieceType.getValue(Piece.getType(Move.getOriginPiece(move)));
			value += KING_VALUE / piecetypeValue;

			int target = Move.getTargetPiece(move);
			if (Piece.isValid(target)) {
				value += 10 * PieceType.getValue(Piece.getType(target));
			}

			entries[i].value = value;
		}
	}
}
