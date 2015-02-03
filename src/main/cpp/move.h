/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_MOVE_H
#define PULSE_MOVE_H

#include "square.h"
#include "piece.h"
#include "piecetype.h"
#include "movetype.h"

namespace pulse {

/**
 * This class represents a move as a int value. The fields are represented by
 * the following bits.
 * <p/>
 * <code> 0 -  2</code>: type (required)
 * <code> 3 -  9</code>: origin square (required)
 * <code>10 - 16</code>: target square (required)
 * <code>17 - 21</code>: origin piece (required)
 * <code>22 - 26</code>: target piece (optional)
 * <code>27 - 29</code>: promotion type (optional)
 */
class Move {
private:
  // These are our bit masks
  static const int TYPE_SHIFT = 0;
  static const int TYPE_MASK = MoveType::MASK << TYPE_SHIFT;
  static const int ORIGIN_SQUARE_SHIFT = 3;
  static const int ORIGIN_SQUARE_MASK = Square::MASK << ORIGIN_SQUARE_SHIFT;
  static const int TARGET_SQUARE_SHIFT = 10;
  static const int TARGET_SQUARE_MASK = Square::MASK << TARGET_SQUARE_SHIFT;
  static const int ORIGIN_PIECE_SHIFT = 17;
  static const int ORIGIN_PIECE_MASK = Piece::MASK << ORIGIN_PIECE_SHIFT;
  static const int TARGET_PIECE_SHIFT = 22;
  static const int TARGET_PIECE_MASK = Piece::MASK << TARGET_PIECE_SHIFT;
  static const int PROMOTION_SHIFT = 27;
  static const int PROMOTION_MASK = PieceType::MASK << PROMOTION_SHIFT;

public:
  // We don't use 0 as a null value to protect against errors.
  static const int NOMOVE = (MoveType::NOMOVETYPE << TYPE_SHIFT)
      | (Square::NOSQUARE << ORIGIN_SQUARE_SHIFT)
      | (Square::NOSQUARE << TARGET_SQUARE_SHIFT)
      | (Piece::NOPIECE << ORIGIN_PIECE_SHIFT)
      | (Piece::NOPIECE << TARGET_PIECE_SHIFT)
      | (PieceType::NOPIECETYPE << PROMOTION_SHIFT);

  static int valueOf(int type, int originSquare, int targetSquare, int originPiece, int targetPiece, int promotion);
  static int getType(int move);
  static int getOriginSquare(int move);
  static int getTargetSquare(int move);
  static int getOriginPiece(int move);
  static int getTargetPiece(int move);
  static int getPromotion(int move);

private:
  Move();
  ~Move();
};

}

#endif
