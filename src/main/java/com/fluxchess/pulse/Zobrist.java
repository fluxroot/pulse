/*
 * Copyright (C) 2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import java.security.SecureRandom;

final class Zobrist {

  private static final SecureRandom random = new SecureRandom();

  static final long[][] board = new long[Piece.values.length][Board.BOARDSIZE];
  static final long[] castlingRights = new long[Castling.values.length];
  static final long[] enPassantSquare = new long[Board.BOARDSIZE];
  static final long activeColor = next();

  // Initialize the zobrist keys
  static {
    for (int piece : Piece.values) {
      for (int i = 0; i < Board.BOARDSIZE; ++i) {
        board[piece][i] = next();
      }
    }

    for (int castling : Castling.values) {
      castlingRights[castling] = next();
    }

    for (int i = 0; i < Board.BOARDSIZE; ++i) {
      enPassantSquare[i] = next();
    }
  }

  private Zobrist() {
  }

  private static long next() {
    byte[] bytes = new byte[16];
    random.nextBytes(bytes);

    long hash = 0L;
    for (int i = 0; i < bytes.length; ++i) {
      hash ^= ((long) (bytes[i] & 0xFF)) << ((i * 8) % 64);
    }

    return hash;
  }

}
