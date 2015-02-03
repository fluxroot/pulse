/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_PROTOCOL_H
#define PULSE_PROTOCOL_H

#include "movelist.h"

namespace pulse {

class Protocol {
public:
  virtual ~Protocol() {};

  virtual void sendBestMove(int bestMove, int ponderMove) = 0;
  virtual void sendStatus(
    int currentDepth, int currentMaxDepth, uint64_t totalNodes, int currentMove, int currentMoveNumber) = 0;
  virtual void sendStatus(
    bool force, int currentDepth, int currentMaxDepth, uint64_t totalNodes, int currentMove, int currentMoveNumber) = 0;
  virtual void sendMove(RootEntry entry, int currentDepth, int currentMaxDepth, uint64_t totalNodes) = 0;
};

}

#endif
