/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_BOARD_H
#define PULSE_BOARD_H

#include <string>

namespace pulse {

/**
 * This is our internal board.
 */
class Board {
public:
  Board(const std::string& fen);
};

}

#endif
