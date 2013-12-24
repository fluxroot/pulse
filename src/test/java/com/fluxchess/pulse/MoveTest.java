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
    int move = Move.valueOf(Move.Type.PAWNPROMOTION, Position.a7, Position.b8, IntPiece.WHITEPAWN, IntPiece.BLACKQUEEN, IntChessman.KNIGHT);

    assertEquals(Move.Type.PAWNPROMOTION, Move.getType(move));
    assertEquals(Position.a7, Move.getOriginPosition(move));
    assertEquals(Position.b8, Move.getTargetPosition(move));
    assertEquals(IntPiece.WHITEPAWN, Move.getOriginPiece(move));
    assertEquals(IntPiece.BLACKQUEEN, Move.getTargetPiece(move));
    assertEquals(IntChessman.KNIGHT, Move.getPromotion(move));
  }

  @Test
  public void testConversion() throws IllegalNotationException {
    GenericMove genericMove = new GenericMove(GenericPosition.a2, GenericPosition.a3);
    int move = Move.valueOf(genericMove, new Board(new GenericBoard(GenericBoard.STANDARDSETUP)));

    assertEquals(Move.Type.NORMAL, Move.getType(move));
    assertEquals(Position.a2, Move.getOriginPosition(move));
    assertEquals(Position.a3, Move.getTargetPosition(move));
    assertEquals(IntPiece.WHITEPAWN, Move.getOriginPiece(move));
    assertEquals(IntPiece.NOPIECE, Move.getTargetPiece(move));
    assertEquals(IntChessman.NOCHESSMAN, Move.getPromotion(move));
    assertEquals(genericMove, Move.toGenericMove(move));

    genericMove = new GenericMove(GenericPosition.a2, GenericPosition.a4);
    move = Move.valueOf(genericMove, new Board(new GenericBoard(GenericBoard.STANDARDSETUP)));

    assertEquals(Move.Type.PAWNDOUBLE, Move.getType(move));
    assertEquals(Position.a2, Move.getOriginPosition(move));
    assertEquals(Position.a4, Move.getTargetPosition(move));
    assertEquals(IntPiece.WHITEPAWN, Move.getOriginPiece(move));
    assertEquals(IntPiece.NOPIECE, Move.getTargetPiece(move));
    assertEquals(IntChessman.NOCHESSMAN, Move.getPromotion(move));
    assertEquals(genericMove, Move.toGenericMove(move));

    genericMove = new GenericMove(GenericPosition.c7, GenericPosition.b8, GenericChessman.KNIGHT);
    move = Move.valueOf(genericMove, new Board(new GenericBoard("1q2k3/2P5/8/8/8/8/8/4K3 w - - 0 1")));

    assertEquals(Move.Type.PAWNPROMOTION, Move.getType(move));
    assertEquals(Position.c7, Move.getOriginPosition(move));
    assertEquals(Position.b8, Move.getTargetPosition(move));
    assertEquals(IntPiece.WHITEPAWN, Move.getOriginPiece(move));
    assertEquals(IntPiece.BLACKQUEEN, Move.getTargetPiece(move));
    assertEquals(IntChessman.KNIGHT, Move.getPromotion(move));
    assertEquals(genericMove, Move.toGenericMove(move));

    genericMove = new GenericMove(GenericPosition.d4, GenericPosition.c3);
    move = Move.valueOf(genericMove, new Board(new GenericBoard("4k3/8/8/8/2Pp4/8/8/4K3 b - c3 0 1")));

    assertEquals(Move.Type.ENPASSANT, Move.getType(move));
    assertEquals(Position.d4, Move.getOriginPosition(move));
    assertEquals(Position.c3, Move.getTargetPosition(move));
    assertEquals(IntPiece.BLACKPAWN, Move.getOriginPiece(move));
    assertEquals(IntPiece.WHITEPAWN, Move.getTargetPiece(move));
    assertEquals(IntChessman.NOCHESSMAN, Move.getPromotion(move));
    assertEquals(genericMove, Move.toGenericMove(move));

    genericMove = new GenericMove(GenericPosition.e1, GenericPosition.c1);
    move = Move.valueOf(genericMove, new Board(new GenericBoard("4k3/8/8/8/8/8/8/R3K3 w Q - 0 1")));

    assertEquals(Move.Type.CASTLING, Move.getType(move));
    assertEquals(Position.e1, Move.getOriginPosition(move));
    assertEquals(Position.c1, Move.getTargetPosition(move));
    assertEquals(IntPiece.WHITEKING, Move.getOriginPiece(move));
    assertEquals(IntPiece.NOPIECE, Move.getTargetPiece(move));
    assertEquals(IntChessman.NOCHESSMAN, Move.getPromotion(move));
    assertEquals(genericMove, Move.toGenericMove(move));
  }

  @Test
  public void testSetTargetPosition() {
    int move = Move.valueOf(Move.Type.PAWNPROMOTION, Position.b7, Position.c8, IntPiece.WHITEPAWN, IntPiece.BLACKQUEEN, IntChessman.KNIGHT);

    assertEquals(Position.c8, Move.getTargetPosition(move));

    move = Move.setTargetPosition(move, Position.a8);

    assertEquals(Position.a8, Move.getTargetPosition(move));

    move = Move.setTargetPositionAndPiece(move, Position.c8, IntPiece.BLACKKNIGHT);

    assertEquals(Position.c8, Move.getTargetPosition(move));
    assertEquals(IntPiece.BLACKKNIGHT, Move.getTargetPiece(move));
  }

  @Test
  public void testPromotion() {
    int move = Move.valueOf(Move.Type.PAWNPROMOTION, Position.b7, Position.c8, IntPiece.WHITEPAWN, IntPiece.BLACKQUEEN, IntChessman.KNIGHT);

    assertEquals(IntChessman.KNIGHT, Move.getPromotion(move));

    move = Move.setPromotion(move, IntChessman.QUEEN);

    assertEquals(IntChessman.QUEEN, Move.getPromotion(move));
  }

}
