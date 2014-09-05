/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_MOVEGENERATOR_H
#define PULSE_MOVEGENERATOR_H

#include "position.h"
#include "movelist.h"

namespace pulse {

class MoveGenerator {
public:
  MoveList& getLegalMoves(Position& position, int depth, bool isCheck);
  MoveList& getMoves(Position& position, int depth, bool isCheck);

private:
  MoveList moves;

  void addMoves(MoveList& list, Position& position);
  void addMoves(MoveList& list, int originSquare, const std::vector<int>& moveDelta, Position& position);
  void addPawnMoves(MoveList& list, int pawnSquare, Position& position);
  void addCastlingMoves(MoveList& list, int kingSquare, Position& position);
};

}

#endif
