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

import com.fluxchess.jcpi.models.GenericPiece;
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
    for (GenericPiece genericPiece : GenericPiece.values()) {
      assertEquals(genericPiece, Piece.toGenericPiece(Piece.valueOf(genericPiece)));
      assertEquals(genericPiece, Piece.toGenericPiece(Piece.valueOf(Piece.Type.valueOf(genericPiece.chessman), Color.valueOf(genericPiece.color))));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidValueOf() {
    Piece.valueOf(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericPiece() {
    Piece.toGenericPiece(Piece.NOPIECE);
  }

  @Test
  public void testOrdinal() {
    for (int piece : Piece.values) {
      assertEquals(Piece.ordinal(piece), Piece.toGenericPiece(piece).ordinal());
      assertEquals(piece, Piece.values[Piece.ordinal(piece)]);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidOrdinal() {
    Piece.ordinal(Piece.NOPIECE);
  }

  @Test
  public void testIsValid() {
    for (int piece : Piece.values) {
      assertTrue(Piece.isValid(piece));
      assertEquals(piece, piece & Piece.MASK);
    }

    assertFalse(Piece.isValid(Piece.NOPIECE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsValid() {
    Piece.isValid(-1);
  }

  @Test
  public void testGetChessman() {
    assertEquals(Piece.Type.PAWN, Piece.getType(Piece.WHITEPAWN));
    assertEquals(Piece.Type.PAWN, Piece.getType(Piece.BLACKPAWN));
    assertEquals(Piece.Type.KNIGHT, Piece.getType(Piece.WHITEKNIGHT));
    assertEquals(Piece.Type.KNIGHT, Piece.getType(Piece.BLACKKNIGHT));
    assertEquals(Piece.Type.BISHOP, Piece.getType(Piece.WHITEBISHOP));
    assertEquals(Piece.Type.BISHOP, Piece.getType(Piece.BLACKBISHOP));
    assertEquals(Piece.Type.ROOK, Piece.getType(Piece.WHITEROOK));
    assertEquals(Piece.Type.ROOK, Piece.getType(Piece.BLACKROOK));
    assertEquals(Piece.Type.QUEEN, Piece.getType(Piece.WHITEQUEEN));
    assertEquals(Piece.Type.QUEEN, Piece.getType(Piece.BLACKQUEEN));
    assertEquals(Piece.Type.KING, Piece.getType(Piece.WHITEKING));
    assertEquals(Piece.Type.KING, Piece.getType(Piece.BLACKKING));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGetChessman() {
    Piece.getType(Piece.NOPIECE);
  }

  @Test
  public void testGetColor() {
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITEPAWN));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACKPAWN));
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITEKNIGHT));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACKKNIGHT));
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITEBISHOP));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACKBISHOP));
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITEROOK));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACKROOK));
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITEQUEEN));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACKQUEEN));
    assertEquals(Color.WHITE, Piece.getColor(Piece.WHITEKING));
    assertEquals(Color.BLACK, Piece.getColor(Piece.BLACKKING));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGetColor() {
    Piece.getColor(Piece.NOPIECE);
  }

}
