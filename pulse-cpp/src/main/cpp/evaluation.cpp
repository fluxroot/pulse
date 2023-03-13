// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "evaluation.h"

namespace pulse::evaluation {
namespace {
constexpr int materialWeight = 100;
constexpr int mobilityWeight = 80;

constexpr int MAX_WEIGHT = 100;

int evaluateMaterial(int color, Position& position) {
	int material = position.material[color];

	// Add bonus for bishop pair
	if (bitboard::size(position.pieces[color][piecetype::BISHOP]) >= 2) {
		material += 50;
	}

	return material;
}

int evaluateMobility(Position& position, int square, const std::vector<int>& directions) {
	int mobility = 0;
	bool sliding = piecetype::isSliding(piece::getType(position.board[square]));

	for (auto direction : directions) {
		int targetSquare = square + direction;

		while (square::isValid(targetSquare)) {
			mobility++;

			if (sliding && position.board[targetSquare] == piece::NOPIECE) {
				targetSquare += direction;
			} else {
				break;
			}
		}
	}

	return mobility;
}

int evaluateMobility(int color, Position& position) {
	int knightMobility = 0;
	for (auto squares = position.pieces[color][piecetype::KNIGHT];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		knightMobility += evaluateMobility(position, square, square::knightDirections);
	}

	int bishopMobility = 0;
	for (auto squares = position.pieces[color][piecetype::BISHOP];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		bishopMobility += evaluateMobility(position, square, square::bishopDirections);
	}

	int rookMobility = 0;
	for (auto squares = position.pieces[color][piecetype::ROOK];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		rookMobility += evaluateMobility(position, square, square::rookDirections);
	}

	int queenMobility = 0;
	for (auto squares = position.pieces[color][piecetype::QUEEN];
		 squares != 0; squares = bitboard::remainder(squares)) {
		int square = bitboard::next(squares);
		queenMobility += evaluateMobility(position, square, square::queenDirections);
	}

	return knightMobility * 4
		   + bishopMobility * 5
		   + rookMobility * 2
		   + queenMobility;
}
}

/**
 * Evaluates the position.
 *
 * @param position the position.
 * @return the evaluation value in centipawns.
 */
int evaluate(Position& position) {
	// Initialize
	int myColor = position.activeColor;
	int oppositeColor = color::opposite(myColor);
	int value = 0;

	// Evaluate material
	int materialScore = (evaluateMaterial(myColor, position) - evaluateMaterial(oppositeColor, position))
						* materialWeight / MAX_WEIGHT;
	value += materialScore;

	// Evaluate mobility
	int mobilityScore = (evaluateMobility(myColor, position) - evaluateMobility(oppositeColor, position))
						* mobilityWeight / MAX_WEIGHT;
	value += mobilityScore;

	// Add Tempo
	value += TEMPO;

	return value;
}
}
