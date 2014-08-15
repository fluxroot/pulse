/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "piecetype.h"
#include "color.h"

#include <cassert>
#include <cctype>

namespace pulse {

const std::array<int, PieceType::SIZE> PieceType::values = {
  PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
};

bool PieceType::isValid(int pieceType) {
  switch (pieceType) {
    case PAWN:
    case KNIGHT:
    case BISHOP:
    case ROOK:
    case QUEEN:
    case KING:
      return true;
    case NOPIECETYPE:
    default:
      return false;
  }
}

int PieceType::fromNotation(char notation) {
  char uppercaseNotation = std::toupper(notation);
  switch (uppercaseNotation) {
    case PAWN_NOTATION:
      return PAWN;
    case KNIGHT_NOTATION:
      return KNIGHT;
    case BISHOP_NOTATION:
      return BISHOP;
    case ROOK_NOTATION:
      return ROOK;
    case QUEEN_NOTATION:
      return QUEEN;
    case KING_NOTATION:
      return KING;
    default:
      return NOPIECETYPE;
  }
}

char PieceType::toNotation(int piecetype, int color) {
  assert(Color::isValid(color));

  switch (piecetype) {
    case PAWN:
      return Color::transform(PAWN_NOTATION, color);
    case KNIGHT:
      return Color::transform(KNIGHT_NOTATION, color);
    case BISHOP:
      return Color::transform(BISHOP_NOTATION, color);
    case ROOK:
      return Color::transform(ROOK_NOTATION, color);
    case QUEEN:
      return Color::transform(QUEEN_NOTATION, color);
    case KING:
      return Color::transform(KING_NOTATION, color);
    case NOPIECETYPE:
    default:
      throw std::exception();
  }
}

bool PieceType::isValidPromotion(int pieceType) {
  switch (pieceType) {
    case KNIGHT:
    case BISHOP:
    case ROOK:
    case QUEEN:
      return true;
    case PAWN:
    case KING:
    case NOPIECETYPE:
    default:
      return false;
  }
}

bool PieceType::isSliding(int pieceType) {
  switch (pieceType) {
    case BISHOP:
    case ROOK:
    case QUEEN:
      return true;
    case PAWN:
    case KNIGHT:
    case KING:
      return false;
    case NOPIECETYPE:
    default:
      throw std::exception();
  }
}

int PieceType::getValue(int pieceType) {
  switch (pieceType) {
    case PAWN:
      return PAWN_VALUE;
    case KNIGHT:
      return KNIGHT_VALUE;
    case BISHOP:
      return BISHOP_VALUE;
    case ROOK:
      return ROOK_VALUE;
    case QUEEN:
      return QUEEN_VALUE;
    case KING:
      return KING_VALUE;
    case NOPIECETYPE:
    default:
      throw std::exception();
  }
}

}
