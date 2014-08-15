/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_PULSE_H
#define PULSE_PULSE_H

#include "search.h"

namespace pulse {

class Pulse {
public:
  void run();

private:
  // We have to maintain at least the state of the board and the search.
  std::unique_ptr<Board> board = std::unique_ptr<Board>(new Board(Board::STANDARDBOARD));
  std::unique_ptr<Search> search = Search::newInfiniteSearch(*board);

  void receiveInitialize();
  void receiveReady();
  void receiveNewGame();
  void receivePosition(std::istringstream& input);
  void receiveGo(std::istringstream& input);
  void receivePonderHit();
  void receiveStop();
};

}

#endif
