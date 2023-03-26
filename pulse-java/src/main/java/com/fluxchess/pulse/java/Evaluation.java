/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java;

import com.fluxchess.pulse.java.model.Piece;
import com.fluxchess.pulse.java.model.PieceType;
import com.fluxchess.pulse.java.model.Square;

import static com.fluxchess.pulse.java.model.Color.opposite;
import static com.fluxchess.pulse.java.model.Square.bishopDirections;
import static com.fluxchess.pulse.java.model.Square.knightDirections;
import static com.fluxchess.pulse.java.model.Square.queenDirections;
import static com.fluxchess.pulse.java.model.Square.rookDirections;

final class Evaluation {

	static final int TEMPO = 1;

	private static final int MATERIAL_WEIGHT = 100;
	private static final int MOBILITY_WEIGHT = 80;
	private static final int MAX_WEIGHT = 100;

	/**
	 * Evaluates the position.
	 *
	 * @param position the position.
	 * @return the evaluation value in centipawns.
	 */
	int evaluate(Position position) {
		// Initialize
		int myColor = position.activeColor;
		int oppositeColor = opposite(myColor);
		int value = 0;

		// Evaluate material
		int materialScore = (evaluateMaterial(myColor, position) - evaluateMaterial(oppositeColor, position))
			* MATERIAL_WEIGHT / MAX_WEIGHT;
		value += materialScore;

		// Evaluate mobility
		int mobilityScore = (evaluateMobility(myColor, position) - evaluateMobility(oppositeColor, position))
			* MOBILITY_WEIGHT / MAX_WEIGHT;
		value += mobilityScore;

		// Add Tempo
		value += TEMPO;

		return value;
	}

	private int evaluateMaterial(int color, Position position) {
		int material = position.material[color];

		// Add bonus for bishop pair
		if (Bitboard.size(position.pieces[color][PieceType.BISHOP]) >= 2) {
			material += 50;
		}

		return material;
	}

	private int evaluateMobility(int color, Position position) {
		int knightMobility = 0;
		for (long squares = position.pieces[color][PieceType.KNIGHT]; squares != 0; squares = Bitboard.remainder(squares)) {
			int square = Bitboard.next(squares);
			knightMobility += evaluateMobility(position, square, knightDirections);
		}

		int bishopMobility = 0;
		for (long squares = position.pieces[color][PieceType.BISHOP]; squares != 0; squares = Bitboard.remainder(squares)) {
			int square = Bitboard.next(squares);
			bishopMobility += evaluateMobility(position, square, bishopDirections);
		}

		int rookMobility = 0;
		for (long squares = position.pieces[color][PieceType.ROOK]; squares != 0; squares = Bitboard.remainder(squares)) {
			int square = Bitboard.next(squares);
			rookMobility += evaluateMobility(position, square, rookDirections);
		}

		int queenMobility = 0;
		for (long squares = position.pieces[color][PieceType.QUEEN]; squares != 0; squares = Bitboard.remainder(squares)) {
			int square = Bitboard.next(squares);
			queenMobility += evaluateMobility(position, square, queenDirections);
		}

		return knightMobility * 4
			+ bishopMobility * 5
			+ rookMobility * 2
			+ queenMobility;
	}

	private int evaluateMobility(Position position, int square, int[] directions) {
		int mobility = 0;
		boolean sliding = PieceType.isSliding(Piece.getType(position.board[square]));

		for (int direction : directions) {
			int targetSquare = square + direction;

			while (Square.isValid(targetSquare)) {
				mobility++;

				if (sliding && position.board[targetSquare] == Piece.NOPIECE) {
					targetSquare += direction;
				} else {
					break;
				}
			}
		}

		return mobility;
	}
}
