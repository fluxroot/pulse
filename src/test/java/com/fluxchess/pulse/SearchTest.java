/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.fluxchess.pulse.MoveList.RootEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchTest {

  @Test
  public void testMate() throws InterruptedException {
    final int[] currentBestMove = {Move.NOMOVE};
    final int[] currentPonderMove = {Move.NOMOVE};

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
        });
    search.newDepthSearch(Notation.toPosition("3K3r/8/3k4/8/8/8/8/8 w - - 0 1"), 1);
    search.start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertEquals(Move.NOMOVE, currentBestMove[0]);
    assertEquals(Move.NOMOVE, currentPonderMove[0]);
  }

  @Test
  public void testMate1() throws InterruptedException {
    final int[] currentBestMove = {Move.NOMOVE};
    final int[] currentPonderMove = {Move.NOMOVE};
    final int[] mate = {Value.NOVALUE};

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
            if (Math.abs(entry.value) >= Value.CHECKMATE_THRESHOLD) {
              // Calculate mate distance
              int mateDepth = Value.CHECKMATE - Math.abs(entry.value);
              mate[0] = Integer.signum(entry.value) * (mateDepth + 1) / 2;
            }
          }
        });
    search.newDepthSearch(Notation.toPosition("8/8/1R1P4/2B2p2/k1K2P2/4P3/8/8 w - - 3 101"), 2);
    search.start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertEquals(Square.b6, Move.getOriginSquare(currentBestMove[0]));
    assertEquals(Square.a6, Move.getTargetSquare(currentBestMove[0]));
    assertEquals(Move.NOMOVE, currentPonderMove[0]);
    assertEquals(1, mate[0]);
  }

  @Test
  public void testStalemate() throws InterruptedException {
    final int[] currentBestMove = {Move.NOMOVE};
    final int[] currentPonderMove = {Move.NOMOVE};

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
        });
    search.newDepthSearch(Notation.toPosition("7k/5K2/6Q1/8/8/8/8/8 b - - 1 1"), 1);
    search.start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertEquals(Move.NOMOVE, currentBestMove[0]);
    assertEquals(Move.NOMOVE, currentPonderMove[0]);
  }

  @Test
  public void testMateStopCondition() throws InterruptedException {
    final int[] currentBestMove = {Move.NOMOVE};

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
        });
    search.newClockSearch(Notation.toPosition("3K4/7r/3k4/8/8/8/8/8 b - - 0 1"), 10000, 0, 10000, 0, 40);
    search.start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertEquals(Square.h7, Move.getOriginSquare(currentBestMove[0]));
    assertEquals(Square.h8, Move.getTargetSquare(currentBestMove[0]));
  }

  @Test
  public void testOneMoveStopCondition() throws InterruptedException {
    final int[] currentBestMove = {Move.NOMOVE};

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
        });
    search.newClockSearch(Notation.toPosition("K1k5/8/8/8/8/8/8/8 w - - 0 1"), 10000, 0, 10000, 0, 40);
    search.start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertEquals(Square.a8, Move.getOriginSquare(currentBestMove[0]));
    assertEquals(Square.a7, Move.getTargetSquare(currentBestMove[0]));
  }

}
