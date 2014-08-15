/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

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
    int move = Move.valueOf(Move.Type.PAWNPROMOTION, Square.a7, Square.b8, Piece.WHITE_PAWN, Piece.BLACK_QUEEN, PieceType.KNIGHT);

    assertEquals(Move.Type.PAWNPROMOTION, Move.getType(move));
    assertEquals(Square.a7, Move.getOriginSquare(move));
    assertEquals(Square.b8, Move.getTargetSquare(move));
    assertEquals(Piece.WHITE_PAWN, Move.getOriginPiece(move));
    assertEquals(Piece.BLACK_QUEEN, Move.getTargetPiece(move));
    assertEquals(PieceType.KNIGHT, Move.getPromotion(move));
  }

  @Test
  public void testPromotion() {
    int move = Move.valueOf(Move.Type.PAWNPROMOTION, Square.b7, Square.c8, Piece.WHITE_PAWN, Piece.BLACK_QUEEN, PieceType.KNIGHT);

    assertEquals(PieceType.KNIGHT, Move.getPromotion(move));
  }

}
