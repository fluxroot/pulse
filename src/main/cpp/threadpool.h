// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#pragma once

#include <future>
#include <queue>

namespace pulse {

class ThreadPool final {
public:
	ThreadPool() : running(true) {
		threads.emplace_back(&ThreadPool::worker, this);
	};

	~ThreadPool() {
		{
			std::unique_lock<std::mutex> lock(mutex);
			running = false;
		}
		condition.notify_all();
		for (auto& thread: threads) {
			thread.join();
		}
	}

	template<class R>
	std::future<std::invoke_result_t<R>> submit(R&& task) {
		auto packagedTask = std::make_shared<std::packaged_task<std::invoke_result_t<R>() >>(task);
		auto future = packagedTask->get_future();
		{
			std::unique_lock<std::mutex> lock(mutex);
			tasks.emplace([packagedTask] { (*packagedTask)(); });
		}
		condition.notify_one();
		return future;
	}

private:
	bool running;
	std::vector<std::thread> threads;
	std::queue<std::function<void()>> tasks;
	std::mutex mutex;
	std::condition_variable condition;

	void worker() {
		for (;;) {
			std::function < void() > task;
			{
				std::unique_lock<std::mutex> lock(mutex);
				condition.wait(lock, [this] { return !this->running || !this->tasks.empty(); });
				if (!running) {
					return;
				}
				task = std::move(tasks.front());
				tasks.pop();
			}
			task();
		}
	};
};
}
