/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.jetbrains.annotations.NotNull;

import static com.fluxchess.pulse.Bitboard.next;
import static com.fluxchess.pulse.Bitboard.remainder;
import static com.fluxchess.pulse.Color.opposite;
import static com.fluxchess.pulse.Piece.NOPIECE;
import static com.fluxchess.pulse.PieceType.BISHOP;
import static com.fluxchess.pulse.PieceType.KNIGHT;
import static com.fluxchess.pulse.PieceType.QUEEN;
import static com.fluxchess.pulse.PieceType.ROOK;
import static com.fluxchess.pulse.PieceType.isSliding;
import static com.fluxchess.pulse.Square.bishopDirections;
import static com.fluxchess.pulse.Square.knightDirections;
import static com.fluxchess.pulse.Square.queenDirections;
import static com.fluxchess.pulse.Square.rookDirections;
import static com.fluxchess.pulse.Value.CHECKMATE_THRESHOLD;
import static java.lang.Math.abs;

final class Evaluation {

  static final int TEMPO = 1;

  static int materialWeight = 100;
  static int mobilityWeight = 80;
  private static final int MAX_WEIGHT = 100;

  /**
   * Evaluates the position.
   *
   * @param position the position.
   * @return the evaluation value in centipawns.
   */
  int evaluate(@NotNull Position position) {
    // Initialize
    int myColor = position.activeColor;
    int oppositeColor = opposite(myColor);
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

    assert abs(value) < CHECKMATE_THRESHOLD;
    return value;
  }

  private int evaluateMaterial(int color, @NotNull Position position) {
    assert Color.isValid(color);

    int material = position.material[color];

    // Add bonus for bishop pair
    if (position.pieces[color][BISHOP].size() >= 2) {
      material += 50;
    }

    return material;
  }

  private int evaluateMobility(int color, @NotNull Position position) {
    assert Color.isValid(color);

    int knightMobility = 0;
    for (long squares = position.pieces[color][KNIGHT].squares; squares != 0; squares = remainder(squares)) {
      int square = next(squares);
      knightMobility += evaluateMobility(color, position, square, knightDirections);
    }

    int bishopMobility = 0;
    for (long squares = position.pieces[color][BISHOP].squares; squares != 0; squares = remainder(squares)) {
      int square = next(squares);
      bishopMobility += evaluateMobility(color, position, square, bishopDirections);
    }

    int rookMobility = 0;
    for (long squares = position.pieces[color][ROOK].squares; squares != 0; squares = remainder(squares)) {
      int square = next(squares);
      rookMobility += evaluateMobility(color, position, square, rookDirections);
    }

    int queenMobility = 0;
    for (long squares = position.pieces[color][QUEEN].squares; squares != 0; squares = remainder(squares)) {
      int square = next(squares);
      queenMobility += evaluateMobility(color, position, square, queenDirections);
    }

    return knightMobility * 4
        + bishopMobility * 5
        + rookMobility * 2
        + queenMobility;
  }

  private int evaluateMobility(int color, @NotNull Position position, int square, @NotNull int[] directions) {
    assert Color.isValid(color);
    assert Piece.isValid(position.board[square]);

    int mobility = 0;
    boolean sliding = isSliding(Piece.getType(position.board[square]));

    for (int direction : directions) {
      int targetSquare = square + direction;

      while (Square.isValid(targetSquare)) {
        ++mobility;

        if (sliding && position.board[targetSquare] == NOPIECE) {
          targetSquare += direction;
        } else {
          break;
        }
      }
    }

    return mobility;
  }

}
