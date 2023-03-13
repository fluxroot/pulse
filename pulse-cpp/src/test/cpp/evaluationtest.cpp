// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "evaluation.h"
#include "notation.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(evaluationtest, testEvaluate) {
	Position position(notation::toPosition(notation::STANDARDPOSITION));

	EXPECT_EQ(+evaluation::TEMPO, evaluation::evaluate(position));
}
