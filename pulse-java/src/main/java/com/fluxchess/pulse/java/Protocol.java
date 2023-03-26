/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java;

interface Protocol {

	void sendBestMove(int bestMove, int ponderMove);

	void sendStatus(int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber);

	void sendStatus(boolean force, int currentDepth, int currentMaxDepth, long totalNodes, int currentMove, int currentMoveNumber);

	void sendMove(MoveList.RootEntry entry, int currentDepth, int currentMaxDepth, long totalNodes);

	void sendInfo(String message);

	void sendDebug(String message);
}
