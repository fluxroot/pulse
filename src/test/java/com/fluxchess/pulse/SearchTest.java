/*
 * Copyright 2013-2014 the original author or authors.
 *
 * This file is part of Pulse Chess.
 *
 * Pulse Chess is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pulse Chess is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pulse Chess.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.commands.*;
import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.jcpi.models.GenericPosition;
import com.fluxchess.jcpi.models.IllegalNotationException;
import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SearchTest {

  @Test
  public void testMate() throws IllegalNotationException, InterruptedException {
    final GenericMove[] bestMove = {null};
    final GenericMove[] ponderMove = {null};
    final int[] depth = {0};
    final int[] mate = {-Evaluation.INFINITY};
    final long[] nodes = {0};

    final Semaphore semaphore = new Semaphore(0);

    Search.newDepthSearch(
      new Board(new GenericBoard("3K3r/8/3k4/8/8/8/8/8 w - - 0 1")),
      new IProtocol() {
        @Override
        public void send(ProtocolInitializeAnswerCommand command) {
        }

        @Override
        public void send(ProtocolReadyAnswerCommand command) {
        }

        @Override
        public void send(ProtocolBestMoveCommand command) {
          bestMove[0] = command.bestMove;
          ponderMove[0] = command.ponderMove;

          semaphore.release();
        }

        @Override
        public void send(ProtocolInformationCommand command) {
          depth[0] = command.getDepth();
          mate[0] = command.getMate();
          nodes[0] = command.getNodes();
        }
      },
      1
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertNull(bestMove[0]);
    assertNull(ponderMove[0]);
    assertEquals(1, depth[0]);
    assertEquals(0, mate[0]);
    assertEquals(1, nodes[0]);
  }

  @Test
  public void testMate1() throws IllegalNotationException, InterruptedException {
    final GenericMove[] bestMove = {null};
    final GenericMove[] ponderMove = {null};
    final int[] mate = {-Evaluation.INFINITY};

    final Semaphore semaphore = new Semaphore(0);

    Search.newDepthSearch(
      new Board(new GenericBoard("8/8/1R1P4/2B2p2/k1K2P2/4P3/8/8 w - - 3 101")),
      new IProtocol() {
        @Override
        public void send(ProtocolInitializeAnswerCommand command) {
        }

        @Override
        public void send(ProtocolReadyAnswerCommand command) {
        }

        @Override
        public void send(ProtocolBestMoveCommand command) {
          bestMove[0] = command.bestMove;
          ponderMove[0] = command.ponderMove;

          semaphore.release();
        }

        @Override
        public void send(ProtocolInformationCommand command) {
          if (command.getMate() != null) {
            mate[0] = command.getMate();
          }
        }
      },
      2
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertEquals(new GenericMove(GenericPosition.b6, GenericPosition.a6), bestMove[0]);
    assertNull(ponderMove[0]);
    assertEquals(1, mate[0]);
  }

  @Test
  public void testStalemate() throws IllegalNotationException, InterruptedException {
    final GenericMove[] bestMove = {null};
    final GenericMove[] ponderMove = {null};
    final int[] depth = {0};
    final int[] value = {-Evaluation.INFINITY};
    final long[] nodes = {0};

    final Semaphore semaphore = new Semaphore(0);

    Search.newDepthSearch(
      new Board(new GenericBoard("7k/5K2/6Q1/8/8/8/8/8 b - - 1 1")),
      new IProtocol() {
        @Override
        public void send(ProtocolInitializeAnswerCommand command) {
        }

        @Override
        public void send(ProtocolReadyAnswerCommand command) {
        }

        @Override
        public void send(ProtocolBestMoveCommand command) {
          bestMove[0] = command.bestMove;
          ponderMove[0] = command.ponderMove;

          semaphore.release();
        }

        @Override
        public void send(ProtocolInformationCommand command) {
          depth[0] = command.getDepth();
          value[0] = command.getCentipawns();
          nodes[0] = command.getNodes();
        }
      },
      1
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertNull(bestMove[0]);
    assertNull(ponderMove[0]);
    assertEquals(1, depth[0]);
    assertEquals(0, value[0]);
    assertEquals(1, nodes[0]);
  }

  @Test
  public void testMateStopCondition() throws IllegalNotationException, InterruptedException {
    final GenericMove[] bestMove = {null};

    final Semaphore semaphore = new Semaphore(0);

    Search.newClockSearch(
      new Board(new GenericBoard("3K4/7r/3k4/8/8/8/8/8 b - - 0 1")),
      new IProtocol() {
        @Override
        public void send(ProtocolInitializeAnswerCommand command) {
        }

        @Override
        public void send(ProtocolReadyAnswerCommand command) {
        }

        @Override
        public void send(ProtocolBestMoveCommand command) {
          bestMove[0] = command.bestMove;

          semaphore.release();
        }

        @Override
        public void send(ProtocolInformationCommand command) {
        }
      },
      10000, 0, 10000, 0, 40
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertEquals(new GenericMove(GenericPosition.h7, GenericPosition.h8), bestMove[0]);
  }

  @Test
  public void testOneMoveStopCondition() throws IllegalNotationException, InterruptedException {
    final GenericMove[] bestMove = {null};

    final Semaphore semaphore = new Semaphore(0);

    Search.newClockSearch(
      new Board(new GenericBoard("K1k5/8/8/8/8/8/8/8 w - - 0 1")),
      new IProtocol() {
        @Override
        public void send(ProtocolInitializeAnswerCommand command) {
        }

        @Override
        public void send(ProtocolReadyAnswerCommand command) {
        }

        @Override
        public void send(ProtocolBestMoveCommand command) {
          bestMove[0] = command.bestMove;

          semaphore.release();
        }

        @Override
        public void send(ProtocolInformationCommand command) {
        }
      },
      10000, 0, 10000, 0, 40
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertEquals(new GenericMove(GenericPosition.a8, GenericPosition.a7), bestMove[0]);
  }

}
