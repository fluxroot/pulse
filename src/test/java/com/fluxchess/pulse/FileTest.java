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

import com.fluxchess.jcpi.models.GenericFile;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.fluxchess.test.AssertUtil.assertUtilityClassWellDefined;
import static org.junit.Assert.*;

public class FileTest {

  @Test
  public void testUtilityClass() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    assertUtilityClassWellDefined(File.class);
  }

  @Test
  public void testValues() {
    for (GenericFile genericFile : GenericFile.values()) {
      assertEquals(genericFile, File.toGenericFile(File.valueOf(genericFile)));
      assertEquals(genericFile.ordinal(), File.valueOf(genericFile));
      assertEquals(File.valueOf(genericFile), File.values[File.valueOf(genericFile)]);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericFile() {
    File.toGenericFile(File.NOFILE);
  }

  @Test
  public void testIsValid() {
    for (int file : File.values) {
      assertTrue(File.isValid(file));
    }

    assertFalse(File.isValid(File.NOFILE));
  }

}
