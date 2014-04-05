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

import com.fluxchess.jcpi.models.GenericChessman;
import com.fluxchess.jcpi.models.GenericPiece;

import static com.fluxchess.pulse.Color.BLACK;
import static com.fluxchess.pulse.Color.WHITE;

final class Piece {

  static final class Type {
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

    private Type() {
    }

    static int valueOfPromotion(GenericChessman genericChessman) {
      assert genericChessman != null;

      switch (genericChessman) {
        case KNIGHT:
          return KNIGHT;
        case BISHOP:
          return BISHOP;
        case ROOK:
          return ROOK;
        case QUEEN:
          return QUEEN;
        case PAWN:
        case KING:
        default:
          throw new IllegalArgumentException();
      }
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

  static int valueOf(int pieceType, int color) {
    switch (color) {
      case WHITE:
        switch (pieceType) {
          case Type.PAWN:
            return WHITE_PAWN;
          case Type.KNIGHT:
            return WHITE_KNIGHT;
          case Type.BISHOP:
            return WHITE_BISHOP;
          case Type.ROOK:
            return WHITE_ROOK;
          case Type.QUEEN:
            return WHITE_QUEEN;
          case Type.KING:
            return WHITE_KING;
          case Type.NOPIECETYPE:
          default:
            throw new IllegalArgumentException();
        }
      case BLACK:
        switch (pieceType) {
          case Type.PAWN:
            return BLACK_PAWN;
          case Type.KNIGHT:
            return BLACK_KNIGHT;
          case Type.BISHOP:
            return BLACK_BISHOP;
          case Type.ROOK:
            return BLACK_ROOK;
          case Type.QUEEN:
            return BLACK_QUEEN;
          case Type.KING:
            return BLACK_KING;
          case Type.NOPIECETYPE:
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

  static int getType(int piece) {
    switch (piece) {
      case WHITE_PAWN:
      case BLACK_PAWN:
        return Type.PAWN;
      case WHITE_KNIGHT:
      case BLACK_KNIGHT:
        return Type.KNIGHT;
      case WHITE_BISHOP:
      case BLACK_BISHOP:
        return Type.BISHOP;
      case WHITE_ROOK:
      case BLACK_ROOK:
        return Type.ROOK;
      case WHITE_QUEEN:
      case BLACK_QUEEN:
        return Type.QUEEN;
      case WHITE_KING:
      case BLACK_KING:
        return Type.KING;
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
        return WHITE;
      case BLACK_PAWN:
      case BLACK_KNIGHT:
      case BLACK_BISHOP:
      case BLACK_ROOK:
      case BLACK_QUEEN:
      case BLACK_KING:
        return BLACK;
      case NOPIECE:
      default:
        throw new IllegalArgumentException();
    }
  }

}
