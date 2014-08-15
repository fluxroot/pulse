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
      for (int pieceType : PieceType.values) {
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
    assertEquals(PieceType.PAWN, Piece.getType(Piece.WHITE_PAWN));
    assertEquals(PieceType.PAWN, Piece.getType(Piece.BLACK_PAWN));
    assertEquals(PieceType.KNIGHT, Piece.getType(Piece.WHITE_KNIGHT));
    assertEquals(PieceType.KNIGHT, Piece.getType(Piece.BLACK_KNIGHT));
    assertEquals(PieceType.BISHOP, Piece.getType(Piece.WHITE_BISHOP));
    assertEquals(PieceType.BISHOP, Piece.getType(Piece.BLACK_BISHOP));
    assertEquals(PieceType.ROOK, Piece.getType(Piece.WHITE_ROOK));
    assertEquals(PieceType.ROOK, Piece.getType(Piece.BLACK_ROOK));
    assertEquals(PieceType.QUEEN, Piece.getType(Piece.WHITE_QUEEN));
    assertEquals(PieceType.QUEEN, Piece.getType(Piece.BLACK_QUEEN));
    assertEquals(PieceType.KING, Piece.getType(Piece.WHITE_KING));
    assertEquals(PieceType.KING, Piece.getType(Piece.BLACK_KING));
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
