/*
 * Copyright (C) 2013-2016 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "pulse.h"
#include "perft.h"

#include <iostream>

int printUsage() {
	std::cout << "Usage: pulse [perft]" << std::endl;
	return 1;
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
			return printUsage();
		}
	} else {
		return printUsage();
	}
}
