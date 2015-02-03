/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_PULSE_H
#define PULSE_PULSE_H

#include "search.h"
#include "notation.h"

namespace pulse {

class Pulse : public Protocol {
public:
  void run();

  virtual void sendBestMove(int bestMove, int ponderMove);
  virtual void sendStatus(
    int currentDepth, int currentMaxDepth, uint64_t totalNodes, int currentMove, int currentMoveNumber);
  virtual void sendStatus(
    bool force, int currentDepth, int currentMaxDepth, uint64_t totalNodes, int currentMove, int currentMoveNumber);
  virtual void sendMove(RootEntry entry, int currentDepth, int currentMaxDepth, uint64_t totalNodes);

  static std::string fromMove(int move);

private:
  std::unique_ptr<Search> search = std::unique_ptr<Search>(new Search(*this));
  std::chrono::system_clock::time_point startTime;
  std::chrono::system_clock::time_point statusStartTime;

  std::unique_ptr<Position> currentPosition = std::unique_ptr<Position>(new Position(Notation::toPosition(Notation::STANDARDPOSITION)));

  void receiveInitialize();
  void receiveReady();
  void receiveNewGame();
  void receivePosition(std::istringstream& input);
  void receiveGo(std::istringstream& input);
  void receivePonderHit();
  void receiveStop();
  void receiveQuit();
};

}

#endif
