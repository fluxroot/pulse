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

import com.fluxchess.jcpi.models.*;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.junit.Assert.assertEquals;

public class MoveTest {

  @Test
  public void testUtilityClass() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Move.class);
  }

  @Test
  public void testCreation() {
    int move = Move.valueOf(Move.Type.PAWNPROMOTION, Square.a7, Square.b8, Piece.WHITE_PAWN, Piece.BLACK_QUEEN, Piece.Type.KNIGHT);

    assertEquals(Move.Type.PAWNPROMOTION, Move.getType(move));
    assertEquals(Square.a7, Move.getOriginSquare(move));
    assertEquals(Square.b8, Move.getTargetSquare(move));
    assertEquals(Piece.WHITE_PAWN, Move.getOriginPiece(move));
    assertEquals(Piece.BLACK_QUEEN, Move.getTargetPiece(move));
    assertEquals(Piece.Type.KNIGHT, Move.getPromotion(move));
  }

  @Test
  public void testConversion() throws IllegalNotationException {
    GenericMove genericMove = new GenericMove(GenericPosition.a2, GenericPosition.a3);
    int move = Move.valueOf(genericMove, new Board(new GenericBoard(GenericBoard.STANDARDSETUP)));

    assertEquals(Move.Type.NORMAL, Move.getType(move));
    assertEquals(Square.a2, Move.getOriginSquare(move));
    assertEquals(Square.a3, Move.getTargetSquare(move));
    assertEquals(Piece.WHITE_PAWN, Move.getOriginPiece(move));
    assertEquals(Piece.NOPIECE, Move.getTargetPiece(move));
    assertEquals(Piece.Type.NOPIECETYPE, Move.getPromotion(move));
    assertEquals(genericMove, Move.toGenericMove(move));

    genericMove = new GenericMove(GenericPosition.a2, GenericPosition.a4);
    move = Move.valueOf(genericMove, new Board(new GenericBoard(GenericBoard.STANDARDSETUP)));

    assertEquals(Move.Type.PAWNDOUBLE, Move.getType(move));
    assertEquals(Square.a2, Move.getOriginSquare(move));
    assertEquals(Square.a4, Move.getTargetSquare(move));
    assertEquals(Piece.WHITE_PAWN, Move.getOriginPiece(move));
    assertEquals(Piece.NOPIECE, Move.getTargetPiece(move));
    assertEquals(Piece.Type.NOPIECETYPE, Move.getPromotion(move));
    assertEquals(genericMove, Move.toGenericMove(move));

    genericMove = new GenericMove(GenericPosition.c7, GenericPosition.b8, GenericChessman.KNIGHT);
    move = Move.valueOf(genericMove, new Board(new GenericBoard("1q2k3/2P5/8/8/8/8/8/4K3 w - - 0 1")));

    assertEquals(Move.Type.PAWNPROMOTION, Move.getType(move));
    assertEquals(Square.c7, Move.getOriginSquare(move));
    assertEquals(Square.b8, Move.getTargetSquare(move));
    assertEquals(Piece.WHITE_PAWN, Move.getOriginPiece(move));
    assertEquals(Piece.BLACK_QUEEN, Move.getTargetPiece(move));
    assertEquals(Piece.Type.KNIGHT, Move.getPromotion(move));
    assertEquals(genericMove, Move.toGenericMove(move));

    genericMove = new GenericMove(GenericPosition.d4, GenericPosition.c3);
    move = Move.valueOf(genericMove, new Board(new GenericBoard("4k3/8/8/8/2Pp4/8/8/4K3 b - c3 0 1")));

    assertEquals(Move.Type.ENPASSANT, Move.getType(move));
    assertEquals(Square.d4, Move.getOriginSquare(move));
    assertEquals(Square.c3, Move.getTargetSquare(move));
    assertEquals(Piece.BLACK_PAWN, Move.getOriginPiece(move));
    assertEquals(Piece.WHITE_PAWN, Move.getTargetPiece(move));
    assertEquals(Piece.Type.NOPIECETYPE, Move.getPromotion(move));
    assertEquals(genericMove, Move.toGenericMove(move));

    genericMove = new GenericMove(GenericPosition.e1, GenericPosition.c1);
    move = Move.valueOf(genericMove, new Board(new GenericBoard("4k3/8/8/8/8/8/8/R3K3 w Q - 0 1")));

    assertEquals(Move.Type.CASTLING, Move.getType(move));
    assertEquals(Square.e1, Move.getOriginSquare(move));
    assertEquals(Square.c1, Move.getTargetSquare(move));
    assertEquals(Piece.WHITE_KING, Move.getOriginPiece(move));
    assertEquals(Piece.NOPIECE, Move.getTargetPiece(move));
    assertEquals(Piece.Type.NOPIECETYPE, Move.getPromotion(move));
    assertEquals(genericMove, Move.toGenericMove(move));
  }

  @Test
  public void testPromotion() {
    int move = Move.valueOf(Move.Type.PAWNPROMOTION, Square.b7, Square.c8, Piece.WHITE_PAWN, Piece.BLACK_QUEEN, Piece.Type.KNIGHT);

    assertEquals(Piece.Type.KNIGHT, Move.getPromotion(move));
  }

}
