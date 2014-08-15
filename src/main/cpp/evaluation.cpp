/*
 * Copyright (C) 2013-2014 Phokham Nonava
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
 * Evaluates the board.
 *
 * @param board the board.
 * @return the evaluation value in centipawns.
 */
int Evaluation::evaluate(Board& board) {
  // Initialize
  int myColor = board.activeColor;
  int oppositeColor = Color::opposite(myColor);
  int value = 0;

  // Evaluate material
  int materialScore = (evaluateMaterial(myColor, board) - evaluateMaterial(oppositeColor, board))
      * materialWeight / MAX_WEIGHT;
  value += materialScore;

  // Evaluate mobility
  int mobilityScore = (evaluateMobility(myColor, board) - evaluateMobility(oppositeColor, board))
      * mobilityWeight / MAX_WEIGHT;
  value += mobilityScore;

  // Add Tempo
  value += TEMPO;

  // This is just a safe guard to protect against overflow in our evaluation
  // function.
  if (value <= -Value::CHECKMATE_THRESHOLD || value >= Value::CHECKMATE_THRESHOLD) {
    assert(false);
  }

  return value;
}

int Evaluation::evaluateMaterial(int color, Board& board) {
  assert(Color::isValid(color));

  int material = board.material[color];

  // Add bonus for bishop pair
  if (board.bishops[color].size() >= 2) {
    material += 50;
  }

  return material;
}

int Evaluation::evaluateMobility(int color, Board& board) {
  assert(Color::isValid(color));

  int knightMobility = 0;
  for (auto squares = board.knights[color].squares; squares != 0; squares &= squares - 1) {
    int square = Bitboard::next(squares);
    knightMobility += evaluateMobility(color, board, square, Board::knightDirections);
  }

  int bishopMobility = 0;
  for (auto squares = board.bishops[color].squares; squares != 0; squares &= squares - 1) {
    int square = Bitboard::next(squares);
    bishopMobility += evaluateMobility(color, board, square, Board::bishopDirections);
  }

  int rookMobility = 0;
  for (auto squares = board.rooks[color].squares; squares != 0; squares &= squares - 1) {
    int square = Bitboard::next(squares);
    rookMobility += evaluateMobility(color, board, square, Board::rookDirections);
  }

  int queenMobility = 0;
  for (auto squares = board.queens[color].squares; squares != 0; squares &= squares - 1) {
    int square = Bitboard::next(squares);
    queenMobility += evaluateMobility(color, board, square, Board::queenDirections);
  }

  return knightMobility * 4
      + bishopMobility * 5
      + rookMobility * 2
      + queenMobility;
}

int Evaluation::evaluateMobility(int color, Board& board, int square, const std::vector<int>& moveDelta) {
  assert(Color::isValid(color));
  assert(Piece::isValid(board.board[square]));

  int mobility = 0;
  bool sliding = PieceType::isSliding(Piece::getType(board.board[square]));

  for (auto delta : moveDelta) {
    int targetSquare = square + delta;

    while (Square::isValid(targetSquare)) {
      ++mobility;

      if (sliding && board.board[targetSquare] == Piece::NOPIECE) {
        targetSquare += delta;
      } else {
        break;
      }
    }
  }

  return mobility;
}

}
