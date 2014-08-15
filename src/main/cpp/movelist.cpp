/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "movelist.h"

#include <cassert>

namespace pulse {

MoveList::MoveList() {
  for (unsigned int i = 0; i < entries.size(); ++i) {
    entries[i] = std::shared_ptr<Entry>(new Entry());
  }
}

/**
 * Sorts the move list using a stable insertion sort.
 */
void MoveList::sort() {
  for (int i = 1; i < size; ++i) {
    std::shared_ptr<Entry> entry(entries[i]);

    int j = i;
    while ((j > 0) && (entries[j - 1]->value < entry->value)) {
      entries[j] = entries[j - 1];
      --j;
    }

    entries[j] = entry;
  }
}

/**
 * Rates the moves in the list according to "Most Valuable Victim - Least Valuable Aggressor".
 */
void MoveList::rateFromMVVLVA() {
  for (int i = 0; i < size; ++i) {
    int move = entries[i]->move;
    int value = 0;

    int pieceTypeValue = PieceType::getValue(Piece::getType(Move::getOriginPiece(move)));
    value += PieceType::KING_VALUE / pieceTypeValue;

    int target = Move::getTargetPiece(move);
    if (Piece::isValid(target)) {
      value += 10 * PieceType::getValue(Piece::getType(target));
    }

    assert(value >= (PieceType::KING_VALUE / PieceType::KING_VALUE)
      && value <= (PieceType::KING_VALUE / PieceType::PAWN_VALUE) + 10 * PieceType::QUEEN_VALUE);

    entries[i]->value = value;
  }
}

}
