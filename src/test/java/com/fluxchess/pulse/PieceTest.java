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
