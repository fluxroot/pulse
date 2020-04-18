/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

#include "position.h"

#include <array>

namespace pulse::notation {

constexpr auto STANDARDPOSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

Position toPosition(const std::string& fen);

std::string fromPosition(const Position& position);

int toColor(char notation);

char fromColor(int color);

int toPieceType(char notation);

char fromPieceType(int piecetype);

int toPiece(char notation);

char fromPiece(int piece);

int toCastlingType(char notation);

char fromCastlingType(int castlingtype);

int toCastling(char notation);

char fromCastling(int castling);

int toFile(char notation);

char fromFile(int file);

int toRank(char notation);

char fromRank(int rank);

std::string fromSquare(int square);
}
