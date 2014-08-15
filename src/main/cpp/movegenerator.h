/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_MOVEGENERATOR_H
#define PULSE_MOVEGENERATOR_H

#include "board.h"
#include "movelist.h"

namespace pulse {

class MoveGenerator {
public:
  MoveList& getLegalMoves(Board& board, int depth, bool isCheck);
  MoveList& getMoves(Board& board, int depth, bool isCheck);

private:
  MoveList moves;

  void addMoves(MoveList& list, Board& board);
  void addMoves(MoveList& list, int originSquare, const std::vector<int>& moveDelta, Board& board);
  void addPawnMoves(MoveList& list, int pawnSquare, Board& board);
  void addCastlingMoves(MoveList& list, int kingSquare, Board& board);
};

}

#endif
