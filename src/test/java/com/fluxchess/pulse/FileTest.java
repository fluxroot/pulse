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
  public void testInvalidValueOf() {
    File.valueOf(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToGenericFile() {
    File.toGenericFile(File.NOFILE);
  }

  @Test
  public void testIsValid() {
    for (int file : File.values) {
      assertTrue(File.isValid(file));
      assertEquals(file, file & File.MASK);
    }

    assertFalse(File.isValid(File.NOFILE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIsValid() {
    File.isValid(-1);
  }

}
