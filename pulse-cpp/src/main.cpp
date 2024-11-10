// Copyright 2013-2023 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "pulse.h"
#include "perft.h"

#include <iostream>

void printUsage() {
	std::cerr << "Usage: pulse-cpp [perft]" << std::endl;
}

int main(int argc, char* argv[]) {
	if (argc == 1) {
		std::unique_ptr<pulse::Pulse> pulse(new pulse::Pulse());
		pulse->run();
	} else if (argc == 2) {
		std::string token(argv[1]);
		if (token == "perft") {
			std::unique_ptr<pulse::Perft> perft(new pulse::Perft());
			perft->run();
		} else {
			printUsage();
			return 1;
		}
	} else {
		printUsage();
		return 1;
	}
	return 0;
}
