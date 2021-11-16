/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "model/file.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(filetest, testValues) {
	for (auto file: file::values) {
		EXPECT_EQ(file, file::values[file]);
	}
}
