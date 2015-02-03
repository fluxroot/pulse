/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.pulse.MoveType.PAWNPROMOTION;
import static com.fluxchess.pulse.Piece.BLACK_QUEEN;
import static com.fluxchess.pulse.Piece.WHITE_PAWN;
import static com.fluxchess.pulse.PieceType.KNIGHT;
import static com.fluxchess.pulse.Square.a7;
import static com.fluxchess.pulse.Square.b7;
import static com.fluxchess.pulse.Square.b8;
import static com.fluxchess.pulse.Square.c8;
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
    int move = Move.valueOf(PAWNPROMOTION, a7, b8, WHITE_PAWN, BLACK_QUEEN, KNIGHT);

    assertThat(Move.getType(move), is(PAWNPROMOTION));
    assertThat(Move.getOriginSquare(move), is(a7));
    assertThat(Move.getTargetSquare(move), is(b8));
    assertThat(Move.getOriginPiece(move), is(WHITE_PAWN));
    assertThat(Move.getTargetPiece(move), is(BLACK_QUEEN));
    assertThat(Move.getPromotion(move), is(KNIGHT));
  }

  @Test
  public void testPromotion() {
    int move = Move.valueOf(PAWNPROMOTION, b7, c8, WHITE_PAWN, BLACK_QUEEN, KNIGHT);

    assertThat(Move.getPromotion(move), is(KNIGHT));
  }

}
