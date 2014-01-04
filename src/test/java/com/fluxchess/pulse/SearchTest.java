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
    final Semaphore semaphore = new Semaphore(0);

    Search.newInfiniteSearch(
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
          assertNull(command.bestMove);
          assertNull(command.ponderMove);
          semaphore.release();
        }

        @Override
        public void send(ProtocolInformationCommand command) {
        }
      }
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void testMateStopCondition() throws IllegalNotationException, InterruptedException {
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
          assertEquals(new GenericMove(GenericPosition.h7, GenericPosition.h8), command.bestMove);
          semaphore.release();
        }

        @Override
        public void send(ProtocolInformationCommand command) {
        }
      },
      10000, 0, 10000, 0, 40
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void testOneMoveStopCondition() throws IllegalNotationException, InterruptedException {
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
          assertEquals(new GenericMove(GenericPosition.a8, GenericPosition.a7), command.bestMove);
          semaphore.release();
        }

        @Override
        public void send(ProtocolInformationCommand command) {
        }
      },
      10000, 0, 10000, 0, 40
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));
  }

}
