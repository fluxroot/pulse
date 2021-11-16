/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

#include "square.h"
#include "piece.h"
#include "piecetype.h"
#include "movetype.h"

/**
 * A move is encoded as a int value. The fields are represented by
 * the following bits.
 * <ul>
 * <li><code> 0 -  2</code>: type (required)</li>
 * <li><code> 3 -  9</code>: origin square (required)</li>
 * <li><code>10 - 16</code>: target square (required)</li>
 * <li><code>17 - 21</code>: origin piece (required)</li>
 * <li><code>22 - 26</code>: target piece (optional)</li>
 * <li><code>27 - 29</code>: promotion type (optional)</li>
 * </ul>
 */
namespace pulse::move {
namespace {
// These are our bit masks
constexpr int TYPE_SHIFT = 0;
constexpr int TYPE_MASK = movetype::MASK << TYPE_SHIFT;
constexpr int ORIGIN_SQUARE_SHIFT = 3;
constexpr int ORIGIN_SQUARE_MASK = square::MASK << ORIGIN_SQUARE_SHIFT;
constexpr int TARGET_SQUARE_SHIFT = 10;
constexpr int TARGET_SQUARE_MASK = square::MASK << TARGET_SQUARE_SHIFT;
constexpr int ORIGIN_PIECE_SHIFT = 17;
constexpr int ORIGIN_PIECE_MASK = piece::MASK << ORIGIN_PIECE_SHIFT;
constexpr int TARGET_PIECE_SHIFT = 22;
constexpr int TARGET_PIECE_MASK = piece::MASK << TARGET_PIECE_SHIFT;
constexpr int PROMOTION_SHIFT = 27;
constexpr int PROMOTION_MASK = piecetype::MASK << PROMOTION_SHIFT;
}

// We don't use 0 as a null value to protect against errors.
constexpr int NOMOVE = (movetype::NOMOVETYPE << TYPE_SHIFT)
					   | (square::NOSQUARE << ORIGIN_SQUARE_SHIFT)
					   | (square::NOSQUARE << TARGET_SQUARE_SHIFT)
					   | (piece::NOPIECE << ORIGIN_PIECE_SHIFT)
					   | (piece::NOPIECE << TARGET_PIECE_SHIFT)
					   | (piecetype::NOPIECETYPE << PROMOTION_SHIFT);

int valueOf(int type, int originSquare, int targetSquare, int originPiece, int targetPiece, int promotion);

int getType(int move);

int getOriginSquare(int move);

int getTargetSquare(int move);

int getOriginPiece(int move);

int getTargetPiece(int move);

int getPromotion(int move);
}
