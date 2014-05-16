/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

/**
 * This class stores our moves for a specific position. For the root node we
 * will populate pv for every root move.
 */
final class MoveList {

  private static final int MAX_MOVES = 256;

  final Entry[] entries = new Entry[MAX_MOVES];
  int size = 0;

  static final class Entry {
    int move = Move.NOMOVE;
    int value = Evaluation.NOVALUE;
    final MoveVariation pv = new MoveVariation();
  }

  static final class MoveVariation {
    final int[] moves = new int[Search.MAX_PLY];
    int size = 0;
  }

  MoveList() {
    for (int i = 0; i < MAX_MOVES; ++i) {
      entries[i] = new Entry();
    }
  }

  /**
   * Sorts the move list using a stable insertion sort.
   */
  void sort() {
    for (int i = 1; i < size; ++i) {
      Entry entry = entries[i];

      int j = i;
      while ((j > 0) && (entries[j - 1].value < entry.value)) {
        entries[j] = entries[j - 1];
        --j;
      }

      entries[j] = entry;
    }
  }

  /**
   * Rates the moves in the list according to "Most Valuable Victim - Least Valuable Aggressor".
   */
  void rateFromMVVLVA() {
    for (int i = 0; i < size; ++i) {
      int move = entries[i].move;
      int value = 0;

      int pieceTypeValue = Evaluation.getPieceTypeValue(Piece.getType(Move.getOriginPiece(move)));
      value += Evaluation.KING_VALUE / pieceTypeValue;

      int target = Move.getTargetPiece(move);
      if (Piece.isValid(target)) {
        value += 10 * Evaluation.getPieceTypeValue(Piece.getType(target));
      }

      assert value >= (Evaluation.KING_VALUE / Evaluation.KING_VALUE)
          && value <= (Evaluation.KING_VALUE / Evaluation.PAWN_VALUE) + 10 * Evaluation.QUEEN_VALUE;

      entries[i].value = value;
    }
  }

}
