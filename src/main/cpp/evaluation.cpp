/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "evaluation.h"
#include "piecetype.h"
#include "value.h"

#include <cassert>

namespace pulse {

int Evaluation::materialWeight = 100;
int Evaluation::mobilityWeight = 80;

/**
 * Evaluates the position.
 *
 * @param position the position.
 * @return the evaluation value in centipawns.
 */
int Evaluation::evaluate(Position& position) {
  // Initialize
  int myColor = position.activeColor;
  int oppositeColor = Color::opposite(myColor);
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

  assert(std::abs(value) < Value::CHECKMATE_THRESHOLD);
  return value;
}

int Evaluation::evaluateMaterial(int color, Position& position) {
  assert(Color::isValid(color));

  int material = position.material[color];

  // Add bonus for bishop pair
  if (position.pieces[color][PieceType::BISHOP].size() >= 2) {
    material += 50;
  }

  return material;
}

int Evaluation::evaluateMobility(int color, Position& position) {
  assert(Color::isValid(color));

  int knightMobility = 0;
  for (auto squares = position.pieces[color][PieceType::KNIGHT].squares; squares != 0; squares = Bitboard::remainder(squares)) {
    int square = Bitboard::next(squares);
    knightMobility += evaluateMobility(color, position, square, Square::knightDirections);
  }

  int bishopMobility = 0;
  for (auto squares = position.pieces[color][PieceType::BISHOP].squares; squares != 0; squares = Bitboard::remainder(squares)) {
    int square = Bitboard::next(squares);
    bishopMobility += evaluateMobility(color, position, square, Square::bishopDirections);
  }

  int rookMobility = 0;
  for (auto squares = position.pieces[color][PieceType::ROOK].squares; squares != 0; squares = Bitboard::remainder(squares)) {
    int square = Bitboard::next(squares);
    rookMobility += evaluateMobility(color, position, square, Square::rookDirections);
  }

  int queenMobility = 0;
  for (auto squares = position.pieces[color][PieceType::QUEEN].squares; squares != 0; squares = Bitboard::remainder(squares)) {
    int square = Bitboard::next(squares);
    queenMobility += evaluateMobility(color, position, square, Square::queenDirections);
  }

  return knightMobility * 4
      + bishopMobility * 5
      + rookMobility * 2
      + queenMobility;
}

int Evaluation::evaluateMobility(int color, Position& position, int square, const std::vector<int>& directions) {
  assert(Color::isValid(color));
  assert(Piece::isValid(position.board[square]));

  int mobility = 0;
  bool sliding = PieceType::isSliding(Piece::getType(position.board[square]));

  for (auto direction : directions) {
    int targetSquare = square + direction;

    while (Square::isValid(targetSquare)) {
      ++mobility;

      if (sliding && position.board[targetSquare] == Piece::NOPIECE) {
        targetSquare += direction;
      } else {
        break;
      }
    }
  }

  return mobility;
}

}
