/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericPiece;

final class Piece {

  static final int MASK = 0x1F;

  static final int WHITE_PAWN = 0;
  static final int WHITE_KNIGHT = 1;
  static final int WHITE_BISHOP = 2;
  static final int WHITE_ROOK = 3;
  static final int WHITE_QUEEN = 4;
  static final int WHITE_KING = 5;
  static final int BLACK_PAWN = 6;
  static final int BLACK_KNIGHT = 7;
  static final int BLACK_BISHOP = 8;
  static final int BLACK_ROOK = 9;
  static final int BLACK_QUEEN = 10;
  static final int BLACK_KING = 11;
  static final int NOPIECE = 12;

  static final int[] values = {
      WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING,
      BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING
  };

  private Piece() {
  }

  static boolean isValid(int piece) {
    switch (piece) {
      case WHITE_PAWN:
      case WHITE_KNIGHT:
      case WHITE_BISHOP:
      case WHITE_ROOK:
      case WHITE_QUEEN:
      case WHITE_KING:
      case BLACK_PAWN:
      case BLACK_KNIGHT:
      case BLACK_BISHOP:
      case BLACK_ROOK:
      case BLACK_QUEEN:
      case BLACK_KING:
        return true;
      case NOPIECE:
      default:
        return false;
    }
  }

  static int valueOf(GenericPiece genericPiece) {
    assert genericPiece != null;

    switch (genericPiece) {
      case WHITEPAWN:
        return WHITE_PAWN;
      case WHITEKNIGHT:
        return WHITE_KNIGHT;
      case WHITEBISHOP:
        return WHITE_BISHOP;
      case WHITEROOK:
        return WHITE_ROOK;
      case WHITEQUEEN:
        return WHITE_QUEEN;
      case WHITEKING:
        return WHITE_KING;
      case BLACKPAWN:
        return BLACK_PAWN;
      case BLACKKNIGHT:
        return BLACK_KNIGHT;
      case BLACKBISHOP:
        return BLACK_BISHOP;
      case BLACKROOK:
        return BLACK_ROOK;
      case BLACKQUEEN:
        return BLACK_QUEEN;
      case BLACKKING:
        return BLACK_KING;
      default:
        throw new IllegalArgumentException();
    }
  }

  static int valueOf(int color, int pieceType) {
    switch (color) {
      case Color.WHITE:
        switch (pieceType) {
          case PieceType.PAWN:
            return WHITE_PAWN;
          case PieceType.KNIGHT:
            return WHITE_KNIGHT;
          case PieceType.BISHOP:
            return WHITE_BISHOP;
          case PieceType.ROOK:
            return WHITE_ROOK;
          case PieceType.QUEEN:
            return WHITE_QUEEN;
          case PieceType.KING:
            return WHITE_KING;
          case PieceType.NOPIECETYPE:
          default:
            throw new IllegalArgumentException();
        }
      case Color.BLACK:
        switch (pieceType) {
          case PieceType.PAWN:
            return BLACK_PAWN;
          case PieceType.KNIGHT:
            return BLACK_KNIGHT;
          case PieceType.BISHOP:
            return BLACK_BISHOP;
          case PieceType.ROOK:
            return BLACK_ROOK;
          case PieceType.QUEEN:
            return BLACK_QUEEN;
          case PieceType.KING:
            return BLACK_KING;
          case PieceType.NOPIECETYPE:
          default:
            throw new IllegalArgumentException();
        }
      case Color.NOCOLOR:
      default:
        throw new IllegalArgumentException();
    }
  }

  static GenericPiece toGenericPiece(int piece) {
    switch (piece) {
      case WHITE_PAWN:
        return GenericPiece.WHITEPAWN;
      case WHITE_KNIGHT:
        return GenericPiece.WHITEKNIGHT;
      case WHITE_BISHOP:
        return GenericPiece.WHITEBISHOP;
      case WHITE_ROOK:
        return GenericPiece.WHITEROOK;
      case WHITE_QUEEN:
        return GenericPiece.WHITEQUEEN;
      case WHITE_KING:
        return GenericPiece.WHITEKING;
      case BLACK_PAWN:
        return GenericPiece.BLACKPAWN;
      case BLACK_KNIGHT:
        return GenericPiece.BLACKKNIGHT;
      case BLACK_BISHOP:
        return GenericPiece.BLACKBISHOP;
      case BLACK_ROOK:
        return GenericPiece.BLACKROOK;
      case BLACK_QUEEN:
        return GenericPiece.BLACKQUEEN;
      case BLACK_KING:
        return GenericPiece.BLACKKING;
      case NOPIECE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static int getType(int piece) {
    switch (piece) {
      case WHITE_PAWN:
      case BLACK_PAWN:
        return PieceType.PAWN;
      case WHITE_KNIGHT:
      case BLACK_KNIGHT:
        return PieceType.KNIGHT;
      case WHITE_BISHOP:
      case BLACK_BISHOP:
        return PieceType.BISHOP;
      case WHITE_ROOK:
      case BLACK_ROOK:
        return PieceType.ROOK;
      case WHITE_QUEEN:
      case BLACK_QUEEN:
        return PieceType.QUEEN;
      case WHITE_KING:
      case BLACK_KING:
        return PieceType.KING;
      case NOPIECE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static int getColor(int piece) {
    switch (piece) {
      case WHITE_PAWN:
      case WHITE_KNIGHT:
      case WHITE_BISHOP:
      case WHITE_ROOK:
      case WHITE_QUEEN:
      case WHITE_KING:
        return Color.WHITE;
      case BLACK_PAWN:
      case BLACK_KNIGHT:
      case BLACK_BISHOP:
      case BLACK_ROOK:
      case BLACK_QUEEN:
      case BLACK_KING:
        return Color.BLACK;
      case NOPIECE:
      default:
        throw new IllegalArgumentException();
    }
  }

}
