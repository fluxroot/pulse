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

import com.fluxchess.jcpi.models.GenericBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public final class PerftPerformance {

  private static final Logger LOG = LoggerFactory.getLogger(PerftPerformance.class);

  public static void main(String[] args) {
    long totalNodes = 0;
    long totalTime = 0;

    GenericBoard genericBoard = new GenericBoard(GenericBoard.STANDARDSETUP);
    Board board = new Board(genericBoard);
    MoveGenerator moveGenerator = new MoveGenerator(board);
    int depth = 6;

    LOG.info(String.format("Testing %s at depth %d", genericBoard.toString(), depth));

    for (int i = 1; i < 4; ++i) {
      long startTime = System.currentTimeMillis();
      int result = miniMax(board, moveGenerator, depth);
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

  private static int miniMax(Board board, MoveGenerator moveGenerator, int depth) {
    if (depth == 0) {
      return 1;
    }

    int totalNodes = 0;

    MoveList moves = moveGenerator.getAll();
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.moves[i];

      board.makeMove(move);
      int nodes = miniMax(board, moveGenerator, depth - 1);
      board.undoMove(move);

      totalNodes += nodes;
    }

    return totalNodes;
  }

}
