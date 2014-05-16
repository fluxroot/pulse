/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericBoard;
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
      long result = miniMax(depth, board, 0);
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

  private static long miniMax(int depth, Board board, int ply) {
    if (depth == 0) {
      return 1;
    }

    int totalNodes = 0;

    boolean isCheck = board.isCheck();
    MoveGenerator moveGenerator = MoveGenerator.getMoveGenerator(board, depth, ply, isCheck);
    int move;
    while ((move = moveGenerator.next()) != Move.NOMOVE) {
      long nodes = 0;
      if (board.makeMove(move)) {
        nodes = miniMax(depth - 1, board, ply + 1);
      }
      board.undoMove(move);

      totalNodes += nodes;
    }

    return totalNodes;
  }

}
