/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "pulse.h"

#include <iostream>
#include <string>
#include <sstream>

namespace pulse {

void Pulse::run() {
  std::cin.exceptions(std::iostream::eofbit | std::iostream::failbit | std::iostream::badbit);
  while (true) {
    std::string line;
    std::getline(std::cin, line);
    std::istringstream input(line);

    std::string token;
    input >> std::skipws >> token;
    if (token == "uci") {
      receiveInitialize();
    } else if (token == "isready") {
      receiveReady();
    } else if (token == "ucinewgame") {
      receiveNewGame();
    } else if (token == "position") {
      receivePosition(input);
    } else if (token == "go") {
      receiveGo(input);
    } else if (token == "stop") {
      receiveStop();
    } else if (token == "ponderhit") {
      receivePonderHit();
    } else if (token == "quit") {
      receiveStop();
      break;
    }
  }
}

void Pulse::receiveInitialize() {
  receiveStop();

  // We received an initialization request.

  // We could do some global initialization here. Probably it would be best
  // to initialize all tables here as they will exist until the end of the
  // program.

  // We must send an initialization answer back!
  std::cout << "id name Pulse 1.5-cpp" << std::endl;
  std::cout << "id author Phokham Nonava" << std::endl;
  std::cout << "uciok" << std::endl;
}

void Pulse::receiveReady() {
  // We received a ready request. We must send the token back as soon as we
  // can. However, because we launch the search in a separate thread, our main
  // thread is able to handle the commands asynchronously to the search. If we
  // don't answer the ready request in time, our engine will probably be
  // killed by the GUI.
  std::cout << "readyok" << std::endl;
}

void Pulse::receiveNewGame() {
  receiveStop();

  // We received a new game command.

  // Initialize per-game settings here.
  board = std::unique_ptr<Board>(new Board(Board::STANDARDBOARD));
  search = Search::newInfiniteSearch(*board);
}

void Pulse::receivePosition(std::istringstream& input) {
  receiveStop();

  // We received an position command. Just setup the board.

  std::string token;
  input >> token;
  if (token == "startpos") {
    board = std::unique_ptr<Board>(new Board(Board::STANDARDBOARD));

    if (input >> token) {
      if (token != "moves") {
        throw std::exception();
      }
    }
  } else if (token == "fen") {
    std::string fen;

    while (input >> token) {
      if (token == "moves") {
        break;
      } else {
        fen += token + " ";
      }
    }

    board = std::unique_ptr<Board>(new Board(fen));
  } else {
    throw std::exception();
  }

  while (input >> token) {
    // Verify moves
    MoveGenerator moveGenerator;
    MoveList& moves = moveGenerator.getLegalMoves(*board, 1, board->isCheck());
    bool found = false;
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.entries[i]->move;
      if (Move::toNotation(move) == token) {
        board->makeMove(move);
        found = true;
        break;
      }
    }

    if (!found) {
      throw std::exception();
    }
  }

  // Don't start searching though!
}

void Pulse::receiveGo(std::istringstream& input) {
  receiveStop();

  // We received a start command. Extract all parameters from the
  // command and start the search.
  std::string token;
  input >> token;
  if (token == "depth") {
    int searchDepth;
    if (input >> searchDepth) {
      search = Search::newDepthSearch(*board, searchDepth);
    } else {
      throw std::exception();
    }
  } else if (token == "nodes") {
    uint64_t searchNodes;
    if (input >> searchNodes) {
      search = Search::newNodesSearch(*board, searchNodes);
    }
  } else if (token == "movetime") {
    uint64_t searchTime;
    if (input >> searchTime) {
      search = Search::newTimeSearch(*board, searchTime);
    }
  } else if (token == "infinite") {
    search = Search::newInfiniteSearch(*board);
  } else {
    uint64_t whiteTimeLeft = 1;
    uint64_t whiteTimeIncrement = 0;
    uint64_t blackTimeLeft = 1;
    uint64_t blackTimeIncrement = 0;
    int searchMovesToGo = 40;
    bool ponder = false;

    while (input >> token) {
      if (token == "wtime") {
        if (!(input >> whiteTimeLeft)) {
          throw std::exception();
        }
      }
      if (token == "winc") {
        if (!(input >> whiteTimeIncrement)) {
          throw std::exception();
        }
      }
      if (token == "btime") {
        if (!(input >> blackTimeLeft)) {
          throw std::exception();
        }
      }
      if (token == "binc") {
        if (!(input >> blackTimeIncrement)) {
          throw std::exception();
        }
      }
      if (token == "movestogo") {
        if (!(input >> searchMovesToGo)) {
          throw std::exception();
        }
      }
      if (token == "ponder") {
        ponder = true;
      }
    }

    if (ponder) {
      search = Search::newPonderSearch(
        *board,
        whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, searchMovesToGo);
    } else {
      search = Search::newClockSearch(
        *board,
        whiteTimeLeft, whiteTimeIncrement, blackTimeLeft, blackTimeIncrement, searchMovesToGo);
    }
  }

  // Go...
  search->start();
}

void Pulse::receivePonderHit() {
  // We received a ponder hit command. Just call ponderhit().
  search->ponderhit();
}

void Pulse::receiveStop() {
  // We received a stop command. If a search is running, stop it.
  search->stop();
}

}
