/*
 * Copyright (C) 2013-2014 Phokham Nonava
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
  static const int ORIGINSQUARE_SHIFT = 3;
  static const int ORIGINSQUARE_MASK = Square::MASK << ORIGINSQUARE_SHIFT;
  static const int TARGETSQUARE_SHIFT = 10;
  static const int TARGETSQUARE_MASK = Square::MASK << TARGETSQUARE_SHIFT;
  static const int ORIGINPIECE_SHIFT = 17;
  static const int ORIGINPIECE_MASK = Piece::MASK << ORIGINPIECE_SHIFT;
  static const int TARGETPIECE_SHIFT = 22;
  static const int TARGETPIECE_MASK = Piece::MASK << TARGETPIECE_SHIFT;
  static const int PROMOTION_SHIFT = 27;
  static const int PROMOTION_MASK = PieceType::MASK << PROMOTION_SHIFT;

public:
  // We don't use 0 as a null value to protect against errors.
  static const int NOMOVE = (MoveType::NOMOVETYPE << TYPE_SHIFT)
      | (Square::NOSQUARE << ORIGINSQUARE_SHIFT)
      | (Square::NOSQUARE << TARGETSQUARE_SHIFT)
      | (Piece::NOPIECE << ORIGINPIECE_SHIFT)
      | (Piece::NOPIECE << TARGETPIECE_SHIFT)
      | (PieceType::NOPIECETYPE << PROMOTION_SHIFT);

  static std::string toNotation(int move);
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
