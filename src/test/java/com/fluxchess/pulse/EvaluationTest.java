/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationTest {

	@Test
	void testEvaluate() {
		Position position = Notation.toPosition(Notation.STANDARDPOSITION);
		Evaluation evaluation = new Evaluation();

		assertThat(evaluation.evaluate(position)).isEqualTo(Evaluation.TEMPO);
	}
}
