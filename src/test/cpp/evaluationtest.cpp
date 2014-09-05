/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "evaluation.h"
#include "notation.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(evaluationtest, testEvaluate) {
  Board board(Notation::toBoard(Notation::STANDARDBOARD));
  Evaluation evaluation;

  EXPECT_EQ(+Evaluation::TEMPO, evaluation.evaluate(board));
}
