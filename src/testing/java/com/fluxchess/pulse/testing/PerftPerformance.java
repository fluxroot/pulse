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
package com.fluxchess.pulse.testing;

import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.pulse.Board;
import com.fluxchess.pulse.Move;
import com.fluxchess.pulse.MoveGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

final class PerftPerformance {

  private static final Logger LOG = LoggerFactory.getLogger(PerftPerformance.class);

  private PerftPerformance() {
  }

  public static void main(String[] args) {
    long totalNodes = 0;
    long totalTime = 0;

    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);
    int depth = 6;

    LOG.info(String.format("Testing %s at depth %d", genericBoard.toString(), depth));

    for (int i = 1; i < 4; ++i) {
      long startTime = System.currentTimeMillis();
      int result = miniMax(depth, board, 0);
      long endTime = System.currentTimeMillis();

      long duration = endTime - startTime;
      totalNodes += result;
      totalTime += duration;

      LOG.info(String.format(
          "Duration iteration %d: %02d:%02d:%02d.%03d",
          i,
          TimeUnit.MILLISECONDS.toHours(duration),
          TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
          TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)),
          duration - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration))
      ));
    }

    LOG.info(String.format("Total nodes per millisecond: %d", totalNodes / totalTime));
  }

  private static int miniMax(int depth, Board board, int height) {
    if (depth == 0) {
      return 1;
    }

    int totalNodes = 0;

    boolean isCheck = board.isCheck();
    MoveGenerator moveGenerator = MoveGenerator.getMoveGenerator(board, depth, height, isCheck);
    int move;
    while ((move = moveGenerator.next()) != Move.NOMOVE) {
      board.makeMove(move);
      int nodes = miniMax(depth - 1, board, height + 1);
      board.undoMove(move);

      totalNodes += nodes;
    }

    return totalNodes;
  }

}
