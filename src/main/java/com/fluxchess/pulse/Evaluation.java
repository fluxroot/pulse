/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

final class Evaluation {

  static final int TEMPO = 1;

  static int materialWeight = 100;
  static int mobilityWeight = 80;
  private static final int MAX_WEIGHT = 100;

  /**
   * Evaluates the board.
   *
   * @param board the board.
   * @return the evaluation value in centipawns.
   */
  int evaluate(Board board) {
    assert board != null;

    // Initialize
    int myColor = board.activeColor;
    int oppositeColor = Color.opposite(myColor);
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
    if (value <= -Value.CHECKMATE_THRESHOLD || value >= Value.CHECKMATE_THRESHOLD) {
      assert false;
    }

    return value;
  }

  private int evaluateMaterial(int color, Board board) {
    assert Color.isValid(color);
    assert board != null;

    int material = board.material[color];

    // Add bonus for bishop pair
    if (board.bishops[color].size() >= 2) {
      material += 50;
    }

    return material;
  }

  private int evaluateMobility(int color, Board board) {
    assert Color.isValid(color);
    assert board != null;

    int knightMobility = 0;
    for (long squares = board.knights[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      knightMobility += evaluateMobility(color, board, square, Board.knightDirections);
    }

    int bishopMobility = 0;
    for (long squares = board.bishops[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      bishopMobility += evaluateMobility(color, board, square, Board.bishopDirections);
    }

    int rookMobility = 0;
    for (long squares = board.rooks[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      rookMobility += evaluateMobility(color, board, square, Board.rookDirections);
    }

    int queenMobility = 0;
    for (long squares = board.queens[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      queenMobility += evaluateMobility(color, board, square, Board.queenDirections);
    }

    return knightMobility * 4
        + bishopMobility * 5
        + rookMobility * 2
        + queenMobility;
  }

  private int evaluateMobility(int color, Board board, int square, int[] moveDelta) {
    assert Color.isValid(color);
    assert board != null;
    assert Piece.isValid(board.board[square]);
    assert moveDelta != null;

    int mobility = 0;
    boolean sliding = PieceType.isSliding(Piece.getType(board.board[square]));

    for (int delta : moveDelta) {
      int targetSquare = square + delta;

      while (Square.isValid(targetSquare)) {
        ++mobility;

        if (sliding && board.board[targetSquare] == Piece.NOPIECE) {
          targetSquare += delta;
        } else {
          break;
        }
      }
    }

    return mobility;
  }

}
