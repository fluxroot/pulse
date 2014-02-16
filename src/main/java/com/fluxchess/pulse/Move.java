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

import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.jcpi.models.GenericPosition;
import com.fluxchess.jcpi.models.GenericRank;

/**
 * This class represents a move as a int value. The fields are represented by
 * the following bits.
 * <p/>
 *  0 -  2: type
 *  3 -  9: origin square
 * 10 - 16: target square
 * 17 - 21: origin piece
 * 22 - 26: target piece
 * 27 - 29: promotion piece type
 */
public final class Move {

  public static final class Type {
    public static final int MASK = 0x7;

    public static final int NORMAL = 0;
    public static final int PAWNDOUBLE = 1;
    public static final int PAWNPROMOTION = 2;
    public static final int ENPASSANT = 3;
    public static final int CASTLING = 4;
    public static final int NOTYPE = 5;

    private Type() {
    }

    public static boolean isValid(int type) {
      switch (type) {
        case NORMAL:
        case PAWNDOUBLE:
        case PAWNPROMOTION:
        case ENPASSANT:
        case CASTLING:
          return true;
        case NOTYPE:
          return false;
        default:
          throw new IllegalArgumentException();
      }
    }
  }

  // These are our bit masks
  private static final int TYPE_SHIFT = 0;
  private static final int TYPE_MASK = Move.Type.MASK << TYPE_SHIFT;
  private static final int ORIGINSQUARE_SHIFT = 3;
  private static final int ORIGINSQUARE_MASK = Square.MASK << ORIGINSQUARE_SHIFT;
  private static final int TARGETSQUARE_SHIFT = 10;
  private static final int TARGETSQUARE_MASK = Square.MASK << TARGETSQUARE_SHIFT;
  private static final int ORIGINPIECE_SHIFT = 17;
  private static final int ORIGINPIECE_MASK = Piece.MASK << ORIGINPIECE_SHIFT;
  private static final int TARGETPIECE_SHIFT = 22;
  private static final int TARGETPIECE_MASK = Piece.MASK << TARGETPIECE_SHIFT;
  private static final int PROMOTION_SHIFT = 27;
  private static final int PROMOTION_MASK = Piece.Type.MASK << PROMOTION_SHIFT;

  // We don't use 0 as a null value to protect against errors.
  public static final int NOMOVE = (Move.Type.NOTYPE << TYPE_SHIFT)
    | (Square.NOSQUARE << ORIGINSQUARE_SHIFT)
    | (Square.NOSQUARE << TARGETSQUARE_SHIFT)
    | (Piece.NOPIECE << ORIGINPIECE_SHIFT)
    | (Piece.NOPIECE << TARGETPIECE_SHIFT)
    | (Piece.Type.NOTYPE << PROMOTION_SHIFT);

  private Move() {
  }

  /**
   * Converts a GenericMove to our internal move representation.
   *
   * @param genericMove the GenericMove.
   * @param board the Board. We need the board to figure out what type of move
   *              we're dealing with.
   * @return the internal move representation.
   */
  public static int valueOf(GenericMove genericMove, Board board) {
    assert genericMove != null;
    assert board != null;

    if (isPromotion(genericMove, board)) {
      int promotion;
      if (genericMove.promotion == null) {
        // Promote to a queen if promotion is not set
        promotion = Piece.Type.QUEEN;
      } else {
        promotion = Piece.Type.valueOf(genericMove.promotion);
      }
      return valueOf(
        Move.Type.PAWNPROMOTION,
        Square.valueOf(genericMove.from),
        Square.valueOf(genericMove.to),
        board.board[Square.valueOf(genericMove.from)],
        board.board[Square.valueOf(genericMove.to)],
        promotion
      );
    } else if (isPawnDouble(genericMove, board)) {
      return valueOf(
        Move.Type.PAWNDOUBLE,
        Square.valueOf(genericMove.from),
        Square.valueOf(genericMove.to),
        board.board[Square.valueOf(genericMove.from)],
        Piece.NOPIECE,
        Piece.Type.NOTYPE
      );
    } else if (isEnPassant(genericMove, board)) {
      return valueOf(
        Move.Type.ENPASSANT,
        Square.valueOf(genericMove.from),
        Square.valueOf(genericMove.to),
        board.board[Square.valueOf(genericMove.from)],
        board.board[Square.valueOf(GenericPosition.valueOf(genericMove.to.file, genericMove.from.rank))],
        Piece.Type.NOTYPE
      );
    } else if (isCastling(genericMove, board)) {
      return valueOf(
        Move.Type.CASTLING, Square.valueOf(genericMove.from),
        Square.valueOf(genericMove.to),
        board.board[Square.valueOf(genericMove.from)],
        Piece.NOPIECE,
        Piece.Type.NOTYPE
      );
    } else {
      return valueOf(
        Move.Type.NORMAL,
        Square.valueOf(genericMove.from),
        Square.valueOf(genericMove.to),
        board.board[Square.valueOf(genericMove.from)],
        board.board[Square.valueOf(genericMove.to)],
        Piece.Type.NOTYPE
      );
    }
  }

