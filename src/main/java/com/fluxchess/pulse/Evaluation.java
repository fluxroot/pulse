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

  static final int INFINITY = 200000;
  static final int DRAW = 0;
  static final int CHECKMATE = 100000;
  static final int CHECKMATE_THRESHOLD = CHECKMATE - Search.MAX_PLY;

  static final int VALUE_PAWN = 100;
  static final int VALUE_KNIGHT = 325;
  static final int VALUE_BISHOP = 325;
  static final int VALUE_ROOK = 500;
  static final int VALUE_QUEEN = 975;
  static final int VALUE_KING = 20000;

  static int materialWeight = 100;
  static int mobilityWeight = 80;
  static final int MAX_WEIGHT = 100;

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
    int total = 0;

    // Evaluate material
    total += (evaluateMaterial(myColor, board) - evaluateMaterial(oppositeColor, board))
        * materialWeight / MAX_WEIGHT;

    // Evaluate mobility
    total += (evaluateMobility(myColor, board) - evaluateMobility(oppositeColor, board))
        * mobilityWeight / MAX_WEIGHT;

    // This is just a safe guard to protect against overflow in our evaluation
    // function.
    if (total <= -CHECKMATE_THRESHOLD) {
      assert false;
      total = -CHECKMATE_THRESHOLD + 1;
    } else if (total >= CHECKMATE_THRESHOLD) {
      assert false;
      total = CHECKMATE_THRESHOLD - 1;
    }

    return total;
  }

  private int evaluateMaterial(int color, Board board) {
    assert Color.isValid(color);
    assert board != null;

    int material = VALUE_PAWN * board.pawns[color].size()
        + VALUE_KNIGHT * board.knights[color].size()
        + VALUE_BISHOP * board.bishops[color].size()
        + VALUE_ROOK * board.rooks[color].size()
        + VALUE_QUEEN * board.queens[color].size()
        + VALUE_KING * board.kings[color].size();

    // Add bonus for bishop pair
    if (board.bishops[color].size() >= 2) {
      material += 50;
    }

    return material;
  }

  private int evaluateMobility(int color, Board board) {
    assert Color.isValid(color);
    assert board != null;

    int mobility = 0;

    for (long squares = board.knights[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      mobility += evaluateMobility(color, board, square, MoveGenerator.moveDeltaKnight);
    }
    for (long squares = board.bishops[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      mobility += evaluateMobility(color, board, square, MoveGenerator.moveDeltaBishop);
    }
    for (long squares = board.rooks[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      mobility += evaluateMobility(color, board, square, MoveGenerator.moveDeltaRook);
    }
    for (long squares = board.queens[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      mobility += evaluateMobility(color, board, square, MoveGenerator.moveDeltaQueen);
    }

    return mobility;
  }

  private int evaluateMobility(int color, Board board, int square, int[] moveDelta) {
    assert Color.isValid(color);
    assert board != null;
    assert Piece.isValid(board.board[square]);
    assert moveDelta != null;

    int mobility = 0;

    boolean sliding = Piece.Type.isSliding(Piece.getType(board.board[square]));
    int oppositeColor = Color.opposite(color);

    for (int delta : moveDelta) {
      int targetSquare = square + delta;

      while (Square.isLegal(targetSquare)) {
        int targetPiece = board.board[targetSquare];

        if (targetPiece == Piece.NOPIECE) {
          ++mobility;

          if (!sliding) {
            break;
          }

          targetSquare += delta;
        } else {
          if (Piece.getColor(targetPiece) == oppositeColor) {
            ++mobility;
          }

          break;
        }
      }
    }

    return mobility;
  }

  static int getPieceTypeValue(int pieceType) {
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
