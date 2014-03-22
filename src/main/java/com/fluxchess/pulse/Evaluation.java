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

  // Piece values as defined by Larry Kaufman
  static final int PAWN_VALUE = 100;
  static final int KNIGHT_VALUE = 325;
  static final int BISHOP_VALUE = 325;
  static final int ROOK_VALUE = 500;
  static final int QUEEN_VALUE = 975;
  static final int KING_VALUE = 20000;

  // If there are two minor pieces captured, we are entering the middlegame
  private static final int PHASE_OPENING_MATERIAL = 2 * KNIGHT_VALUE
      + 4 * BISHOP_VALUE
      + 4 * ROOK_VALUE
      + 2 * QUEEN_VALUE;

  // If our major/minor material is equal to a queen and a rook, we are entering the endgame
  private static final int PHASE_ENDGAME_MATERIAL = ROOK_VALUE + QUEEN_VALUE;

  // Within the phase interval we are using the tapered eval to make a smooth transition
  // from the opening to the endgame
  private static final int PHASE_INTERVAL_MATERIAL = PHASE_OPENING_MATERIAL - PHASE_ENDGAME_MATERIAL;

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
    int opening = 0;
    int endgame = 0;

    // Evaluate material
    int materialScore = (evaluateMaterial(myColor, board) - evaluateMaterial(oppositeColor, board))
        * materialWeight / MAX_WEIGHT;
    opening += materialScore;
    endgame += materialScore;

    // Evaluate mobility
    int mobilityScore = (evaluateMobility(myColor, board) - evaluateMobility(oppositeColor, board))
        * mobilityWeight / MAX_WEIGHT;
    opening += mobilityScore;

    // Interpolate opening and endgame
    int value = interpolate(opening, endgame, board);

    // This is just a safe guard to protect against overflow in our evaluation
    // function.
    if (value <= -CHECKMATE_THRESHOLD) {
      assert false;
      value = -CHECKMATE_THRESHOLD + 1;
    } else if (value >= CHECKMATE_THRESHOLD) {
      assert false;
      value = CHECKMATE_THRESHOLD - 1;
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
        return PAWN_VALUE;
      case Piece.Type.KNIGHT:
        return KNIGHT_VALUE;
      case Piece.Type.BISHOP:
        return BISHOP_VALUE;
      case Piece.Type.ROOK:
        return ROOK_VALUE;
      case Piece.Type.QUEEN:
        return QUEEN_VALUE;
      case Piece.Type.KING:
        return KING_VALUE;
      default:
        throw new IllegalArgumentException();
    }
  }

  static int interpolate(int opening, int endgame, Board board) {
    assert board != null;

    int phase;
    int material = board.majorMinorMaterial[Color.WHITE] + board.majorMinorMaterial[Color.BLACK];
    if (material >= PHASE_OPENING_MATERIAL) {
      phase = PHASE_INTERVAL_MATERIAL;
    } else if (material <= PHASE_ENDGAME_MATERIAL) {
      phase = 0;
    } else {
      phase = material - PHASE_ENDGAME_MATERIAL;
    }

    return (opening * phase + endgame * (PHASE_INTERVAL_MATERIAL - phase)) / PHASE_INTERVAL_MATERIAL;
  }

}
