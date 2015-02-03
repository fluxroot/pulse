/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_NOTATION_H
#define PULSE_NOTATION_H

#include "position.h"

#include <array>

namespace pulse {

class Notation {
public:
  static const std::string STANDARDPOSITION;

  static Position toPosition(const std::string& fen);
  static std::string fromPosition(const Position& position);
  static int toColor(char notation);
  static char fromColor(int color);
  static int toPieceType(char notation);
  static char fromPieceType(int piecetype);
  static int toPiece(char notation);
  static char fromPiece(int piece);
  static int toCastlingType(char notation);
  static char fromCastlingType(int castlingtype);
  static int toCastling(char notation);
  static char fromCastling(int castling);
  static int toFile(char notation);
  static char fromFile(int file);
  static int toRank(char notation);
  static char fromRank(int rank);
  static int toSquare(const std::string& notation);
  static std::string fromSquare(int square);

private:
  static const char WHITE_NOTATION = 'w';
  static const char BLACK_NOTATION = 'b';

  static const char PAWN_NOTATION = 'P';
  static const char KNIGHT_NOTATION = 'N';
  static const char BISHOP_NOTATION = 'B';
  static const char ROOK_NOTATION = 'R';
  static const char QUEEN_NOTATION = 'Q';
  static const char KING_NOTATION = 'K';

  static const char KINGSIDE_NOTATION = 'K';
  static const char QUEENSIDE_NOTATION = 'Q';

  static const char a_NOTATION = 'a';
  static const char b_NOTATION = 'b';
  static const char c_NOTATION = 'c';
  static const char d_NOTATION = 'd';
  static const char e_NOTATION = 'e';
  static const char f_NOTATION = 'f';
  static const char g_NOTATION = 'g';
  static const char h_NOTATION = 'h';

  static const char r1_NOTATION = '1';
  static const char r2_NOTATION = '2';
  static const char r3_NOTATION = '3';
  static const char r4_NOTATION = '4';
  static const char r5_NOTATION = '5';
  static const char r6_NOTATION = '6';
  static const char r7_NOTATION = '7';
  static const char r8_NOTATION = '8';

  Notation();
  ~Notation();

  static int colorOf(char notation);
  static char transform(char notation, int color);
};

}

#endif
