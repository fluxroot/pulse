/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.commands.IProtocol;
import com.fluxchess.jcpi.commands.ProtocolBestMoveCommand;
import com.fluxchess.jcpi.models.GenericMove;

import java.util.concurrent.Semaphore;

/**
 * This class implements our search in a separate thread to keep the main
 * thread available for more commands.
 */
public final class Search implements Runnable {

  private final Thread thread = new Thread(this);
  private final Semaphore semaphore = new Semaphore(0);
  private final IProtocol protocol;

  private final Board board;

  public Search(Board board, IProtocol protocol) {
    this.board = board;
    this.protocol = protocol;
  }

  public void start() {
    if (!thread.isAlive()) {
      thread.start();
      try {
        // Wait for initialization
        semaphore.acquire();
      } catch (InterruptedException e) {
        // Do nothing
      }
    }
  }

  public void stop() {
    if (thread.isAlive()) {

      // Signal the search thread that we want to stop it

      try {
        // Wait for the thread to die
        thread.join();
      } catch (InterruptedException e) {
        // Do nothing
      }
    }
  }

  public void run() {
    // Do all initialization before releasing the main thread to JCPI
    semaphore.release();

    // Get the best move and convert it to a GenericMove
    GenericMove bestMove = null;

    // Send the best move to the GUI
    protocol.send(new ProtocolBestMoveCommand(bestMove, null));
  }

}
