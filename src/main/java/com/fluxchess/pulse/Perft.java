/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import java.util.concurrent.TimeUnit;

import static com.fluxchess.pulse.MoveList.MoveEntry;

final class Perft {

  private static final int MAX_DEPTH = 6;

  private final MoveGenerator[] moveGenerators = new MoveGenerator[MAX_DEPTH];

  void run() {
    Position position = Notation.toPosition(Notation.STANDARDPOSITION);
    int depth = MAX_DEPTH;

    for (int i = 0; i < MAX_DEPTH; ++i) {
      moveGenerators[i] = new MoveGenerator();
    }

    System.out.format("Testing %s at depth %d%n", Notation.fromPosition(position), depth);

    long startTime = System.currentTimeMillis();
    long result = miniMax(depth, position, 0);
    long endTime = System.currentTimeMillis();

    long duration = endTime - startTime;

    System.out.format(
        "Nodes: %d%nDuration: %02d:%02d:%02d.%03d%n",
        result,
        TimeUnit.MILLISECONDS.toHours(duration),
        TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
        TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)),
        duration - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration))
    );

    System.out.format("n/ms: %d%n", result / duration);
  }

  private long miniMax(int depth, Position position, int ply) {
    if (depth == 0) {
      return 1;
    }

    int totalNodes = 0;

    boolean isCheck = position.isCheck();
    MoveGenerator moveGenerator = moveGenerators[ply];
    MoveList<MoveEntry> moves = moveGenerator.getMoves(position, depth, isCheck);
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.entries[i].move;
      long nodes = 0;

      position.makeMove(move);
      if (!position.isCheck(Color.opposite(position.activeColor))) {
        nodes = miniMax(depth - 1, position, ply + 1);
      }
      position.undoMove(move);

      totalNodes += nodes;
    }

    return totalNodes;
  }

}
