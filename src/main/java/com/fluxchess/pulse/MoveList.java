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

import com.fluxchess.jcpi.models.IntPiece;

public final class MoveList {

  private static final int MAXSIZE = 256;

  public final Entry[] entries = new Entry[MAXSIZE];
  public int size = 0;

  public static final class Entry {
    public int move = Move.NOMOVE;
    public int value = -Evaluation.INFINITY;
    public MoveVariation pv = new MoveVariation();

    private int oldValue = -Evaluation.INFINITY;

    public void save() {
      oldValue = value;
    }

    public void restore() {
      value = oldValue;
    }
  }

  public static final class MoveVariation {
    public final int[] moves = new int[Search.MAX_HEIGHT];
    public int size = 0;
  }

  public MoveList() {
    for (int i = 0; i < MAXSIZE; ++i) {
      entries[i] = new Entry();
    }
  }

  public void save() {
    for (Entry entry : entries) {
      entry.save();
    }
  }

  public void restore() {
    for (Entry entry : entries) {
      entry.restore();
    }
  }

  /**
   * Sorts the move list using a stable insertion sort.
   */
  public void sort() {
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
  public void rateFromMVVLVA() {
    for (int i = 0; i < size; ++i) {
      int move = entries[i].move;
      int value = 0;

      int chessmanValue = Evaluation.getChessmanValue(IntPiece.getChessman(Move.getOriginPiece(move)));
      value += Evaluation.VALUE_KING / chessmanValue;

      int target = Move.getTargetPiece(move);
      if (IntPiece.isValid(target)) {
        value += 10 * Evaluation.getChessmanValue(IntPiece.getChessman(target));
      }

      assert value >= (Evaluation.VALUE_KING / Evaluation.VALUE_KING) && value <= (Evaluation.VALUE_KING / Evaluation.VALUE_PAWN) + 10 * Evaluation.VALUE_QUEEN;

      entries[i].value = value;
    }
  }

}
