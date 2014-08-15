/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "pulse.h"

#include <memory>

int main(int argc, char* argv[]) {
  std::unique_ptr<pulse::Pulse> pulse(new pulse::Pulse());
  pulse->run();
}
