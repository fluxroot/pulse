/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

final class Evaluation {

  static final int INFINITE = 200000;
  static final int CHECKMATE = 100000;
  static final int CHECKMATE_THRESHOLD = CHECKMATE - Search.MAX_PLY;
  static final int DRAW = 0;
  static final int NOVALUE = 300000;

  // Piece values as defined by Larry Kaufman
  static final int PAWN_VALUE = 100;
  static final int KNIGHT_VALUE = 325;
  static final int BISHOP_VALUE = 325;
  static final int ROOK_VALUE = 500;
  static final int QUEEN_VALUE = 975;
  static final int KING_VALUE = 20000;

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

    int knightMobility = 0;
    for (long squares = board.knights[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      knightMobility += evaluateMobility(color, board, square, MoveGenerator.moveDeltaKnight);
    }

    int bishopMobility = 0;
    for (long squares = board.bishops[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      bishopMobility += evaluateMobility(color, board, square, MoveGenerator.moveDeltaBishop);
    }

    int rookMobility = 0;
    for (long squares = board.rooks[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      rookMobility += evaluateMobility(color, board, square, MoveGenerator.moveDeltaRook);
    }

    int queenMobility = 0;
    for (long squares = board.queens[color].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      queenMobility += evaluateMobility(color, board, square, MoveGenerator.moveDeltaQueen);
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

  static int getPieceTypeValue(int pieceType) {
    assert PieceType.isValid(pieceType);

    switch (pieceType) {
      case PieceType.PAWN:
        return PAWN_VALUE;
      case PieceType.KNIGHT:
        return KNIGHT_VALUE;
      case PieceType.BISHOP:
        return BISHOP_VALUE;
      case PieceType.ROOK:
        return ROOK_VALUE;
      case PieceType.QUEEN:
        return QUEEN_VALUE;
      case PieceType.KING:
        return KING_VALUE;
      default:
        throw new IllegalArgumentException();
    }
  }

}
