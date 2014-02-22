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

/**
 * This class encodes piece information as an int value. The data is
 * encoded as follows:<br/>
 * <br/>
 * <code>Bit 0 - 2</code>: the type (required)<br/>
 * <code>Bit 3 - 4</code>: the color (required)<br/>
 */
public final class Piece {

  /**
   * This class encodes type information as an int value. The data is
   * encoded as follows:<br/>
   * <br/>
   * <code>Bit 0 - 2</code>: the type (required)<br/>
   */
  public static final class Type {
    public static final int MASK = 0x7;

    public static final int PAWN = 0;
    public static final int KNIGHT = 1;
    public static final int BISHOP = 2;
    public static final int ROOK = 3;
    public static final int QUEEN = 4;
    public static final int KING = 5;
    public static final int NOTYPE = 6;

    public static final int[] values = {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
    };

    public static final int[] promotions = {
        KNIGHT, BISHOP, ROOK, QUEEN
    };

    private Type() {
    }

    public static int valueOf(GenericChessman genericChessman) {
      if (genericChessman == null) throw new IllegalArgumentException();

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

    public static int valueOfPromotion(GenericChessman genericChessman) {
      if (genericChessman == null) throw new IllegalArgumentException();

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

    public static GenericChessman toGenericChessman(int pieceType) {
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

    public static boolean isValid(int pieceType) {
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

    public static boolean isValidPromotion(int pieceType) {
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

    public static boolean isSliding(int pieceType) {
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

  public static final int MASK = 0x1F;

  private static final int TYPE_SHIFT = 0;
  private static final int TYPE_MASK = Type.MASK << TYPE_SHIFT;
  private static final int COLOR_SHIFT = 3;
  private static final int COLOR_MASK = Color.MASK << COLOR_SHIFT;

  public static final int NOPIECE = (Type.NOTYPE << TYPE_SHIFT) | (Color.NOCOLOR << COLOR_SHIFT);

  public static final int WHITEPAWN = (Type.PAWN << TYPE_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int WHITEKNIGHT = (Type.KNIGHT << TYPE_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int WHITEBISHOP = (Type.BISHOP << TYPE_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int WHITEROOK = (Type.ROOK << TYPE_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int WHITEQUEEN = (Type.QUEEN << TYPE_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int WHITEKING = (Type.KING << TYPE_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int BLACKPAWN = (Type.PAWN << TYPE_SHIFT) | (Color.BLACK << COLOR_SHIFT);
  public static final int BLACKKNIGHT = (Type.KNIGHT << TYPE_SHIFT) | (Color.BLACK << COLOR_SHIFT);
  public static final int BLACKBISHOP = (Type.BISHOP << TYPE_SHIFT) | (Color.BLACK << COLOR_SHIFT);
  public static final int BLACKROOK = (Type.ROOK << TYPE_SHIFT) | (Color.BLACK << COLOR_SHIFT);
  public static final int BLACKQUEEN = (Type.QUEEN << TYPE_SHIFT) | (Color.BLACK << COLOR_SHIFT);
  public static final int BLACKKING = (Type.KING << TYPE_SHIFT) | (Color.BLACK << COLOR_SHIFT);

  public static final int[] values = {
      WHITEPAWN, WHITEKNIGHT, WHITEBISHOP, WHITEROOK, WHITEQUEEN, WHITEKING,
      BLACKPAWN, BLACKKNIGHT, BLACKBISHOP, BLACKROOK, BLACKQUEEN, BLACKKING
  };

  private Piece() {
  }

  public static int valueOf(GenericPiece genericPiece) {
    if (genericPiece == null) throw new IllegalArgumentException();

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

  public static int valueOf(int pieceType, int color) {
    switch (color) {
      case Color.WHITE:
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
      case Color.BLACK:
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

  public static GenericPiece toGenericPiece(int piece) {
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

  public static int ordinal(int piece) {
    switch (piece) {
      case WHITEPAWN:
        return 0;
      case WHITEKNIGHT:
        return 1;
      case WHITEBISHOP:
        return 2;
      case WHITEROOK:
        return 3;
      case WHITEQUEEN:
        return 4;
      case WHITEKING:
        return 5;
      case BLACKPAWN:
        return 6;
      case BLACKKNIGHT:
        return 7;
      case BLACKBISHOP:
        return 8;
      case BLACKROOK:
        return 9;
      case BLACKQUEEN:
        return 10;
      case BLACKKING:
        return 11;
      case NOPIECE:
      default:
        throw new IllegalArgumentException();
    }
  }

  public static boolean isValid(int piece) {
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

  public static int getType(int piece) {
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

  public static int getColor(int piece) {
    switch (piece) {
      case WHITEPAWN:
      case WHITEKNIGHT:
      case WHITEBISHOP:
      case WHITEROOK:
      case WHITEQUEEN:
      case WHITEKING:
        return Color.WHITE;
      case BLACKPAWN:
      case BLACKKNIGHT:
      case BLACKBISHOP:
      case BLACKROOK:
      case BLACKQUEEN:
      case BLACKKING:
        return Color.BLACK;
      case NOPIECE:
      default:
        throw new IllegalArgumentException();
    }
  }

}
