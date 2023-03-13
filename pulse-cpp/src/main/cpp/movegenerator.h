// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.
#pragma once

#include "position.h"
#include "movelist.h"

namespace pulse {

class MoveGenerator final {
public:
	MoveList<MoveEntry>& getLegalMoves(Position& position, int depth, bool isCheck);

	MoveList<MoveEntry>& getMoves(Position& position, int depth, bool isCheck);

private:
	MoveList<MoveEntry> moves;

	static void addMoves(MoveList<MoveEntry>& list, Position& position);

	static void
	addMoves(MoveList<MoveEntry>& list, int originSquare, const std::vector<int>& directions, Position& position);

	static void addPawnMoves(MoveList<MoveEntry>& list, int pawnSquare, Position& position);

	static void addCastlingMoves(MoveList<MoveEntry>& list, int kingSquare, Position& position);
};
}
