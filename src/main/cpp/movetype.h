/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
#pragma once

namespace pulse {

class MoveType {
public:
	static const int MASK = 0x7;

	static const int NORMAL = 0;
	static const int PAWNDOUBLE = 1;
	static const int PAWNPROMOTION = 2;
	static const int ENPASSANT = 3;
	static const int CASTLING = 4;

	static const int NOMOVETYPE = 5;

private:
	MoveType();

	~MoveType();
};

}
