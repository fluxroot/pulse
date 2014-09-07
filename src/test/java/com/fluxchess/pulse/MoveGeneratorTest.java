/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericMove;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static com.fluxchess.pulse.MoveList.MoveEntry;

public class MoveGeneratorTest {

  private static final int MAX_DEPTH = 6;
  private static final MoveGenerator[] moveGenerators = new MoveGenerator[MAX_DEPTH];

  @BeforeClass
  public static void setUpClass() {
    for (int i = 0; i < MAX_DEPTH; ++i) {
      moveGenerators[i] = new MoveGenerator();
    }
  }

  private long miniMax(int depth, Position position, int ply) {
    if (depth <= 0) {
      return 1;
    }

    long totalNodes = 0;

    boolean isCheck = position.isCheck();
    MoveGenerator moveGenerator = moveGenerators[ply];
    MoveList<MoveEntry> moves = moveGenerator.getMoves(position, depth, isCheck);

    for (int i = 0; i < moves.size; ++i) {
      int move = moves.entries[i].move;

      position.makeMove(move);
      if (!position.isCheck(Color.opposite(position.activeColor))) {
        totalNodes += miniMax(depth - 1, position, ply + 1);
      }
      position.undoMove(move);
    }

    return totalNodes;
  }

  @Test
  public void testPerft() throws IOException {
    for (int i = 1; i < 4; i++) {
      try (InputStream inputStream = MoveGeneratorTest.class.getResourceAsStream("/perftsuite.epd")) {
        BufferedReader file = new BufferedReader(new InputStreamReader(inputStream));

        String line = file.readLine();
        while (line != null) {
          String[] tokens = line.split(";");

          if (tokens.length > i) {
            String[] data = tokens[i].trim().split(" ");
            int depth = Integer.parseInt(data[0].substring(1));
            long nodes = Integer.parseInt(data[1]);

            Position position = Notation.toPosition(tokens[0].trim());

            long result = miniMax(depth, position, 0);
            if (nodes != result) {
              throw new AssertionError(findMissingMoves(depth, position, 0));
            }
          }

          line = file.readLine();
        }
      }
    }
  }

  private String findMissingMoves(int depth, Position position, int ply) {
    String message = "";

    // Get expected moves from JCPI
    GenericBoard genericBoard = Notation.toGenericBoard(position);
    Collection<GenericMove> expectedMoves = new HashSet<>(Arrays.asList(
        com.fluxchess.jcpi.utils.MoveGenerator.getGenericMoves(genericBoard)
    ));

    // Get actual moves
    boolean isCheck = position.isCheck();
    MoveGenerator moveGenerator = moveGenerators[ply];
    MoveList<MoveEntry> moves = moveGenerator.getLegalMoves(position, depth, isCheck);
    Collection<GenericMove> actualMoves = new HashSet<>();
    for (int i = 0; i < moves.size; ++i) {
      actualMoves.add(Jcpi.fromMove(moves.entries[i].move));
    }

    // Compare expected and actual moves
    Collection<GenericMove> invalidMoves = new HashSet<>(actualMoves);
    invalidMoves.removeAll(expectedMoves);

    Collection<GenericMove> missingMoves = new HashSet<>(expectedMoves);
    missingMoves.removeAll(actualMoves);

    if (invalidMoves.isEmpty() && missingMoves.isEmpty()) {
      if (depth <= 1) {
        return message;
      }

      for (int i = 0; i < moves.size; ++i) {
        int move = moves.entries[i].move;

        position.makeMove(move);
        message += findMissingMoves(depth - 1, position, ply + 1);
        position.undoMove(move);

        if (!message.isEmpty()) {
          break;
        }
      }
    } else {
      message += String.format("Failed check for board: %s%n", genericBoard);
      message += String.format("Expected: %s%n", expectedMoves);
      message += String.format("  Actual: %s%n", actualMoves);
      message += String.format(" Missing: %s%n", missingMoves);
      message += String.format(" Invalid: %s%n", invalidMoves);
    }

    return message;
  }

}
