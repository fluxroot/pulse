/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "pulse.h"
#include "perft.h"

#include <string>
#include <iostream>

int main(int argc, char* argv[]) {
  try {
    if (argc == 2) {
      std::string token(argv[1]);
      if (token == "perft") {
        std::unique_ptr<pulse::Perft> perft(new pulse::Perft());
        perft->run();
      }
    } else {
      std::unique_ptr<pulse::Pulse> pulse(new pulse::Pulse());
      pulse->run();
    }
  } catch (std::exception& e) {
    std::cout << "Exiting Pulse due to an exception: " << e.what() << std::endl;
    return 1;
  }
}
