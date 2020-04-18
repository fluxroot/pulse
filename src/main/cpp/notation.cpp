/*
 * Copyright (C) 2013-2019 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "notation.h"
#include "file.h"
#include "rank.h"
#include "castlingtype.h"

#include <sstream>

namespace pulse::notation {
namespace {
constexpr char WHITE_NOTATION = 'w';
constexpr char BLACK_NOTATION = 'b';

constexpr char PAWN_NOTATION = 'P';
constexpr char KNIGHT_NOTATION = 'N';
constexpr char BISHOP_NOTATION = 'B';
constexpr char ROOK_NOTATION = 'R';
constexpr char QUEEN_NOTATION = 'Q';
constexpr char KING_NOTATION = 'K';

constexpr char KINGSIDE_NOTATION = 'K';
constexpr char QUEENSIDE_NOTATION = 'Q';

constexpr char a_NOTATION = 'a';
constexpr char b_NOTATION = 'b';
constexpr char c_NOTATION = 'c';
constexpr char d_NOTATION = 'd';
constexpr char e_NOTATION = 'e';
constexpr char f_NOTATION = 'f';
constexpr char g_NOTATION = 'g';
constexpr char h_NOTATION = 'h';

constexpr char r1_NOTATION = '1';
constexpr char r2_NOTATION = '2';
constexpr char r3_NOTATION = '3';
constexpr char r4_NOTATION = '4';
constexpr char r5_NOTATION = '5';
constexpr char r6_NOTATION = '6';
constexpr char r7_NOTATION = '7';
constexpr char r8_NOTATION = '8';

int colorOf(char notation) {
	if (std::islower(notation)) {
		return color::BLACK;
	} else {
		return color::WHITE;
	}
}

char transform(char notation, int color) {
	switch (color) {
		case color::WHITE:
			return std::toupper(notation);
		case color::BLACK:
			return std::tolower(notation);
		default:
			throw std::exception();
	}
}
}

Position toPosition(const std::string& fen) {
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
	int file = file::a;
	int rank = rank::r8;

	for (auto character : token) {
		int piece = toPiece(character);
		if (piece != piece::NOPIECE) {
			if (!file::isValid(file) || !rank::isValid(rank)) {
				throw std::invalid_argument("Illegal file or rank");
			}

			position.put(piece, square::valueOf(file, rank));

			if (file == file::h) {
				file = file::NOFILE;
			} else {
				file++;
			}
		} else if (character == '/') {
			if (file != file::NOFILE || rank == rank::r1) {
				throw std::invalid_argument("Illegal file or rank");
			}

			file = file::a;
			rank--;
		} else {
			std::string s = {character};
			int emptySquares = std::stoi(s);
			if (emptySquares < 1 || 8 < emptySquares) {
				throw std::invalid_argument("Illegal number of empty squares");
			}

			file += emptySquares - 1;
			if (!file::isValid(file)) {
				throw std::invalid_argument("Illegal number of empty squares");
			}

			if (file == file::h) {
				file = file::NOFILE;
			} else {
				file++;
			}
		}
	}

	// Parse active color
	token = tokens[tokensIndex++];

	if (token.length() != 1) {
		throw std::exception();
	}

	int activeColor = toColor(token[0]);
	if (activeColor == color::NOCOLOR) {
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
			if (castling == castling::NOCASTLING) {
				castlingFile = toFile(character);
				if (castlingFile == file::NOFILE) {
					throw std::exception();
				}

				int color = colorOf(character);

				if (position.pieces[color][piecetype::KING] == 0) {
					throw std::exception();
				}

				kingFile = square::getFile(bitboard::next(position.pieces[color][piecetype::KING]));
				if (castlingFile > kingFile) {
					castling = castling::valueOf(color, castlingtype::KINGSIDE);
				} else {
					castling = castling::valueOf(color, castlingtype::QUEENSIDE);
				}
			} else if (castling::getType(castling) == castlingtype::KINGSIDE) {
				castlingFile = file::h;
				kingFile = file::e;
			} else {
				castlingFile = file::a;
				kingFile = file::e;
			}

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
		if (!(activeColor == color::BLACK && enPassantRank == rank::r3)
			&& !(activeColor == color::WHITE && enPassantRank == rank::r6)) {
			throw std::exception();
		}

		position.setEnPassantSquare(square::valueOf(enPassantFile, enPassantRank));
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

std::string fromPosition(const Position& position) {
	std::string fen;

	// Pieces
	for (auto iter = rank::values.rbegin(); iter != rank::values.rend(); iter++) {
		int rank = *iter;
		unsigned int emptySquares = 0;

		for (auto file : file::values) {
			int piece = position.board[square::valueOf(file, rank)];

			if (piece == piece::NOPIECE) {
				emptySquares++;
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

		if (rank > rank::r1) {
			fen += '/';
		}
	}

	fen += ' ';

	// Color
	fen += fromColor(position.activeColor);

	fen += ' ';

	// Castling
	std::string castlingNotation;
	if ((position.castlingRights & castling::WHITE_KINGSIDE) != castling::NOCASTLING) {
		castlingNotation += fromCastling(castling::WHITE_KINGSIDE);
	}
	if ((position.castlingRights & castling::WHITE_QUEENSIDE) != castling::NOCASTLING) {
		castlingNotation += fromCastling(castling::WHITE_QUEENSIDE);
	}
	if ((position.castlingRights & castling::BLACK_KINGSIDE) != castling::NOCASTLING) {
		castlingNotation += fromCastling(castling::BLACK_KINGSIDE);
	}
	if ((position.castlingRights & castling::BLACK_QUEENSIDE) != castling::NOCASTLING) {
		castlingNotation += fromCastling(castling::BLACK_QUEENSIDE);
	}
	if (castlingNotation.empty()) {
		fen += '-';
	} else {
		fen += castlingNotation;
	}

	fen += ' ';

	// En passant
	if (position.enPassantSquare != square::NOSQUARE) {
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

int toColor(char notation) {
	char lowercaseNotation = std::tolower(notation);
	switch (lowercaseNotation) {
		case WHITE_NOTATION:
			return color::WHITE;
		case BLACK_NOTATION:
			return color::BLACK;
		default:
			return color::NOCOLOR;
	}
}

char fromColor(int color) {
	switch (color) {
		case color::WHITE:
			return WHITE_NOTATION;
		case color::BLACK:
			return BLACK_NOTATION;
		case color::NOCOLOR:
		default:
			throw std::exception();
	}
}

int toPieceType(char notation) {
	char uppercaseNotation = std::toupper(notation);
	switch (uppercaseNotation) {
		case PAWN_NOTATION:
			return piecetype::PAWN;
		case KNIGHT_NOTATION:
			return piecetype::KNIGHT;
		case BISHOP_NOTATION:
			return piecetype::BISHOP;
		case ROOK_NOTATION:
			return piecetype::ROOK;
		case QUEEN_NOTATION:
			return piecetype::QUEEN;
		case KING_NOTATION:
			return piecetype::KING;
		default:
			return piecetype::NOPIECETYPE;
	}
}

char fromPieceType(int piecetype) {
	switch (piecetype) {
		case piecetype::PAWN:
			return PAWN_NOTATION;
		case piecetype::KNIGHT:
			return KNIGHT_NOTATION;
		case piecetype::BISHOP:
			return BISHOP_NOTATION;
		case piecetype::ROOK:
			return ROOK_NOTATION;
		case piecetype::QUEEN:
			return QUEEN_NOTATION;
		case piecetype::KING:
			return KING_NOTATION;
		case piecetype::NOPIECETYPE:
		default:
			throw std::exception();
	}
}

int toPiece(char notation) {
	int color = colorOf(notation);
	int piecetype = toPieceType(notation);

	if (piecetype != piecetype::NOPIECETYPE) {
		return piece::valueOf(color, piecetype);
	} else {
		return piece::NOPIECE;
	}
}

char fromPiece(int piece) {
	return transform(fromPieceType(piece::getType(piece)), piece::getColor(piece));
}

int toCastlingType(char notation) {
	char uppercaseNotation = std::toupper(notation);
	switch (uppercaseNotation) {
		case KINGSIDE_NOTATION:
			return castlingtype::KINGSIDE;
		case QUEENSIDE_NOTATION:
			return castlingtype::QUEENSIDE;
		default:
			return castlingtype::NOCASTLINGTYPE;
	}
}

char fromCastlingType(int castlingtype) {
	switch (castlingtype) {
		case castlingtype::KINGSIDE:
			return KINGSIDE_NOTATION;
		case castlingtype::QUEENSIDE:
			return QUEENSIDE_NOTATION;
		case castlingtype::NOCASTLINGTYPE:
		default:
			throw std::exception();
	}
}

int toCastling(char notation) {
	int color = colorOf(notation);
	int castlingtype = toCastlingType(notation);

	if (castlingtype != castlingtype::NOCASTLINGTYPE) {
		return castling::valueOf(color, castlingtype);
	} else {
		return castling::NOCASTLING;
	}
}

char fromCastling(int castling) {
	return transform(fromCastlingType(castling::getType(castling)), castling::getColor(castling));
}

int toFile(char notation) {
	char lowercaseNotation = std::tolower(notation);
	switch (lowercaseNotation) {
		case a_NOTATION:
			return file::a;
		case b_NOTATION:
			return file::b;
		case c_NOTATION:
			return file::c;
		case d_NOTATION:
			return file::d;
		case e_NOTATION:
			return file::e;
		case f_NOTATION:
			return file::f;
		case g_NOTATION:
			return file::g;
		case h_NOTATION:
			return file::h;
		default:
			return file::NOFILE;
	}
}

char fromFile(int file) {
	switch (file) {
		case file::a:
			return a_NOTATION;
		case file::b:
			return b_NOTATION;
		case file::c:
			return c_NOTATION;
		case file::d:
			return d_NOTATION;
		case file::e:
			return e_NOTATION;
		case file::f:
			return f_NOTATION;
		case file::g:
			return g_NOTATION;
		case file::h:
			return h_NOTATION;
		case file::NOFILE:
		default:
			throw std::exception();
	}
}

int toRank(char notation) {
	switch (notation) {
		case r1_NOTATION:
			return rank::r1;
		case r2_NOTATION:
			return rank::r2;
		case r3_NOTATION:
			return rank::r3;
		case r4_NOTATION:
			return rank::r4;
		case r5_NOTATION:
			return rank::r5;
		case r6_NOTATION:
			return rank::r6;
		case r7_NOTATION:
			return rank::r7;
		case r8_NOTATION:
			return rank::r8;
		default:
			return rank::NORANK;
	}
}

char fromRank(int rank) {
	switch (rank) {
		case rank::r1:
			return r1_NOTATION;
		case rank::r2:
			return r2_NOTATION;
		case rank::r3:
			return r3_NOTATION;
		case rank::r4:
			return r4_NOTATION;
		case rank::r5:
			return r5_NOTATION;
		case rank::r6:
			return r6_NOTATION;
		case rank::r7:
			return r7_NOTATION;
		case rank::r8:
			return r8_NOTATION;
		case rank::NORANK:
		default:
			throw std::exception();
	}
}

int toSquare(const std::string& notation) {
	int file = toFile(notation[0]);
	int rank = toRank(notation[1]);

	if (file != file::NOFILE && rank != rank::NORANK) {
		return (rank << 4) + file;
	} else {
		return square::NOSQUARE;
	}
}

std::string fromSquare(int square) {
	std::string notation;
	notation += fromFile(square::getFile(square));
	notation += fromRank(square::getRank(square));

	return notation;
}
}
