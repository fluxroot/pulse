/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EvaluationTest {

  @Test
  public void testEvaluate() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);
    Evaluation evaluation = new Evaluation();

    assertEquals(Evaluation.TEMPO, evaluation.evaluate(position));
  }

}
