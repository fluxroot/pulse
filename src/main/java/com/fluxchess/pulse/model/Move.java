/*
 * Copyright (C) 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.model;

import static com.fluxchess.pulse.model.MoveType.NOMOVETYPE;
import static com.fluxchess.pulse.model.Piece.NOPIECE;
import static com.fluxchess.pulse.model.PieceType.NOPIECETYPE;
import static com.fluxchess.pulse.model.Square.NOSQUARE;

/**
 * This class represents a move as a int value. The fields are represented by
 * the following bits.
 * <ul>
 * <li><code> 0 -  2</code>: type (required)</li>
 * <li><code> 3 -  9</code>: origin square (required)</li>
 * <li><code>10 - 16</code>: target square (required)</li>
 * <li><code>17 - 21</code>: origin piece (required)</li>
 * <li><code>22 - 26</code>: target piece (optional)</li>
 * <li><code>27 - 29</code>: promotion type (optional)</li>
 * </ul>
 */
public final class Move {

	// These are our bit masks
	private static final int TYPE_SHIFT = 0;
	private static final int TYPE_MASK = MoveType.MASK << TYPE_SHIFT;
	private static final int ORIGIN_SQUARE_SHIFT = 3;
	private static final int ORIGIN_SQUARE_MASK = Square.MASK << ORIGIN_SQUARE_SHIFT;
	private static final int TARGET_SQUARE_SHIFT = 10;
	private static final int TARGET_SQUARE_MASK = Square.MASK << TARGET_SQUARE_SHIFT;
	private static final int ORIGIN_PIECE_SHIFT = 17;
	private static final int ORIGIN_PIECE_MASK = Piece.MASK << ORIGIN_PIECE_SHIFT;
	private static final int TARGET_PIECE_SHIFT = 22;
	private static final int TARGET_PIECE_MASK = Piece.MASK << TARGET_PIECE_SHIFT;
	private static final int PROMOTION_SHIFT = 27;
	private static final int PROMOTION_MASK = PieceType.MASK << PROMOTION_SHIFT;

	// We don't use 0 as a null value to protect against errors.
	public static final int NOMOVE = (NOMOVETYPE << TYPE_SHIFT)
			| (NOSQUARE << ORIGIN_SQUARE_SHIFT)
			| (NOSQUARE << TARGET_SQUARE_SHIFT)
			| (NOPIECE << ORIGIN_PIECE_SHIFT)
			| (NOPIECE << TARGET_PIECE_SHIFT)
			| (NOPIECETYPE << PROMOTION_SHIFT);

	private Move() {
	}

	public static int valueOf(int type, int originSquare, int targetSquare, int originPiece, int targetPiece, int promotion) {
		int move = 0;

		// Encode type
		move |= type << TYPE_SHIFT;

		// Encode origin square
		move |= originSquare << ORIGIN_SQUARE_SHIFT;

		// Encode target square
		move |= targetSquare << TARGET_SQUARE_SHIFT;

		// Encode origin piece
		move |= originPiece << ORIGIN_PIECE_SHIFT;

		// Encode target piece
		move |= targetPiece << TARGET_PIECE_SHIFT;

		// Encode promotion
		move |= promotion << PROMOTION_SHIFT;

		return move;
	}

	public static int getType(int move) {
		return (move & TYPE_MASK) >>> TYPE_SHIFT;
	}

	public static int getOriginSquare(int move) {
		return (move & ORIGIN_SQUARE_MASK) >>> ORIGIN_SQUARE_SHIFT;
	}

	public static int getTargetSquare(int move) {
		return (move & TARGET_SQUARE_MASK) >>> TARGET_SQUARE_SHIFT;
	}

	public static int getOriginPiece(int move) {
		return (move & ORIGIN_PIECE_MASK) >>> ORIGIN_PIECE_SHIFT;
	}

	public static int getTargetPiece(int move) {
		return (move & TARGET_PIECE_MASK) >>> TARGET_PIECE_SHIFT;
	}

	public static int getPromotion(int move) {
		return (move & PROMOTION_MASK) >>> PROMOTION_SHIFT;
	}
}
