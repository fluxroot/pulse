/*
 * Copyright (C) 2013-2015 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "notation.h"
#include "file.h"
#include "rank.h"
#include "piecetype.h"
#include "castlingtype.h"

#include <cassert>
#include <string>
#include <sstream>
#include <cctype>

namespace pulse {

const std::string Notation::STANDARDPOSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
  
Position Notation::toPosition(const std::string& fen) {
  Position position;

  // Clean and split into tokens
  std::vector<std::string> tokens;
  std::stringstream ss(fen);
  std::string token;
  while (std::getline(ss, token, ' ')) {
    if (!token.empty()) {
      tokens.push_back(token);
    }
  }

  // halfmove clock and fullmove number are optional
  if (tokens.size() < 4 || tokens.size() > 6) {
    throw std::exception();
  }

  unsigned int tokensIndex = 0;

  // Parse pieces
  token = tokens[tokensIndex++];
  int file = File::a;
  int rank = Rank::r8;

  for (auto character : token) {
    int piece = toPiece(character);
    if (piece != Piece::NOPIECE) {
      if (!File::isValid(file) || !Rank::isValid(rank)) {
        throw std::invalid_argument("Illegal file or rank");
      }

      position.put(piece, Square::valueOf(file, rank));

      if (file == File::h) {
        file = File::NOFILE;
      } else {
        ++file;
      }
    } else if (character == '/') {
      if (file != File::NOFILE || rank == Rank::r1) {
        throw std::invalid_argument("Illegal file or rank");
      }

      file = File::a;
      --rank;
    } else {
      std::string s = { character };
      int emptySquares = std::stoi(s);
      if (emptySquares < 1 || 8 < emptySquares) {
        throw std::invalid_argument("Illegal number of empty squares");
      }

      file += emptySquares - 1;
      if (!File::isValid(file)) {
        throw std::invalid_argument("Illegal number of empty squares");
      }

      if (file == File::h) {
        file = File::NOFILE;
      } else {
        ++file;
      }
    }
  }

  // Parse active color
  token = tokens[tokensIndex++];

  if (token.length() != 1) {
    throw std::exception();
  }

  int activeColor = toColor(token[0]);
  if (activeColor == Color::NOCOLOR) {
    throw std::exception();
  }
  position.setActiveColor(activeColor);

  // Parse castling rights
  token = tokens[tokensIndex++];

  if (token.compare("-") != 0) {
    for (auto character : token) {
      int castlingFile;
      int kingFile;
      int castling = toCastling(character);
      if (castling == Castling::NOCASTLING) {
        castlingFile = toFile(character);
        if (castlingFile == File::NOFILE) {
          throw std::exception();
        }

        int color = colorOf(character);

        if (position.pieces[color][PieceType::KING].squares == 0) {
          throw std::exception();
        }

        kingFile = Square::getFile(Bitboard::next(position.pieces[color][PieceType::KING].squares));
        if (castlingFile > kingFile) {
          castling = Castling::valueOf(color, CastlingType::KINGSIDE);
        } else {
          castling = Castling::valueOf(color, CastlingType::QUEENSIDE);
        }
      } else if (Castling::getType(castling) == CastlingType::KINGSIDE) {
        castlingFile = File::h;
        kingFile = File::e;
      } else {
        castlingFile = File::a;
        kingFile = File::e;
      }

      assert(Castling::isValid(castling));
      assert(File::isValid(castlingFile));
      assert(File::isValid(kingFile));

      position.setCastlingRight(castling);
    }
  }

  // Parse en passant square
  token = tokens[tokensIndex++];

  if (token.compare("-") != 0) {
    if (token.length() != 2) {
      throw std::exception();
    }

    int enPassantFile = toFile(token[0]);
    int enPassantRank = toRank(token[1]);
    if (!(activeColor == Color::BLACK && enPassantRank == Rank::r3)
      && !(activeColor == Color::WHITE && enPassantRank == Rank::r6)) {
      throw std::exception();
    }

    position.setEnPassantSquare(Square::valueOf(enPassantFile, enPassantRank));
  }

  // Parse halfmove clock
  if (tokens.size() >= 5) {
    token = tokens[tokensIndex++];

    int number = std::stoi(token);
    if (number < 0) {
      throw std::exception();
    }

    position.setHalfmoveClock(number);
  }

  // Parse fullmove number
  if (tokens.size() == 6) {
    token = tokens[tokensIndex++];

    int number = std::stoi(token);
    if (number < 1) {
      throw std::exception();
    }

    position.setFullmoveNumber(number);
  }

  return position;
}

std::string Notation::fromPosition(const Position& position) {
  std::string fen;

  // Pieces
  for (auto iter = Rank::values.rbegin(); iter != Rank::values.rend(); ++iter) {
    int rank = *iter;
    unsigned int emptySquares = 0;

    for (auto file : File::values) {
      int piece = position.board[Square::valueOf(file, rank)];

      if (piece == Piece::NOPIECE) {
        ++emptySquares;
      } else {
        if (emptySquares > 0) {
          fen += std::to_string(emptySquares);
          emptySquares = 0;
        }
        fen += fromPiece(piece);
      }
    }

    if (emptySquares > 0) {
      fen += std::to_string(emptySquares);
    }

    if (rank > Rank::r1) {
      fen += '/';
    }
  }

  fen += ' ';

  // Color
  fen += fromColor(position.activeColor);

  fen += ' ';

  // Castling
  std::string castlingNotation;
  if ((position.castlingRights & Castling::WHITE_KINGSIDE) != Castling::NOCASTLING) {
    castlingNotation += fromCastling(Castling::WHITE_KINGSIDE);
  }
  if ((position.castlingRights & Castling::WHITE_QUEENSIDE) != Castling::NOCASTLING) {
    castlingNotation += fromCastling(Castling::WHITE_QUEENSIDE);
  }
  if ((position.castlingRights & Castling::BLACK_KINGSIDE) != Castling::NOCASTLING) {
    castlingNotation += fromCastling(Castling::BLACK_KINGSIDE);
  }
  if ((position.castlingRights & Castling::BLACK_QUEENSIDE) != Castling::NOCASTLING) {
    castlingNotation += fromCastling(Castling::BLACK_QUEENSIDE);
  }
  if (castlingNotation.empty()) {
    fen += '-';
  } else {
    fen += castlingNotation;
  }

  fen += ' ';

  // En passant
  if (position.enPassantSquare != Square::NOSQUARE) {
    fen += fromSquare(position.enPassantSquare);
  } else {
    fen += '-';
  }

  fen += ' ';

  // Halfmove clock
  fen += std::to_string(position.halfmoveClock);

  fen += ' ';

  // Fullmove number
  fen += std::to_string(position.getFullmoveNumber());

  return fen;
}

int Notation::toColor(char notation) {
  char lowercaseNotation = std::tolower(notation);
  switch (lowercaseNotation) {
    case WHITE_NOTATION:
      return Color::WHITE;
    case BLACK_NOTATION:
      return Color::BLACK;
    default:
      return Color::NOCOLOR;
  }
}

char Notation::fromColor(int color) {
  switch (color) {
    case Color::WHITE:
      return WHITE_NOTATION;
    case Color::BLACK:
      return BLACK_NOTATION;
    case Color::NOCOLOR:
    default:
      throw std::exception();
  }
}

int Notation::colorOf(char notation) {
  if (std::islower(notation)) {
    return Color::BLACK;
  } else {
    return Color::WHITE;
  }
}

char Notation::transform(char notation, int color) {
  switch (color) {
    case Color::WHITE:
      return std::toupper(notation);
    case Color::BLACK:
      return std::tolower(notation);
    default:
      throw std::exception();
  }
}

int Notation::toPieceType(char notation) {
  char uppercaseNotation = std::toupper(notation);
  switch (uppercaseNotation) {
    case PAWN_NOTATION:
      return PieceType::PAWN;
    case KNIGHT_NOTATION:
      return PieceType::KNIGHT;
    case BISHOP_NOTATION:
      return PieceType::BISHOP;
    case ROOK_NOTATION:
      return PieceType::ROOK;
    case QUEEN_NOTATION:
      return PieceType::QUEEN;
    case KING_NOTATION:
      return PieceType::KING;
    default:
      return PieceType::NOPIECETYPE;
  }
}

char Notation::fromPieceType(int piecetype) {
  switch (piecetype) {
    case PieceType::PAWN:
      return PAWN_NOTATION;
    case PieceType::KNIGHT:
      return KNIGHT_NOTATION;
    case PieceType::BISHOP:
      return BISHOP_NOTATION;
    case PieceType::ROOK:
      return ROOK_NOTATION;
    case PieceType::QUEEN:
      return QUEEN_NOTATION;
    case PieceType::KING:
      return KING_NOTATION;
    case PieceType::NOPIECETYPE:
    default:
      throw std::exception();
  }
}

int Notation::toPiece(char notation) {
  int color = colorOf(notation);
  int piecetype = toPieceType(notation);

  if (piecetype != PieceType::NOPIECETYPE) {
    return Piece::valueOf(color, piecetype);
  } else {
    return Piece::NOPIECE;
  }
}

char Notation::fromPiece(int piece) {
  assert(Piece::isValid(piece));

  return transform(fromPieceType(Piece::getType(piece)), Piece::getColor(piece));
}

int Notation::toCastlingType(char notation) {
  char uppercaseNotation = std::toupper(notation);
  switch (uppercaseNotation) {
    case KINGSIDE_NOTATION:
      return CastlingType::KINGSIDE;
    case QUEENSIDE_NOTATION:
      return CastlingType::QUEENSIDE;
    default:
      return CastlingType::NOCASTLINGTYPE;
  }
}

char Notation::fromCastlingType(int castlingtype) {
  switch (castlingtype) {
    case CastlingType::KINGSIDE:
      return KINGSIDE_NOTATION;
    case CastlingType::QUEENSIDE:
      return QUEENSIDE_NOTATION;
    case CastlingType::NOCASTLINGTYPE:
    default:
      throw std::exception();
  }
}

int Notation::toCastling(char notation) {
  int color = colorOf(notation);
  int castlingtype = toCastlingType(notation);

  if (castlingtype != CastlingType::NOCASTLINGTYPE) {
    return Castling::valueOf(color, castlingtype);
  } else {
    return Castling::NOCASTLING;
  }
}

char Notation::fromCastling(int castling) {
  assert(Castling::isValid(castling));

  return transform(fromCastlingType(Castling::getType(castling)), Castling::getColor(castling));
}

int Notation::toFile(char notation) {
  char lowercaseNotation = std::tolower(notation);
  switch (lowercaseNotation) {
    case a_NOTATION:
      return File::a;
    case b_NOTATION:
      return File::b;
    case c_NOTATION:
      return File::c;
    case d_NOTATION:
      return File::d;
    case e_NOTATION:
      return File::e;
    case f_NOTATION:
      return File::f;
    case g_NOTATION:
      return File::g;
    case h_NOTATION:
      return File::h;
    default:
      return File::NOFILE;
  }
}

char Notation::fromFile(int file) {
  switch (file) {
    case File::a:
      return a_NOTATION;
    case File::b:
      return b_NOTATION;
    case File::c:
      return c_NOTATION;
    case File::d:
      return d_NOTATION;
    case File::e:
      return e_NOTATION;
    case File::f:
      return f_NOTATION;
    case File::g:
      return g_NOTATION;
    case File::h:
      return h_NOTATION;
    case File::NOFILE:
    default:
      throw std::exception();
  }
}

int Notation::toRank(char notation) {
  switch (notation) {
    case r1_NOTATION:
      return Rank::r1;
    case r2_NOTATION:
      return Rank::r2;
    case r3_NOTATION:
      return Rank::r3;
    case r4_NOTATION:
      return Rank::r4;
    case r5_NOTATION:
      return Rank::r5;
    case r6_NOTATION:
      return Rank::r6;
    case r7_NOTATION:
      return Rank::r7;
    case r8_NOTATION:
      return Rank::r8;
    default:
      return Rank::NORANK;
  }
}

char Notation::fromRank(int rank) {
  switch (rank) {
    case Rank::r1:
      return r1_NOTATION;
    case Rank::r2:
      return r2_NOTATION;
    case Rank::r3:
      return r3_NOTATION;
    case Rank::r4:
      return r4_NOTATION;
    case Rank::r5:
      return r5_NOTATION;
    case Rank::r6:
      return r6_NOTATION;
    case Rank::r7:
      return r7_NOTATION;
    case Rank::r8:
      return r8_NOTATION;
    case Rank::NORANK:
    default:
      throw std::exception();
  }
}

int Notation::toSquare(const std::string& notation) {
  int file = toFile(notation[0]);
  int rank = toRank(notation[1]);

  if (file != File::NOFILE && rank != Rank::NORANK) {
    int square = (rank << 4) + file;
    assert(Square::isValid(square));

    return square;
  } else {
    return Square::NOSQUARE;
  }
}

std::string Notation::fromSquare(int square) {
  assert(Square::isValid(square));

  std::string notation;
  notation += fromFile(Square::getFile(square));
  notation += fromRank(Square::getRank(square));

  return notation;
}

}
