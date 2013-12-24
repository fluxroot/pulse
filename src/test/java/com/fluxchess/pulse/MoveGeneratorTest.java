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
import com.fluxchess.jcpi.models.IllegalNotationException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MoveGeneratorTest {

  private final Map<Long, Long> table = new HashMap<>();

  @Ignore
  @Test
  public void testSpecial() throws IllegalNotationException {
    GenericBoard genericBoard = new GenericBoard("r3k2R/8/8/8/8/8/8/R3K3 b Qq - 0 1");
    Board board = new Board(genericBoard);
    MoveGenerator moveGenerator = new MoveGenerator(board);

    MoveList moves = moveGenerator.getAll();

    for (int i = 0; i < moves.size; ++i) {
      System.out.println(Move.toGenericMove(moves.moves[i]).toString());
    }
  }

  @Test
  public void testPerft() throws IOException, IllegalNotationException {
    for (int i = 1; i < 5; i++) {
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
            table.clear();

            long result = miniMax(board, new MoveGenerator(board), depth);
            assertEquals(tokens[0].trim() + " at depth " + depth + "failed", nodes, result);
          }

          line = file.readLine();
        }
      }
    }
  }

  private long miniMax(Board board, MoveGenerator moveGenerator, int depth) {
    if (depth == 0) {
      return 1;
    }

    if (table.containsKey(board.zobristCode)) {
      return table.get(board.zobristCode);
    }

    long totalNodes = 0;

    MoveList moves = moveGenerator.getAll();
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.moves[i];

      board.makeMove(move);
      long nodes = miniMax(board, moveGenerator, depth - 1);
      board.undoMove(move);

      totalNodes += nodes;
    }

    table.put(board.zobristCode, totalNodes);

    return totalNodes;
  }

}
