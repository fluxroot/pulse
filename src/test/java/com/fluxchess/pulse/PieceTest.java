/*
 * Copyright 2007-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
      assertEquals(genericPiece, Piece.toGenericPiece(Piece.valueOf(PieceType.valueOf(genericPiece.chessman), Color.valueOf(genericPiece.color))));
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
    assertEquals(PieceType.PAWN, Piece.getChessman(Piece.WHITEPAWN));
    assertEquals(PieceType.PAWN, Piece.getChessman(Piece.BLACKPAWN));
    assertEquals(PieceType.KNIGHT, Piece.getChessman(Piece.WHITEKNIGHT));
    assertEquals(PieceType.KNIGHT, Piece.getChessman(Piece.BLACKKNIGHT));
    assertEquals(PieceType.BISHOP, Piece.getChessman(Piece.WHITEBISHOP));
    assertEquals(PieceType.BISHOP, Piece.getChessman(Piece.BLACKBISHOP));
    assertEquals(PieceType.ROOK, Piece.getChessman(Piece.WHITEROOK));
    assertEquals(PieceType.ROOK, Piece.getChessman(Piece.BLACKROOK));
    assertEquals(PieceType.QUEEN, Piece.getChessman(Piece.WHITEQUEEN));
    assertEquals(PieceType.QUEEN, Piece.getChessman(Piece.BLACKQUEEN));
    assertEquals(PieceType.KING, Piece.getChessman(Piece.WHITEKING));
    assertEquals(PieceType.KING, Piece.getChessman(Piece.BLACKKING));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGetChessman() {
    Piece.getChessman(Piece.NOPIECE);
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
