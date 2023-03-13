// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "threadpool.h"

#include "gtest/gtest.h"

using namespace pulse;

TEST(threadpooltest, test) {
	ThreadPool threadPool;
	std::future<int> result = threadPool.submit([] {
		return 42;
	});
	EXPECT_EQ(result.get(), 42);
}
