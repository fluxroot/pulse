/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericBoard;
import org.junit.Test;

public class BoardTest {

  @Test
  public void testConstructor() {
    Board board = new Board(new GenericBoard(GenericBoard.STANDARDSETUP));
  }

}
