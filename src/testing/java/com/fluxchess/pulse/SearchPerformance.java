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
import com.fluxchess.jcpi.protocols.IProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class SearchPerformance {

  private static final Logger LOG = LoggerFactory.getLogger(SearchPerformance.class);

  public static void main(String[] args) throws InterruptedException {
    final GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    final int depth = 8;

    final long[] startTime = {0};
    final long[] endTime = {0};
    final long[] nps = {0};

    final BlockingQueue<IEngineCommand> commands = new LinkedBlockingQueue<>();

    commands.add(new EngineInitializeRequestCommand());
    commands.add(new EngineNewGameCommand());
    commands.add(new EngineAnalyzeCommand(genericBoard, new ArrayList<GenericMove>()));
    commands.add(new EngineReadyRequestCommand("test"));

    new Pulse(new IProtocolHandler() {
      @Override
      public IEngineCommand receive() throws IOException {
        IEngineCommand command;
        try {
          command = commands.take();
        } catch (InterruptedException e) {
          throw new IOException(e);
        }

        return command;
      }

      @Override
      public void send(ProtocolInitializeAnswerCommand command) {
        LOG.info(String.format("Testing %s at depth %d", genericBoard.toString(), depth));
      }

      @Override
      public void send(ProtocolReadyAnswerCommand command) {
        EngineStartCalculatingCommand calculatingCommand = new EngineStartCalculatingCommand();
        calculatingCommand.setDepth(depth);

        startTime[0] = System.currentTimeMillis();
        commands.add(calculatingCommand);
      }

      @Override
      public void send(ProtocolBestMoveCommand command) {
        endTime[0] = System.currentTimeMillis();

        long duration = endTime[0] - startTime[0];

        LOG.info(String.format(
          "Duration: %02d:%02d:%02d.%03d",
          TimeUnit.MILLISECONDS.toHours(duration),
          TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
          TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)),
          duration - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration))
        ));

        LOG.info(String.format("Total nodes per millisecond: %d", nps[0]));

        commands.add(new EngineQuitCommand());
      }

      @Override
      public void send(ProtocolInformationCommand command) {
        if (command.getNps() != null) {
          nps[0] = command.getNps();
        }
      }
    }).run();
  }

}
