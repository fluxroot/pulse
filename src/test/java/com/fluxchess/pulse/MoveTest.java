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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MoveTest {

  @Test
  public void testUtilityClass()
      throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Move.class);
  }

  @Test
  public void testCreation() {
    int move = Move.valueOf(MoveType.PAWNPROMOTION, Square.a7, Square.b8, Piece.WHITE_PAWN, Piece.BLACK_QUEEN, PieceType.KNIGHT);

    assertThat(Move.getType(move), is(MoveType.PAWNPROMOTION));
    assertThat(Move.getOriginSquare(move), is(Square.a7));
    assertThat(Move.getTargetSquare(move), is(Square.b8));
    assertThat(Move.getOriginPiece(move), is(Piece.WHITE_PAWN));
    assertThat(Move.getTargetPiece(move), is(Piece.BLACK_QUEEN));
    assertThat(Move.getPromotion(move), is(PieceType.KNIGHT));
  }

  @Test
  public void testPromotion() {
    int move = Move.valueOf(MoveType.PAWNPROMOTION, Square.b7, Square.c8, Piece.WHITE_PAWN, Piece.BLACK_QUEEN, PieceType.KNIGHT);

    assertThat(Move.getPromotion(move), is(PieceType.KNIGHT));
  }

}
