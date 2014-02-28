/*
 * Copyright 2013-2014 the original author or authors.
 *
 * This file is part of Pulse Chess.
 *
 * Pulse Chess is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pulse Chess is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pulse Chess.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxchess.pulse;

final class Evaluation {

  public static final int INFINITY = 200000;
  public static final int DRAW = 0;
  public static final int CHECKMATE = 100000;
  public static final int CHECKMATE_THRESHOLD = CHECKMATE - Search.MAX_PLY;

  public static final int VALUE_PAWN = 100;
  public static final int VALUE_KNIGHT = 325;
  public static final int VALUE_BISHOP = 325;
  public static final int VALUE_ROOK = 500;
  public static final int VALUE_QUEEN = 975;
  public static final int VALUE_KING = 20000;

  /**
   * Evaluates the board.
   *
   * @param board the board.
   * @return the evaluation value in centipawns.
   */
  public int evaluate(Board board) {
    assert board != null;

    // Initialize
    int myColor = board.activeColor;
    int oppositeColor = Color.opposite(myColor);
    int total = 0;

    // Evaluate material
    int myMaterial = VALUE_PAWN * board.pawns[myColor].size()
        + VALUE_KNIGHT * board.knights[myColor].size()
        + VALUE_BISHOP * board.bishops[myColor].size()
        + VALUE_ROOK * board.rooks[myColor].size()
        + VALUE_QUEEN * board.queens[myColor].size()
        + VALUE_KING * board.kings[myColor].size();
    int oppositeMaterial = VALUE_PAWN * board.pawns[oppositeColor].size()
        + VALUE_KNIGHT * board.knights[oppositeColor].size()
        + VALUE_BISHOP * board.bishops[oppositeColor].size()
        + VALUE_ROOK * board.rooks[oppositeColor].size()
        + VALUE_QUEEN * board.queens[oppositeColor].size()
        + VALUE_KING * board.kings[oppositeColor].size();
    total += myMaterial - oppositeMaterial;

    // This is just a safe guard to protect against overflow in our evaluation
    // function.
    if (total <= -CHECKMATE_THRESHOLD) {
      total = -CHECKMATE_THRESHOLD + 1;
    } else if (total >= CHECKMATE_THRESHOLD) {
      total = CHECKMATE_THRESHOLD - 1;
    }

    return total;
  }

  public static int getPieceTypeValue(int pieceType) {
    assert Piece.Type.isValid(pieceType);

    switch (pieceType) {
      case Piece.Type.PAWN:
        return VALUE_PAWN;
      case Piece.Type.KNIGHT:
        return VALUE_KNIGHT;
      case Piece.Type.BISHOP:
        return VALUE_BISHOP;
      case Piece.Type.ROOK:
        return VALUE_ROOK;
      case Piece.Type.QUEEN:
        return VALUE_QUEEN;
      case Piece.Type.KING:
        return VALUE_KING;
      default:
        throw new IllegalArgumentException();
    }
  }

}
