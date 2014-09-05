/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_POSITION_H
#define PULSE_POSITION_H

#include "bitboard.h"
#include "color.h"
#include "castling.h"
#include "square.h"
#include "piece.h"
#include "depth.h"

#include <random>

namespace pulse {

class Position {
public:
  std::array<int, Square::LENGTH> board;

  std::array<Bitboard, Color::SIZE> pawns;
  std::array<Bitboard, Color::SIZE> knights;
  std::array<Bitboard, Color::SIZE> bishops;
  std::array<Bitboard, Color::SIZE> rooks;
  std::array<Bitboard, Color::SIZE> queens;
  std::array<Bitboard, Color::SIZE> kings;

  std::array<int, Color::SIZE> material = {};

  int castlingRights = Castling::NOCASTLING;
  int enPassantSquare = Square::NOSQUARE;
  int activeColor = Color::WHITE;
  int halfmoveClock = 0;

  uint64_t zobristKey = 0;

  Position();
  Position(const Position& position);

  Position& operator=(const Position& position);
  bool operator==(const Position& position) const;
  bool operator!=(const Position& position) const;

  void setActiveColor(int activeColor);
  void setCastlingRight(int castling);
  void setEnPassantSquare(int enPassantSquare);
  void setHalfmoveClock(int halfmoveClock);
  int getFullmoveNumber() const;
  void setFullmoveNumber(int fullmoveNumber);
  bool isRepetition();
  bool hasInsufficientMaterial();
  void put(int piece, int square);
  int remove(int square);
  void makeMove(int move);
  void undoMove(int move);
  bool isCheck();
  bool isCheck(int color);
  bool isAttacked(int targetSquare, int attackerColor);

private:
  class Zobrist {
  public:
    std::array<std::array<uint64_t, Square::LENGTH>, Piece::SIZE> board;
    std::array<uint64_t, Castling::VALUES_LENGTH> castlingRights;
    std::array<uint64_t, Square::LENGTH> enPassantSquare;
    uint64_t activeColor;

    static Zobrist& instance();
  private:
    std::independent_bits_engine<std::mt19937, 8, uint64_t> generator;

    Zobrist();

    uint64_t next();
  };

  class State {
  public:
    uint64_t zobristKey = 0;
    int castlingRights = Castling::NOCASTLING;
    int enPassantSquare = Square::NOSQUARE;
    int halfmoveClock = 0;
  };

  static const int MAX_MOVES = Depth::MAX_PLY + 1024;

  int halfmoveNumber = 2;

  // We will save some position parameters in a State before making a move.
  // Later we will restore them before undoing a move.
  std::array<State, MAX_MOVES> states;
  int statesSize = 0;

  Zobrist& zobrist;

  void clearCastling(int square);
  bool isAttacked(int targetSquare, int attackerPiece, const std::vector<int>& moveDelta);
  bool isAttacked(int targetSquare, int attackerPiece, int queenPiece, const std::vector<int>& moveDelta);
};

}

#endif
