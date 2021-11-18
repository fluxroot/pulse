/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.pulse.model.Move;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;

import static com.fluxchess.pulse.MoveList.RootEntry;
import static com.fluxchess.pulse.model.Move.NOMOVE;
import static com.fluxchess.pulse.model.Square.*;
import static com.fluxchess.pulse.model.Value.*;
import static java.lang.Integer.signum;
import static java.lang.Math.abs;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class SearchTest {

	@Test
	void testMate() throws InterruptedException {
		final int[] currentBestMove = {NOMOVE};
		final int[] currentPonderMove = {NOMOVE};

		final Semaphore semaphore = new Semaphore(0);

		Search search = new Search(
				new Protocol() {
					@Override
					public void sendBestMove(int bestMove, int ponderMove) {
						currentBestMove[0] = bestMove;
						currentPonderMove[0] = ponderMove;

						semaphore.release();
					}

					@Override
					public void sendStatus(int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
					}

					@Override
					public void sendStatus(boolean force, int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
					}

					@Override
					public void sendMove(RootEntry entry, int currentDepth, int currentMaxDepth, long totalNodes) {
					}

					@Override
					public void sendDebug(String message) {
					}
				});
		search.newDepthSearch(Notation.toPosition("3K3r/8/3k4/8/8/8/8/8 w - - 0 1"), 1);
		search.start();

		assertThat(semaphore.tryAcquire(10000, MILLISECONDS)).isEqualTo(true);

		assertThat(currentBestMove[0]).isEqualTo(NOMOVE);
		assertThat(currentPonderMove[0]).isEqualTo(NOMOVE);
	}

	@Test
	void testMate1() throws InterruptedException {
		final int[] currentBestMove = {NOMOVE};
		final int[] currentPonderMove = {NOMOVE};
		final int[] mate = {NOVALUE};

		final Semaphore semaphore = new Semaphore(0);

		Search search = new Search(
				new Protocol() {
					@Override
					public void sendBestMove(int bestMove, int ponderMove) {
						currentBestMove[0] = bestMove;
						currentPonderMove[0] = ponderMove;

						semaphore.release();
					}

					@Override
					public void sendStatus(int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
					}

					@Override
					public void sendStatus(boolean force, int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
					}

					@Override
					public void sendMove(RootEntry entry, int currentDepth, int currentMaxDepth, long totalNodes) {
						if (abs(entry.value) >= CHECKMATE_THRESHOLD) {
							// Calculate mate distance
							int mateDepth = CHECKMATE - abs(entry.value);
							mate[0] = signum(entry.value) * (mateDepth + 1) / 2;
						}
					}

					@Override
					public void sendDebug(String message) {
					}
				});
		search.newDepthSearch(Notation.toPosition("8/8/1R1P4/2B2p2/k1K2P2/4P3/8/8 w - - 3 101"), 2);
		search.start();

		assertThat(semaphore.tryAcquire(10000, MILLISECONDS)).isEqualTo(true);

		assertThat(Move.getOriginSquare(currentBestMove[0])).isEqualTo(b6);
		assertThat(Move.getTargetSquare(currentBestMove[0])).isEqualTo(a6);
		assertThat(currentPonderMove[0]).isEqualTo(NOMOVE);
		assertThat(mate[0]).isEqualTo(1);
	}

	@Test
	void testStalemate() throws InterruptedException {
		final int[] currentBestMove = {NOMOVE};
		final int[] currentPonderMove = {NOMOVE};

		final Semaphore semaphore = new Semaphore(0);

		Search search = new Search(
				new Protocol() {
					@Override
					public void sendBestMove(int bestMove, int ponderMove) {
						currentBestMove[0] = bestMove;
						currentPonderMove[0] = ponderMove;

						semaphore.release();
					}

					@Override
					public void sendStatus(int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
					}

					@Override
					public void sendStatus(boolean force, int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
					}

					@Override
					public void sendMove(RootEntry entry, int currentDepth, int currentMaxDepth, long totalNodes) {
					}

					@Override
					public void sendDebug(String message) {
					}
				});
		search.newDepthSearch(Notation.toPosition("7k/5K2/6Q1/8/8/8/8/8 b - - 1 1"), 1);
		search.start();

		assertThat(semaphore.tryAcquire(10000, MILLISECONDS)).isEqualTo(true);

		assertThat(currentBestMove[0]).isEqualTo(NOMOVE);
		assertThat(currentPonderMove[0]).isEqualTo(NOMOVE);
	}

	@Test
	void testMateStopCondition() throws InterruptedException {
		final int[] currentBestMove = {NOMOVE};

		final Semaphore semaphore = new Semaphore(0);

		Search search = new Search(
				new Protocol() {
					@Override
					public void sendBestMove(int bestMove, int ponderMove) {
						currentBestMove[0] = bestMove;

						semaphore.release();
					}

					@Override
					public void sendStatus(int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
					}

					@Override
					public void sendStatus(boolean force, int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
					}

					@Override
					public void sendMove(RootEntry entry, int currentDepth, int currentMaxDepth, long totalNodes) {
					}

					@Override
					public void sendDebug(String message) {
					}
				});
		search.newClockSearch(Notation.toPosition("3K4/7r/3k4/8/8/8/8/8 b - - 0 1"), 10000, 0, 10000, 0, 40);
		search.start();

		assertThat(semaphore.tryAcquire(10000, MILLISECONDS)).isEqualTo(true);

		assertThat(Move.getOriginSquare(currentBestMove[0])).isEqualTo(h7);
		assertThat(Move.getTargetSquare(currentBestMove[0])).isEqualTo(h8);
	}

	@Test
	void testOneMoveStopCondition() throws InterruptedException {
		final int[] currentBestMove = {NOMOVE};

		final Semaphore semaphore = new Semaphore(0);

		Search search = new Search(
				new Protocol() {
					@Override
					public void sendBestMove(int bestMove, int ponderMove) {
						currentBestMove[0] = bestMove;

						semaphore.release();
					}

					@Override
					public void sendStatus(int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
					}

					@Override
					public void sendStatus(boolean force, int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber) {
					}

					@Override
					public void sendMove(RootEntry entry, int currentDepth, int currentMaxDepth, long totalNodes) {
					}

					@Override
					public void sendDebug(String message) {
					}
				});
		search.newClockSearch(Notation.toPosition("K1k5/8/8/8/8/8/8/8 w - - 0 1"), 10000, 0, 10000, 0, 40);
		search.start();

		assertThat(semaphore.tryAcquire(10000, MILLISECONDS)).isEqualTo(true);

		assertThat(Move.getOriginSquare(currentBestMove[0])).isEqualTo(a8);
		assertThat(Move.getTargetSquare(currentBestMove[0])).isEqualTo(a7);
	}
}
