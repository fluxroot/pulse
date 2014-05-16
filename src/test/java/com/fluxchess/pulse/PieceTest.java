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
import static org.junit.Assert.*;

public class PieceTest {

  @Test
  public void testUtilityClass() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Piece.class);
  }

  @Test
  public void testValues() {
    for (int color : Color.values) {
      for (int pieceType : Piece.Type.values) {
        int piece = Piece.valueOf(pieceType, color);

        assertTrue(Piece.isValid(piece));
        assertEquals(piece, Piece.values[piece]);
      }
    }

    assertFalse(Piece.isValid(Piece.NOPIECE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericPiece() {
    Piece.toGenericPiece(Piece.NOPIECE);
  }

  @Test
  public void testGetType() {
    assertEquals(Piece.Type.PAWN, Piece.getType(Piece.WHITE_PAWN));
    assertEquals(Piece.Type.PAWN, Piece.getType(Piece.BLACK_PAWN));
    assertEquals(Piece.Type.KNIGHT, Piece.getType(Piece.WHITE_KNIGHT));
    assertEquals(Piece.Type.KNIGHT, Piece.getType(Piece.BLACK_KNIGHT));
    assertEquals(Piece.Type.BISHOP, Piece.getType(Piece.WHITE_BISHOP));
    assertEquals(Piece.Type.BISHOP, Piece.getType(Piece.BLACK_BISHOP));
    assertEquals(Piece.Type.ROOK, Piece.getType(Piece.WHITE_ROOK));
    assertEquals(Piece.Type.ROOK, Piece.getType(Piece.BLACK_ROOK));
    assertEquals(Piece.Type.QUEEN, Piece.getType(Piece.WHITE_QUEEN));
    assertEquals(Piece.Type.QUEEN, Piece.getType(Piece.BLACK_QUEEN));
    assertEquals(Piece.Type.KING, Piece.getType(Piece.WHITE_KING));
    assertEquals(Piece.Type.KING, Piece.getType(Piece.BLACK_KING));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGetType() {
    Piece.getType(Piece.NOPIECE);
  }

  @Test
  public void testGetColor() {
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITE_PAWN));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACK_PAWN));
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITE_KNIGHT));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACK_KNIGHT));
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITE_BISHOP));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACK_BISHOP));
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITE_ROOK));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACK_ROOK));
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITE_QUEEN));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACK_QUEEN));
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITE_KING));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACK_KING));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGetColor() {
    Piece.getColor(Piece.NOPIECE);
  }

}
