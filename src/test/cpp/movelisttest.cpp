/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "movelist.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(movelisttest, test) {
  MoveList<MoveEntry> moveList;

  EXPECT_EQ(0, moveList.size);

  moveList.entries[moveList.size++]->move = 1;
  EXPECT_EQ(1, moveList.size);
}
