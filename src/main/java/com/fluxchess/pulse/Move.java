/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericMove;

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
  private static final int ORIGINSQUARE_SHIFT = 3;
  private static final int ORIGINSQUARE_MASK = Square.MASK << ORIGINSQUARE_SHIFT;
  private static final int TARGETSQUARE_SHIFT = 10;
  private static final int TARGETSQUARE_MASK = Square.MASK << TARGETSQUARE_SHIFT;
  private static final int ORIGINPIECE_SHIFT = 17;
  private static final int ORIGINPIECE_MASK = Piece.MASK << ORIGINPIECE_SHIFT;
  private static final int TARGETPIECE_SHIFT = 22;
  private static final int TARGETPIECE_MASK = Piece.MASK << TARGETPIECE_SHIFT;
  private static final int PROMOTION_SHIFT = 27;
  private static final int PROMOTION_MASK = PieceType.MASK << PROMOTION_SHIFT;

  // We don't use 0 as a null value to protect against errors.
  static final int NOMOVE = (MoveType.NOMOVETYPE << TYPE_SHIFT)
      | (Square.NOSQUARE << ORIGINSQUARE_SHIFT)
      | (Square.NOSQUARE << TARGETSQUARE_SHIFT)
      | (Piece.NOPIECE << ORIGINPIECE_SHIFT)
      | (Piece.NOPIECE << TARGETPIECE_SHIFT)
      | (PieceType.NOPIECETYPE << PROMOTION_SHIFT);

  private Move() {
  }

  static GenericMove toGenericMove(int move) {
    int type = getType(move);
    int originSquare = getOriginSquare(move);
    int targetSquare = getTargetSquare(move);

    switch (type) {
      case MoveType.NORMAL:
      case MoveType.PAWNDOUBLE:
      case MoveType.ENPASSANT:
      case MoveType.CASTLING:
        return new GenericMove(
            Square.toGenericPosition(originSquare),
            Square.toGenericPosition(targetSquare)
        );
      case MoveType.PAWNPROMOTION:
        return new GenericMove(
            Square.toGenericPosition(originSquare),
            Square.toGenericPosition(targetSquare),
            PieceType.toGenericChessman(getPromotion(move))
        );
      default:
        throw new IllegalArgumentException();
    }
  }

  static int valueOf(int type, int originSquare, int targetSquare, int originPiece, int targetPiece, int promotion) {
    int move = 0;

    // Encode type
    assert MoveType.isValid(type);
    move |= type << TYPE_SHIFT;

    // Encode origin square
    assert Square.isValid(originSquare);
    move |= originSquare << ORIGINSQUARE_SHIFT;

    // Encode target square
    assert Square.isValid(targetSquare);
    move |= targetSquare << TARGETSQUARE_SHIFT;

    // Encode origin piece
    assert Piece.isValid(originPiece);
    move |= originPiece << ORIGINPIECE_SHIFT;

    // Encode target piece
    assert Piece.isValid(targetPiece) || targetPiece == Piece.NOPIECE;
    move |= targetPiece << TARGETPIECE_SHIFT;

    // Encode promotion
    assert PieceType.isValidPromotion(promotion) || promotion == PieceType.NOPIECETYPE;
    move |= promotion << PROMOTION_SHIFT;

    return move;
  }

  static int getType(int move) {
    int type = (move & TYPE_MASK) >>> TYPE_SHIFT;
    assert MoveType.isValid(type);

    return type;
  }

  static int getOriginSquare(int move) {
    int originSquare = (move & ORIGINSQUARE_MASK) >>> ORIGINSQUARE_SHIFT;
    assert Square.isValid(originSquare);

    return originSquare;
  }

  static int getTargetSquare(int move) {
    int targetSquare = (move & TARGETSQUARE_MASK) >>> TARGETSQUARE_SHIFT;
    assert Square.isValid(targetSquare);

    return targetSquare;
  }

  static int getOriginPiece(int move) {
    int originPiece = (move & ORIGINPIECE_MASK) >>> ORIGINPIECE_SHIFT;
    assert Piece.isValid(originPiece);

    return originPiece;
  }

  static int getTargetPiece(int move) {
    int targetPiece = (move & TARGETPIECE_MASK) >>> TARGETPIECE_SHIFT;
    assert Piece.isValid(targetPiece) || targetPiece == Piece.NOPIECE;

    return targetPiece;
  }

  static int getPromotion(int move) {
    int promotion = (move & PROMOTION_MASK) >>> PROMOTION_SHIFT;
    assert PieceType.isValidPromotion(promotion) || promotion == PieceType.NOPIECETYPE;

    return promotion;
  }

}
