/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EvaluationTest {

  @Test
  public void testEvaluate() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);
    Evaluation evaluation = new Evaluation();

    assertThat(evaluation.evaluate(position), is(Evaluation.TEMPO));
  }

}
