/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#ifndef PULSE_DEPTH_H
#define PULSE_DEPTH_H

namespace pulse {

class Depth {
public:
  static const int MAX_PLY = 256;
  static const int MAX_DEPTH = 64;

private:
  Depth();
  ~Depth();
};

}

#endif
