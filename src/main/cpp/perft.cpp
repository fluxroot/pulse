/*
 * Copyright (C) 2013-2015 Phokham Nonava
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
  std::unique_ptr<Position> position(new Position(Notation::toPosition(Notation::STANDARDPOSITION)));
  int depth = MAX_DEPTH;

  std::cout << "Testing " << Notation::fromPosition(*position) << " at depth " << depth << std::endl;

  auto startTime = std::chrono::system_clock::now();
  uint64_t result = miniMax(depth, *position, 0);
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

uint64_t Perft::miniMax(int depth, Position& position, int ply) {
  if (depth == 0) {
    return 1;
  }

  uint64_t totalNodes = 0;

  bool isCheck = position.isCheck();
  MoveGenerator& moveGenerator = moveGenerators[ply];
  MoveList<MoveEntry>& moves = moveGenerator.getMoves(position, depth, isCheck);
  for (int i = 0; i < moves.size; ++i) {
    int move = moves.entries[i]->move;

    position.makeMove(move);
    if (!position.isCheck(Color::opposite(position.activeColor))) {
      totalNodes += miniMax(depth - 1, position, ply + 1);
    }
    position.undoMove(move);
  }

  return totalNodes;
}

}
