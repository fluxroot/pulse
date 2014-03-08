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

/**
 * This class encodes piece information as an int value. The data is
 * encoded as follows:<br/>
 * <br/>
 * <code>Bit 0 - 2</code>: the type (required)<br/>
 * <code>Bit 3 - 4</code>: the color (required)<br/>
 */
final class Piece {

  /**
   * This class encodes type information as an int value. The data is
   * encoded as follows:<br/>
   * <br/>
   * <code>Bit 0 - 2</code>: the type (required)<br/>
   */
  static final class Type {
    static final int MASK = 0x7;

    static final int PAWN = 0;
    static final int KNIGHT = 1;
    static final int BISHOP = 2;
    static final int ROOK = 3;
    static final int QUEEN = 4;
    static final int KING = 5;
    static final int NOTYPE = 6;

    static final int[] values = {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
    };

    static final int[] promotions = {
        KNIGHT, BISHOP, ROOK, QUEEN
    };

    private Type() {
    }

    static int valueOf(GenericChessman genericChessman) {
      assert genericChessman != null;

      switch (genericChessman) {
        case PAWN:
          return PAWN;
        case KNIGHT:
          return KNIGHT;
        case BISHOP:
          return BISHOP;
        case ROOK:
          return ROOK;
        case QUEEN:
          return QUEEN;
        case KING:
          return KING;
        default:
          throw new IllegalArgumentException();
      }
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
        case NOTYPE:
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
        case NOTYPE:
          return false;
        default:
          throw new IllegalArgumentException();
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
          return false;
        case NOTYPE:
        default:
          throw new IllegalArgumentException();
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
        case NOTYPE:
        default:
          throw new IllegalArgumentException();
      }
    }
  }

  static final int MASK = 0x1F;

  static final int WHITEPAWN = 0;
  static final int WHITEKNIGHT = 1;
  static final int WHITEBISHOP = 2;
  static final int WHITEROOK = 3;
  static final int WHITEQUEEN = 4;
  static final int WHITEKING = 5;
  static final int BLACKPAWN = 6;
  static final int BLACKKNIGHT = 7;
  static final int BLACKBISHOP = 8;
  static final int BLACKROOK = 9;
  static final int BLACKQUEEN = 10;
  static final int BLACKKING = 11;
  static final int NOPIECE = 12;

  static final int[] values = {
      WHITEPAWN, WHITEKNIGHT, WHITEBISHOP, WHITEROOK, WHITEQUEEN, WHITEKING,
      BLACKPAWN, BLACKKNIGHT, BLACKBISHOP, BLACKROOK, BLACKQUEEN, BLACKKING
  };

  private Piece() {
  }

  static int valueOf(GenericPiece genericPiece) {
    assert genericPiece != null;

    switch (genericPiece) {
      case WHITEPAWN:
        return WHITEPAWN;
      case WHITEKNIGHT:
        return WHITEKNIGHT;
      case WHITEBISHOP:
        return WHITEBISHOP;
      case WHITEROOK:
        return WHITEROOK;
      case WHITEQUEEN:
        return WHITEQUEEN;
      case WHITEKING:
        return WHITEKING;
      case BLACKPAWN:
        return BLACKPAWN;
      case BLACKKNIGHT:
        return BLACKKNIGHT;
      case BLACKBISHOP:
        return BLACKBISHOP;
      case BLACKROOK:
        return BLACKROOK;
      case BLACKQUEEN:
        return BLACKQUEEN;
      case BLACKKING:
        return BLACKKING;
      default:
        throw new IllegalArgumentException();
    }
  }

  static int valueOf(int pieceType, int color) {
    switch (color) {
      case WHITE:
        switch (pieceType) {
          case Type.PAWN:
            return WHITEPAWN;
          case Type.KNIGHT:
            return WHITEKNIGHT;
          case Type.BISHOP:
            return WHITEBISHOP;
          case Type.ROOK:
            return WHITEROOK;
          case Type.QUEEN:
            return WHITEQUEEN;
          case Type.KING:
            return WHITEKING;
          case Type.NOTYPE:
          default:
            throw new IllegalArgumentException();
        }
      case BLACK:
        switch (pieceType) {
          case Type.PAWN:
            return BLACKPAWN;
          case Type.KNIGHT:
            return BLACKKNIGHT;
          case Type.BISHOP:
            return BLACKBISHOP;
          case Type.ROOK:
            return BLACKROOK;
          case Type.QUEEN:
            return BLACKQUEEN;
          case Type.KING:
            return BLACKKING;
          case Type.NOTYPE:
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
      case WHITEPAWN:
        return GenericPiece.WHITEPAWN;
      case WHITEKNIGHT:
        return GenericPiece.WHITEKNIGHT;
      case WHITEBISHOP:
        return GenericPiece.WHITEBISHOP;
      case WHITEROOK:
        return GenericPiece.WHITEROOK;
      case WHITEQUEEN:
        return GenericPiece.WHITEQUEEN;
      case WHITEKING:
        return GenericPiece.WHITEKING;
      case BLACKPAWN:
        return GenericPiece.BLACKPAWN;
      case BLACKKNIGHT:
        return GenericPiece.BLACKKNIGHT;
      case BLACKBISHOP:
        return GenericPiece.BLACKBISHOP;
      case BLACKROOK:
        return GenericPiece.BLACKROOK;
      case BLACKQUEEN:
        return GenericPiece.BLACKQUEEN;
      case BLACKKING:
        return GenericPiece.BLACKKING;
      case NOPIECE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static boolean isValid(int piece) {
    switch (piece) {
      case WHITEPAWN:
      case WHITEKNIGHT:
      case WHITEBISHOP:
      case WHITEROOK:
      case WHITEQUEEN:
      case WHITEKING:
      case BLACKPAWN:
      case BLACKKNIGHT:
      case BLACKBISHOP:
      case BLACKROOK:
      case BLACKQUEEN:
      case BLACKKING:
        return true;
      case NOPIECE:
        return false;
      default:
        throw new IllegalArgumentException();
    }
  }

  static int getType(int piece) {
    switch (piece) {
      case WHITEPAWN:
      case BLACKPAWN:
        return Type.PAWN;
      case WHITEKNIGHT:
      case BLACKKNIGHT:
        return Type.KNIGHT;
      case WHITEBISHOP:
      case BLACKBISHOP:
        return Type.BISHOP;
      case WHITEROOK:
      case BLACKROOK:
        return Type.ROOK;
      case WHITEQUEEN:
      case BLACKQUEEN:
        return Type.QUEEN;
      case WHITEKING:
      case BLACKKING:
        return Type.KING;
      case NOPIECE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static int getColor(int piece) {
    switch (piece) {
      case WHITEPAWN:
      case WHITEKNIGHT:
      case WHITEBISHOP:
      case WHITEROOK:
      case WHITEQUEEN:
      case WHITEKING:
        return WHITE;
      case BLACKPAWN:
      case BLACKKNIGHT:
      case BLACKBISHOP:
      case BLACKROOK:
      case BLACKQUEEN:
      case BLACKKING:
        return BLACK;
      case NOPIECE:
      default:
        throw new IllegalArgumentException();
    }
  }

}