  public static int valueOf(int type, int originSquare, int targetSquare, int originPiece, int targetPiece, int promotion) {
    int move = 0;

    // Encode type
    assert Move.Type.isValid(type);
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
    assert (Piece.Type.isValid(promotion) && Piece.Type.isValidPromotion(promotion))
      || promotion == Piece.Type.NOTYPE;
    move |= promotion << PROMOTION_SHIFT;

    return move;
  }

  public static GenericMove toGenericMove(int move) {
    int type = getType(move);
    int originSquare = getOriginSquare(move);
    int targetSquare = getTargetSquare(move);

    switch (type) {
      case Move.Type.NORMAL:
      case Move.Type.PAWNDOUBLE:
      case Move.Type.ENPASSANT:
      case Move.Type.CASTLING:
        return new GenericMove(
          Square.toGenericPosition(originSquare),
          Square.toGenericPosition(targetSquare)
        );
      case Move.Type.PAWNPROMOTION:
        return new GenericMove(
          Square.toGenericPosition(originSquare),
          Square.toGenericPosition(targetSquare),
          Piece.Type.toGenericChessman(getPromotion(move))
        );
      default:
        throw new IllegalArgumentException();
    }
  }

  public static int getType(int move) {
    int type = (move & TYPE_MASK) >>> TYPE_SHIFT;
    assert Move.Type.isValid(type);

    return type;
  }

  public static int getOriginSquare(int move) {
    int originSquare = (move & ORIGINSQUARE_MASK) >>> ORIGINSQUARE_SHIFT;
    assert Square.isValid(originSquare);

    return originSquare;
  }

  public static int getTargetSquare(int move) {
    int targetSquare = (move & TARGETSQUARE_MASK) >>> TARGETSQUARE_SHIFT;
    assert Square.isValid(targetSquare);

    return targetSquare;
  }

  public static int getOriginPiece(int move) {
    int originPiece = (move & ORIGINPIECE_MASK) >>> ORIGINPIECE_SHIFT;
    assert Piece.isValid(originPiece);

    return originPiece;
  }

  public static int getTargetPiece(int move) {
    int targetPiece = (move & TARGETPIECE_MASK) >>> TARGETPIECE_SHIFT;
    assert Piece.isValid(targetPiece) || targetPiece == Piece.NOPIECE;

    return targetPiece;
  }

  public static int getPromotion(int move) {
    int promotion = (move & PROMOTION_MASK) >>> PROMOTION_SHIFT;
    assert (Piece.Type.isValid(promotion) && Piece.Type.isValidPromotion(promotion))
      || promotion == Piece.Type.NOTYPE;

    return promotion;
  }

  private static boolean isPromotion(GenericMove move, Board board) {
    assert move != null;
    assert board != null;

    int originPiece = board.board[Square.valueOf(move.from)];

    return (originPiece == Piece.WHITEPAWN && move.from.rank == GenericRank.R7 && move.to.rank == GenericRank.R8)
      || (originPiece == Piece.BLACKPAWN && move.from.rank == GenericRank.R2 && move.to.rank == GenericRank.R1);
  }

  private static boolean isPawnDouble(GenericMove move, Board board) {
    assert move != null;
    assert board != null;

    int originPiece = board.board[Square.valueOf(move.from)];

    return (originPiece == Piece.WHITEPAWN && move.from.rank == GenericRank.R2 && move.to.rank == GenericRank.R4)
      || (originPiece == Piece.BLACKPAWN && move.from.rank == GenericRank.R7 && move.to.rank == GenericRank.R5);
  }

  private static boolean isEnPassant(GenericMove move, Board board) {
    assert move != null;
    assert board != null;

    int originPiece = board.board[Square.valueOf(move.from)];
    int targetPiece = board.board[Square.valueOf(GenericPosition.valueOf(move.to.file, move.from.rank))];

    return Piece.getType(originPiece) == Piece.Type.PAWN
      && Piece.isValid(targetPiece)
      && Piece.getType(targetPiece) == Piece.Type.PAWN
      && Color.opposite(Piece.getColor(originPiece)) == Piece.getColor(targetPiece)
      && board.enPassant == Square.valueOf(move.to);
  }

  private static boolean isCastling(GenericMove move, Board board) {
    assert move != null;
    assert board != null;

    int originPiece = board.board[Square.valueOf(move.from)];

    return Piece.getType(originPiece) == Piece.Type.KING && (
      // Castling WHITE kingside.
      (move.from == GenericPosition.e1 && move.to == GenericPosition.g1)

        // Castling WHITE queenside.
        || (move.from == GenericPosition.e1 && move.to == GenericPosition.c1)

        // Castling BLACK kingside.
        || (move.from == GenericPosition.e8 && move.to == GenericPosition.g8)

        // Castling BLACK queenside.
        || (move.from == GenericPosition.e8 && move.to == GenericPosition.c8)
    );
  }

}
