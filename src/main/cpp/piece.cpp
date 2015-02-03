/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "piece.h"
#include "color.h"
#include "piecetype.h"

#include <cassert>

namespace pulse {

const std::array<int, Piece::VALUES_SIZE> Piece::values = {
  WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING,
  BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING
};

bool Piece::isValid(int piece) {
  switch (piece) {
    case WHITE_PAWN:
    case WHITE_KNIGHT:
    case WHITE_BISHOP:
    case WHITE_ROOK:
    case WHITE_QUEEN:
    case WHITE_KING:
    case BLACK_PAWN:
    case BLACK_KNIGHT:
    case BLACK_BISHOP:
    case BLACK_ROOK:
    case BLACK_QUEEN:
    case BLACK_KING:
      return true;
    default:
      return false;
  }
}

int Piece::valueOf(int color, int piecetype) {
  switch (color) {
    case Color::WHITE:
      switch (piecetype) {
        case PieceType::PAWN:
          return WHITE_PAWN;
        case PieceType::KNIGHT:
          return WHITE_KNIGHT;
        case PieceType::BISHOP:
          return WHITE_BISHOP;
        case PieceType::ROOK:
          return WHITE_ROOK;
        case PieceType::QUEEN:
          return WHITE_QUEEN;
        case PieceType::KING:
          return WHITE_KING;
        default:
          throw std::exception();
      }
    case Color::BLACK:
      switch (piecetype) {
        case PieceType::PAWN:
          return BLACK_PAWN;
        case PieceType::KNIGHT:
          return BLACK_KNIGHT;
        case PieceType::BISHOP:
          return BLACK_BISHOP;
        case PieceType::ROOK:
          return BLACK_ROOK;
        case PieceType::QUEEN:
          return BLACK_QUEEN;
        case PieceType::KING:
          return BLACK_KING;
        default:
          throw std::exception();
      }
    default:
      throw std::exception();
  }
}

int Piece::getType(int piece) {
  switch (piece) {
    case WHITE_PAWN:
    case BLACK_PAWN:
      return PieceType::PAWN;
    case WHITE_KNIGHT:
    case BLACK_KNIGHT:
      return PieceType::KNIGHT;
    case WHITE_BISHOP:
    case BLACK_BISHOP:
      return PieceType::BISHOP;
    case WHITE_ROOK:
    case BLACK_ROOK:
      return PieceType::ROOK;
    case WHITE_QUEEN:
    case BLACK_QUEEN:
      return PieceType::QUEEN;
    case WHITE_KING:
    case BLACK_KING:
      return PieceType::KING;
    default:
      throw std::exception();
  }
}

int Piece::getColor(int piece) {
  switch (piece) {
    case WHITE_PAWN:
    case WHITE_KNIGHT:
    case WHITE_BISHOP:
    case WHITE_ROOK:
    case WHITE_QUEEN:
    case WHITE_KING:
      return Color::WHITE;
    case BLACK_PAWN:
    case BLACK_KNIGHT:
    case BLACK_BISHOP:
    case BLACK_ROOK:
    case BLACK_QUEEN:
    case BLACK_KING:
      return Color::BLACK;
    default:
      throw std::exception();
  }
}

}
