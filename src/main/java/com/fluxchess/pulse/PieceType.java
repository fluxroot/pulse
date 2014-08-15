/*
 * Copyright (C) 2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericChessman;

final class PieceType {

  static final int MASK = 0x7;

  static final int PAWN = 0;
  static final int KNIGHT = 1;
  static final int BISHOP = 2;
  static final int ROOK = 3;
  static final int QUEEN = 4;
  static final int KING = 5;
  static final int NOPIECETYPE = 6;

  static final int[] values = {
      PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
  };

  private PieceType() {
  }

  static GenericChessman toGenericChessman(int pieceType) {
    switch (pieceType) {
      case PAWN:
        return GenericChessman.PAWN;
      case KNIGHT:
        return GenericChessman.KNIGHT;
      case BISHOP:
        return GenericChessman.BISHOP;
      case ROOK:
        return GenericChessman.ROOK;
      case QUEEN:
        return GenericChessman.QUEEN;
      case KING:
        return GenericChessman.KING;
      case NOPIECETYPE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static boolean isValid(int pieceType) {
    switch (pieceType) {
      case PAWN:
      case KNIGHT:
      case BISHOP:
      case ROOK:
      case QUEEN:
      case KING:
        return true;
      case NOPIECETYPE:
      default:
        return false;
    }
  }

  static boolean isValidPromotion(int pieceType) {
    switch (pieceType) {
      case KNIGHT:
      case BISHOP:
      case ROOK:
      case QUEEN:
        return true;
      case PAWN:
      case KING:
      case NOPIECETYPE:
      default:
        return false;
    }
  }

  static boolean isSliding(int pieceType) {
    switch (pieceType) {
      case BISHOP:
      case ROOK:
      case QUEEN:
        return true;
      case PAWN:
      case KNIGHT:
      case KING:
        return false;
      case NOPIECETYPE:
      default:
        throw new IllegalArgumentException();
    }
  }

}
