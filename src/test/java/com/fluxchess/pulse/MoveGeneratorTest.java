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
import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.jcpi.models.IllegalNotationException;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class MoveGeneratorTest {

  @Test
  public void testPerft() throws IOException, IllegalNotationException {
    for (int i = 1; i < 4; i++) {
      try (InputStream inputStream = MoveGeneratorTest.class.getResourceAsStream("/perftsuite.epd")) {
        BufferedReader file = new BufferedReader(new InputStreamReader(inputStream));

        String line = file.readLine();
        while (line != null) {
          String[] tokens = line.split(";");

          if (tokens.length > i) {
            String[] data = tokens[i].trim().split(" ");
            int depth = Integer.parseInt(data[0].substring(1));
            int nodes = Integer.parseInt(data[1]);

            GenericBoard genericBoard = new GenericBoard(tokens[0].trim());
            Board board = new Board(genericBoard);

            long result = miniMax(depth, board, 0);
            if (nodes != result) {
              throw new AssertionError(findMissingMoves(depth, board, 0));
            }
          }

          line = file.readLine();
        }
      }
    }
  }

  private long miniMax(int depth, Board board, int ply) {
    long totalNodes = 0;

    MoveList moves = getAll(depth, board, ply);

    if (depth <= 1) {
      return moves.size;
    }

    for (int i = 0; i < moves.size; ++i) {
      int move = moves.entries[i].move;

      board.makeMove(move);
      totalNodes += miniMax(depth - 1, board, ply + 1);
      board.undoMove(move);
    }

    return totalNodes;
  }

  private String findMissingMoves(int depth, Board board, int ply) {
    String message = "";

    // Get expected moves from JCPI
    GenericBoard genericBoard = board.toGenericBoard();
    Collection<GenericMove> expectedMoves = new HashSet<>(Arrays.asList(
        com.fluxchess.jcpi.utils.MoveGenerator.getGenericMoves(genericBoard)
    ));

    // Get actual moves
    MoveList moves = getAll(depth, board, ply);
    Collection<GenericMove> actualMoves = new HashSet<>();
    for (int i = 0; i < moves.size; ++i) {
      actualMoves.add(Move.toGenericMove(moves.entries[i].move));
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

        board.makeMove(move);
        message += findMissingMoves(depth - 1, board, ply + 1);
        board.undoMove(move);

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

  private MoveList getAll(int depth, Board board, int ply) {
    MoveList moves = new MoveList();

    boolean isCheck = board.isCheck();
    MoveGenerator moveGenerator = MoveGenerator.getMoveGenerator(board, depth, ply, isCheck);
    int move;
    while ((move = moveGenerator.nextLegal()) != Move.NOMOVE) {
      moves.entries[moves.size++].move = move;
    }

    return moves;
  }

}
