/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

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
