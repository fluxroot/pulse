// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.
#pragma once

#include "protocol.h"
#include "position.h"
#include "movegenerator.h"
#include "evaluation.h"

#include <memory>
#include <chrono>
#include <thread>
#include <mutex>
#include <condition_variable>

namespace pulse {

/**
 * This class implements our search in a separate thread to keep the main
 * thread available for more commands.
 */
class Search final {
public:
	explicit Search(Protocol& protocol);

	void newDepthSearch(Position& _position, int _searchDepth);

	void newNodesSearch(Position& _position, uint64_t _searchNodes);

	void newTimeSearch(Position& _position, uint64_t _searchTime);

	void newInfiniteSearch(Position& _position);

	void newClockSearch(Position& _position,
						uint64_t whiteTimeLeft, uint64_t whiteTimeIncrement, uint64_t blackTimeLeft,
						uint64_t blackTimeIncrement, int movesToGo);

	void newPonderSearch(Position& _position,
						 uint64_t whiteTimeLeft, uint64_t whiteTimeIncrement, uint64_t blackTimeLeft,
						 uint64_t blackTimeIncrement, int movesToGo);

	void reset();

	void start();

	void stop();

	void ponderhit();

	void quit();

	void run();

private:
	/**
	 * This is our search timer for time & clock & ponder searches.
	 */
	class Timer final {
	public:
		Timer(bool& timerStopped, bool& doTimeManagement, int& currentDepth, const int& initialDepth, bool& abort);

		void start(uint64_t _searchTime);

		void stop();

	private:
		std::mutex mutex;
		std::condition_variable condition;
		std::thread thread;

		bool& timerStopped;
		bool& doTimeManagement;
		int& currentDepth;
		const int& initialDepth;

		bool& abort;

		void run(uint64_t _searchTime);
	};

	class Semaphore final {
	public:
		explicit Semaphore(int permits);

		void acquire();

		void release();

		void drainPermits();

	private:
		int permits;
		std::mutex mutex;
		std::condition_variable condition;
	};

	std::thread thread;
	Semaphore wakeupSignal;
	Semaphore runSignal;
	Semaphore stopSignal;
	std::recursive_mutex sync;
	Protocol& protocol;
	bool running = false;
	bool shutdown = false;

	Position position;

	// We will store a MoveGenerator for each ply so we don't have to create them
	// in search. (which is expensive)
	std::array<MoveGenerator, depth::MAX_PLY> moveGenerators;

	// Depth search
	int searchDepth;

	// Nodes search
	uint64_t searchNodes;

	// Time & Clock & Ponder search
	uint64_t searchTime;
	Timer timer;
	bool timerStopped;
	bool runTimer;
	bool doTimeManagement;

	// Search parameters
	MoveList<RootEntry> rootMoves;
	bool abort;
	uint64_t totalNodes;
	const int initialDepth = 1;
	int currentDepth;
	int currentMaxDepth;
	int currentMove;
	int currentMoveNumber;
	std::array<MoveVariation, depth::MAX_PLY + 1> pv;

	void checkStopConditions();

	void updateSearch(int ply);

	void searchRoot(int depth, int alpha, int beta);

	int search(int depth, int alpha, int beta, int ply);

	int quiescent(int depth, int alpha, int beta, int ply);

	static void savePV(int move, MoveVariation& src, MoveVariation& dest);
};
}
