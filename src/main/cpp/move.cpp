/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "move.h"

#include <cassert>
#include <cctype>

namespace pulse {

std::string Move::toNotation(int move) {
  std::string notation;

  notation += Square::toNotation(getOriginSquare(move));
  notation += Square::toNotation(getTargetSquare(move));

  int promotion = getPromotion(move);
  if (promotion != PieceType::NOPIECETYPE) {
    int color = Piece::getColor(getOriginPiece(move));
    notation += std::tolower(PieceType::toNotation(promotion, color));
  }

  return notation;
}

int Move::valueOf(int type, int originSquare, int targetSquare, int originPiece, int targetPiece, int promotion) {
  int move = 0;

  // Encode type
  assert(MoveType::isValid(type));
  move |= type << TYPE_SHIFT;

  // Encode origin square
  assert(Square::isValid(originSquare));
  move |= originSquare << ORIGINSQUARE_SHIFT;

  // Encode target square
  assert(Square::isValid(targetSquare));
  move |= targetSquare << TARGETSQUARE_SHIFT;

  // Encode origin piece
  assert(Piece::isValid(originPiece));
  move |= originPiece << ORIGINPIECE_SHIFT;

  // Encode target piece
  assert(Piece::isValid(targetPiece) || targetPiece == Piece::NOPIECE);
  move |= targetPiece << TARGETPIECE_SHIFT;

  // Encode promotion
  assert(PieceType::isValidPromotion(promotion) || promotion == PieceType::NOPIECETYPE);
  move |= promotion << PROMOTION_SHIFT;

  return move;
}

int Move::getType(int move) {
  int type = (move & TYPE_MASK) >> TYPE_SHIFT;
  assert(MoveType::isValid(type));

  return type;
}

int Move::getOriginSquare(int move) {
  int originSquare = (move & ORIGINSQUARE_MASK) >> ORIGINSQUARE_SHIFT;
  assert(Square::isValid(originSquare));

  return originSquare;
}

int Move::getTargetSquare(int move) {
  int targetSquare = (move & TARGETSQUARE_MASK) >> TARGETSQUARE_SHIFT;
  assert(Square::isValid(targetSquare));

  return targetSquare;
}

int Move::getOriginPiece(int move) {
  int originPiece = (move & ORIGINPIECE_MASK) >> ORIGINPIECE_SHIFT;
  assert(Piece::isValid(originPiece));

  return originPiece;
}

int Move::getTargetPiece(int move) {
  int targetPiece = (move & TARGETPIECE_MASK) >> TARGETPIECE_SHIFT;
  assert(Piece::isValid(targetPiece) || targetPiece == Piece::NOPIECE);

  return targetPiece;
}

int Move::getPromotion(int move) {
  int promotion = (move & PROMOTION_MASK) >> PROMOTION_SHIFT;
  assert(PieceType::isValidPromotion(promotion) || promotion == PieceType::NOPIECETYPE);

  return promotion;
}

}
