/*
 * Copyright 2007-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericPiece;

/**
 * This class encodes piece information as an int value. The data is
 * encoded as follows:<br/>
 * <br/>
 * <code>Bit 0 - 2</code>: the chessman (required)<br/>
 * <code>Bit 3 - 4</code>: the color (required)<br/>
 */
public final class Piece {

  public static final int MASK = 0x1F;

  private static final int CHESSMAN_SHIFT = 0;
  private static final int CHESSMAN_MASK = PieceType.MASK << CHESSMAN_SHIFT;
  private static final int COLOR_SHIFT = 3;
  private static final int COLOR_MASK = Color.MASK << COLOR_SHIFT;

  public static final int NOPIECE = (PieceType.NOCHESSMAN << CHESSMAN_SHIFT) | (Color.NOCOLOR << COLOR_SHIFT);

  public static final int WHITEPAWN = (PieceType.PAWN << CHESSMAN_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int WHITEKNIGHT = (PieceType.KNIGHT << CHESSMAN_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int WHITEBISHOP = (PieceType.BISHOP << CHESSMAN_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int WHITEROOK = (PieceType.ROOK << CHESSMAN_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int WHITEQUEEN = (PieceType.QUEEN << CHESSMAN_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int WHITEKING = (PieceType.KING << CHESSMAN_SHIFT) | (Color.WHITE << COLOR_SHIFT);
  public static final int BLACKPAWN = (PieceType.PAWN << CHESSMAN_SHIFT) | (Color.BLACK << COLOR_SHIFT);
  public static final int BLACKKNIGHT = (PieceType.KNIGHT << CHESSMAN_SHIFT) | (Color.BLACK << COLOR_SHIFT);
  public static final int BLACKBISHOP = (PieceType.BISHOP << CHESSMAN_SHIFT) | (Color.BLACK << COLOR_SHIFT);
  public static final int BLACKROOK = (PieceType.ROOK << CHESSMAN_SHIFT) | (Color.BLACK << COLOR_SHIFT);
  public static final int BLACKQUEEN = (PieceType.QUEEN << CHESSMAN_SHIFT) | (Color.BLACK << COLOR_SHIFT);
  public static final int BLACKKING = (PieceType.KING << CHESSMAN_SHIFT) | (Color.BLACK << COLOR_SHIFT);

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

  public static int valueOf(int chessman, int color) {
    switch (color) {
      case Color.WHITE:
        switch (chessman) {
          case PieceType.PAWN:
            return WHITEPAWN;
          case PieceType.KNIGHT:
            return WHITEKNIGHT;
          case PieceType.BISHOP:
            return WHITEBISHOP;
          case PieceType.ROOK:
            return WHITEROOK;
          case PieceType.QUEEN:
            return WHITEQUEEN;
          case PieceType.KING:
            return WHITEKING;
          case PieceType.NOCHESSMAN:
          default:
            throw new IllegalArgumentException();
        }
      case Color.BLACK:
        switch (chessman) {
          case PieceType.PAWN:
            return BLACKPAWN;
          case PieceType.KNIGHT:
            return BLACKKNIGHT;
          case PieceType.BISHOP:
            return BLACKBISHOP;
          case PieceType.ROOK:
            return BLACKROOK;
          case PieceType.QUEEN:
            return BLACKQUEEN;
          case PieceType.KING:
            return BLACKKING;
          case PieceType.NOCHESSMAN:
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

  public static int getChessman(int piece) {
    switch (piece) {
      case WHITEPAWN:
      case BLACKPAWN:
        return PieceType.PAWN;
      case WHITEKNIGHT:
      case BLACKKNIGHT:
        return PieceType.KNIGHT;
      case WHITEBISHOP:
      case BLACKBISHOP:
        return PieceType.BISHOP;
      case WHITEROOK:
      case BLACKROOK:
        return PieceType.ROOK;
      case WHITEQUEEN:
      case BLACKQUEEN:
        return PieceType.QUEEN;
      case WHITEKING:
      case BLACKKING:
        return PieceType.KING;
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
