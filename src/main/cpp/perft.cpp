/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "perft.h"
#include "notation.h"

#include <iostream>
#include <iomanip>
#include <string>
#include <chrono>
#include <memory>

namespace pulse {

void Perft::run() {
  std::unique_ptr<Board> board(new Board(Notation::toBoard(Notation::STANDARDBOARD)));
  int depth = MAX_DEPTH;

  std::cout << "Testing " << Notation::fromBoard(*board) << " at depth " << depth << std::endl;

  auto startTime = std::chrono::system_clock::now();
  uint64_t result = miniMax(depth, *board, 0);
  auto endTime = std::chrono::system_clock::now();

  auto duration = endTime - startTime;

  std::cout << "Nodes: ";
  std::cout << result << std::endl;
  std::cout << "Duration: ";
  std::cout << std::setfill('0') << std::setw(2)
    << std::chrono::duration_cast<std::chrono::hours>(duration).count() << ":";
  std::cout << std::setfill('0') << std::setw(2)
    << (std::chrono::duration_cast<std::chrono::minutes>(duration)
    - std::chrono::duration_cast<std::chrono::minutes>(std::chrono::duration_cast<std::chrono::hours>(duration))).count() << ":";
  std::cout << std::setfill('0') << std::setw(2)
    << (std::chrono::duration_cast<std::chrono::seconds>(duration)
    - std::chrono::duration_cast<std::chrono::seconds>(std::chrono::duration_cast<std::chrono::minutes>(duration))).count() << ".";
  std::cout << std::setfill('0') << std::setw(2)
    << (std::chrono::duration_cast<std::chrono::milliseconds>(duration)
    - std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::duration_cast<std::chrono::seconds>(duration))).count();
  std::cout << std::endl;

  std::cout << "n/ms: "
    << result / std::chrono::duration_cast<std::chrono::milliseconds>(duration).count() << std::endl;
}

uint64_t Perft::miniMax(int depth, Board& board, int ply) {
  if (depth == 0) {
    return 1;
  }

  uint64_t totalNodes = 0;

  bool isCheck = board.isCheck();
  MoveGenerator& moveGenerator = moveGenerators[ply];
  MoveList& moves = moveGenerator.getMoves(board, depth, isCheck);
  for (int i = 0; i < moves.size; ++i) {
    int move = moves.entries[i]->move;
    uint64_t nodes = 0;

    board.makeMove(move);
    if (!board.isCheck(Color::opposite(board.activeColor))) {
      nodes = miniMax(depth - 1, board, ply + 1);
    }
    board.undoMove(move);

    totalNodes += nodes;
  }

  return totalNodes;
}

}
