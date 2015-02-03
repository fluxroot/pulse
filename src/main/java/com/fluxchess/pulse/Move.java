/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import static com.fluxchess.pulse.MoveType.NOMOVETYPE;
import static com.fluxchess.pulse.Piece.NOPIECE;
import static com.fluxchess.pulse.PieceType.NOPIECETYPE;
import static com.fluxchess.pulse.Square.NOSQUARE;

/**
 * This class represents a move as a int value. The fields are represented by
 * the following bits.
 * <p/>
 * <code> 0 -  2</code>: type (required)
 * <code> 3 -  9</code>: origin square (required)
 * <code>10 - 16</code>: target square (required)
 * <code>17 - 21</code>: origin piece (required)
 * <code>22 - 26</code>: target piece (optional)
 * <code>27 - 29</code>: promotion type (optional)
 */
final class Move {

  // These are our bit masks
  private static final int TYPE_SHIFT = 0;
  private static final int TYPE_MASK = MoveType.MASK << TYPE_SHIFT;
  private static final int ORIGIN_SQUARE_SHIFT = 3;
  private static final int ORIGIN_SQUARE_MASK = Square.MASK << ORIGIN_SQUARE_SHIFT;
  private static final int TARGET_SQUARE_SHIFT = 10;
  private static final int TARGET_SQUARE_MASK = Square.MASK << TARGET_SQUARE_SHIFT;
  private static final int ORIGIN_PIECE_SHIFT = 17;
  private static final int ORIGIN_PIECE_MASK = Piece.MASK << ORIGIN_PIECE_SHIFT;
  private static final int TARGET_PIECE_SHIFT = 22;
  private static final int TARGET_PIECE_MASK = Piece.MASK << TARGET_PIECE_SHIFT;
  private static final int PROMOTION_SHIFT = 27;
  private static final int PROMOTION_MASK = PieceType.MASK << PROMOTION_SHIFT;

  // We don't use 0 as a null value to protect against errors.
  static final int NOMOVE = (NOMOVETYPE << TYPE_SHIFT)
      | (NOSQUARE << ORIGIN_SQUARE_SHIFT)
      | (NOSQUARE << TARGET_SQUARE_SHIFT)
      | (NOPIECE << ORIGIN_PIECE_SHIFT)
      | (NOPIECE << TARGET_PIECE_SHIFT)
      | (NOPIECETYPE << PROMOTION_SHIFT);

  private Move() {
  }

  static int valueOf(int type, int originSquare, int targetSquare, int originPiece, int targetPiece, int promotion) {
    int move = 0;

    // Encode type
    assert MoveType.isValid(type);
    move |= type << TYPE_SHIFT;

    // Encode origin square
    assert Square.isValid(originSquare);
    move |= originSquare << ORIGIN_SQUARE_SHIFT;

    // Encode target square
    assert Square.isValid(targetSquare);
    move |= targetSquare << TARGET_SQUARE_SHIFT;

    // Encode origin piece
    assert Piece.isValid(originPiece);
    move |= originPiece << ORIGIN_PIECE_SHIFT;

    // Encode target piece
    assert Piece.isValid(targetPiece) || targetPiece == NOPIECE;
    move |= targetPiece << TARGET_PIECE_SHIFT;

    // Encode promotion
    assert PieceType.isValidPromotion(promotion) || promotion == NOPIECETYPE;
    move |= promotion << PROMOTION_SHIFT;

    return move;
  }

  static int getType(int move) {
    int type = (move & TYPE_MASK) >>> TYPE_SHIFT;
    assert MoveType.isValid(type);

    return type;
  }

  static int getOriginSquare(int move) {
    int originSquare = (move & ORIGIN_SQUARE_MASK) >>> ORIGIN_SQUARE_SHIFT;
    assert Square.isValid(originSquare);

    return originSquare;
  }

  static int getTargetSquare(int move) {
    int targetSquare = (move & TARGET_SQUARE_MASK) >>> TARGET_SQUARE_SHIFT;
    assert Square.isValid(targetSquare);

    return targetSquare;
  }

  static int getOriginPiece(int move) {
    int originPiece = (move & ORIGIN_PIECE_MASK) >>> ORIGIN_PIECE_SHIFT;
    assert Piece.isValid(originPiece);

    return originPiece;
  }

  static int getTargetPiece(int move) {
    int targetPiece = (move & TARGET_PIECE_MASK) >>> TARGET_PIECE_SHIFT;
    assert Piece.isValid(targetPiece) || targetPiece == NOPIECE;

    return targetPiece;
  }

  static int getPromotion(int move) {
    int promotion = (move & PROMOTION_MASK) >>> PROMOTION_SHIFT;
    assert PieceType.isValidPromotion(promotion) || promotion == NOPIECETYPE;

    return promotion;
  }

}
