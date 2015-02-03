/*
 * Copyright (C) 2013-2015 Phokham Nonava
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
  MoveList<MoveEntry>& getLegalMoves(Position& position, int depth, bool isCheck);
  MoveList<MoveEntry>& getMoves(Position& position, int depth, bool isCheck);

private:
  MoveList<MoveEntry> moves;

  void addMoves(MoveList<MoveEntry>& list, Position& position);
  void addMoves(MoveList<MoveEntry>& list, int originSquare, const std::vector<int>& directions, Position& position);
  void addPawnMoves(MoveList<MoveEntry>& list, int pawnSquare, Position& position);
  void addCastlingMoves(MoveList<MoveEntry>& list, int kingSquare, Position& position);
};

}

#endif
