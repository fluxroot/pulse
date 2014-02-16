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

import com.fluxchess.jcpi.models.GenericCastling;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.junit.Assert.*;

public class CastlingTest {

  @Test
  public void testUtilityClass() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(Castling.class);
  }

  @Test
  public void testValues() {
    for (GenericCastling genericCastling : GenericCastling.values()) {
      assertEquals(genericCastling, Castling.toGenericCastling(Castling.valueOf(genericCastling)));
      assertEquals(genericCastling.ordinal(), Castling.valueOf(genericCastling));
      assertEquals(Castling.valueOf(genericCastling), Castling.values[Castling.valueOf(genericCastling)]);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidValueOf() {
    Castling.valueOf(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericCastling() {
    Castling.toGenericCastling(Castling.NOCASTLING);
  }

  @Test
  public void testIsValid() {
    for (int castling : Castling.values) {
      assertTrue(Castling.isValid(castling));
      assertEquals(castling, castling & Castling.MASK);
    }

    assertFalse(Castling.isValid(Castling.NOCASTLING));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsValid() {
    Castling.isValid(-1);
  }

}
