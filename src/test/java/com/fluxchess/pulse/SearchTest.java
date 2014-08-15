/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
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

    final Semaphore semaphore = new Semaphore(0);

    Search.newDepthSearch(
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
          }
        }, new Board(new GenericBoard("3K3r/8/3k4/8/8/8/8/8 w - - 0 1")),
        1
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertNull(bestMove[0]);
    assertNull(ponderMove[0]);
  }

  @Test
  public void testMate1() throws IllegalNotationException, InterruptedException {
    final GenericMove[] bestMove = {null};
    final GenericMove[] ponderMove = {null};
    final int[] mate = {Value.NOVALUE};

    final Semaphore semaphore = new Semaphore(0);

    Search.newDepthSearch(
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
        }, new Board(new GenericBoard("8/8/1R1P4/2B2p2/k1K2P2/4P3/8/8 w - - 3 101")),
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

    final Semaphore semaphore = new Semaphore(0);

    Search.newDepthSearch(
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
          }
        }, new Board(new GenericBoard("7k/5K2/6Q1/8/8/8/8/8 b - - 1 1")),
        1
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertNull(bestMove[0]);
    assertNull(ponderMove[0]);
  }

  @Test
  public void testMateStopCondition() throws IllegalNotationException, InterruptedException {
    final GenericMove[] bestMove = {null};

    final Semaphore semaphore = new Semaphore(0);

    Search.newClockSearch(
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
        }, new Board(new GenericBoard("3K4/7r/3k4/8/8/8/8/8 b - - 0 1")),
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
        }, new Board(new GenericBoard("K1k5/8/8/8/8/8/8/8 w - - 0 1")),
        10000, 0, 10000, 0, 40
    ).start();

    assertTrue(semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS));

    assertEquals(new GenericMove(GenericPosition.a8, GenericPosition.a7), bestMove[0]);
  }

}
