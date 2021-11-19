// Copyright 2013-2021 Phokham Nonava
//
// Use of this source code is governed by the MIT license that can be
// found in the LICENSE file.

#include "position.h"
#include "model/move.h"

namespace pulse {

// Initialize the zobrist keys
Position::Zobrist::Zobrist() {
	for (auto piece: piece::values) {
		for (int i = 0; i < square::VALUES_LENGTH; i++) {
			board[piece][i] = next();
		}
	}

	castlingRights[castling::WHITE_KINGSIDE] = next();
	castlingRights[castling::WHITE_QUEENSIDE] = next();
	castlingRights[castling::BLACK_KINGSIDE] = next();
	castlingRights[castling::BLACK_QUEENSIDE] = next();
	castlingRights[castling::WHITE_KINGSIDE | castling::WHITE_QUEENSIDE] =
			castlingRights[castling::WHITE_KINGSIDE] ^ castlingRights[castling::WHITE_QUEENSIDE];
	castlingRights[castling::BLACK_KINGSIDE | castling::BLACK_QUEENSIDE] =
			castlingRights[castling::BLACK_KINGSIDE] ^ castlingRights[castling::BLACK_QUEENSIDE];

	for (int i = 0; i < square::VALUES_LENGTH; i++) {
		enPassantSquare[i] = next();
	}

	activeColor = next();
}

Position::Zobrist& Position::Zobrist::instance() {
	static Zobrist* instance = new Zobrist();
	return *instance;
}

uint64_t Position::Zobrist::next() {
	std::array<uint64_t, 16> bytes;
	for (int i = 0; i < 16; i++) {
		bytes[i] = generator();
	}

	uint64_t hash = 0;
	for (int i = 0; i < 16; i++) {
		hash ^= bytes[i] << ((i * 8) % 64);
	}

	return hash;
}

Position::Position()
		: zobrist(Zobrist::instance()) {
	board.fill(+piece::NOPIECE);
}

Position::Position(const Position& position)
		: Position() {
	this->board = position.board;
	this->pieces = position.pieces;

	this->material = position.material;

	this->castlingRights = position.castlingRights;
	this->enPassantSquare = position.enPassantSquare;
	this->activeColor = position.activeColor;
	this->halfmoveClock = position.halfmoveClock;

	this->zobristKey = position.zobristKey;

	this->halfmoveNumber = position.halfmoveNumber;

	this->statesSize = 0;
}

Position& Position::operator=(const Position& position) {
	this->board = position.board;
	this->pieces = position.pieces;

	this->material = position.material;

	this->castlingRights = position.castlingRights;
	this->enPassantSquare = position.enPassantSquare;
	this->activeColor = position.activeColor;
	this->halfmoveClock = position.halfmoveClock;

	this->zobristKey = position.zobristKey;

	this->halfmoveNumber = position.halfmoveNumber;

	this->statesSize = 0;

	return *this;
}

bool Position::operator==(const Position& position) const {
	return this->board == position.board
		   && this->pieces == position.pieces

		   && this->material == position.material

		   && this->castlingRights == position.castlingRights
		   && this->enPassantSquare == position.enPassantSquare
		   && this->activeColor == position.activeColor
		   && this->halfmoveClock == position.halfmoveClock

		   && this->zobristKey == position.zobristKey

		   && this->halfmoveNumber == position.halfmoveNumber;
}

bool Position::operator!=(const Position& position) const {
	return !(*this == position);
}

void Position::setActiveColor(int _activeColor) {
	if (activeColor != _activeColor) {
		activeColor = _activeColor;
		zobristKey ^= zobrist.activeColor;
	}
}

void Position::setCastlingRight(int castling) {
	if ((castlingRights & castling) == castling::NOCASTLING) {
		castlingRights |= castling;
		zobristKey ^= zobrist.castlingRights[castling];
	}
}

void Position::setEnPassantSquare(int _enPassantSquare) {
	if (this->enPassantSquare != square::NOSQUARE) {
		zobristKey ^= zobrist.enPassantSquare[this->enPassantSquare];
	}
	if (_enPassantSquare != square::NOSQUARE) {
		zobristKey ^= zobrist.enPassantSquare[_enPassantSquare];
	}
	enPassantSquare = _enPassantSquare;
}

void Position::setHalfmoveClock(int _halfmoveClock) {
	halfmoveClock = _halfmoveClock;
}

int Position::getFullmoveNumber() const {
	return halfmoveNumber / 2;
}

void Position::setFullmoveNumber(int fullmoveNumber) {
	halfmoveNumber = fullmoveNumber * 2;
	if (activeColor == color::BLACK) {
		halfmoveNumber++;
	}
}

bool Position::isRepetition() {
	// Search back until the last halfmoveClock reset
	int j = std::max(0, statesSize - halfmoveClock);
	for (int i = statesSize - 2; i >= j; i -= 2) {
		if (zobristKey == states[i].zobristKey) {
			return true;
		}
	}

	return false;
}

bool Position::hasInsufficientMaterial() {
	// If there is only one minor left, we are unable to checkmate
	return bitboard::size(pieces[color::WHITE][piecetype::PAWN]) == 0
		   && bitboard::size(pieces[color::BLACK][piecetype::PAWN]) == 0
		   && bitboard::size(pieces[color::WHITE][piecetype::ROOK]) == 0
		   && bitboard::size(pieces[color::BLACK][piecetype::ROOK]) == 0
		   && bitboard::size(pieces[color::WHITE][piecetype::QUEEN]) == 0
		   && bitboard::size(pieces[color::BLACK][piecetype::QUEEN]) == 0
		   && (bitboard::size(pieces[color::WHITE][piecetype::KNIGHT]) +
			   bitboard::size(pieces[color::WHITE][piecetype::BISHOP]) <= 1)
		   && (bitboard::size(pieces[color::BLACK][piecetype::KNIGHT]) +
			   bitboard::size(pieces[color::BLACK][piecetype::BISHOP]) <= 1);
}

/**
 * Puts a piece at the square. We need to update our board and the appropriate
 * piece type list.
 *
 * @param piece  the Piece.
 * @param square the Square.
 */
void Position::put(int piece, int square) {
	int piecetype = piece::getType(piece);
	int color = piece::getColor(piece);

	board[square] = piece;
	pieces[color][piecetype] = bitboard::add(square, pieces[color][piecetype]);
	material[color] += piecetype::getValue(piecetype);

	zobristKey ^= zobrist.board[piece][square];
}

/**
 * Removes a piece from the square. We need to update our board and the
 * appropriate piece type list.
 *
 * @param square the Square.
 * @return the Piece which was removed.
 */
int Position::remove(int square) {
	int piece = board[square];

	int piecetype = piece::getType(piece);
	int color = piece::getColor(piece);

	board[square] = piece::NOPIECE;
	pieces[color][piecetype] = bitboard::remove(square, pieces[color][piecetype]);
	material[color] -= piecetype::getValue(piecetype);

	zobristKey ^= zobrist.board[piece][square];

	return piece;
}

void Position::makeMove(int move) {
	// Save state
	State& entry = states[statesSize];
	entry.zobristKey = zobristKey;
	entry.castlingRights = castlingRights;
	entry.enPassantSquare = enPassantSquare;
	entry.halfmoveClock = halfmoveClock;

	statesSize++;

	// Get variables
	int type = move::getType(move);
	int originSquare = move::getOriginSquare(move);
	int targetSquare = move::getTargetSquare(move);
	int originPiece = move::getOriginPiece(move);
	int originColor = piece::getColor(originPiece);
	int targetPiece = move::getTargetPiece(move);

	// Remove target piece and update castling rights
	if (targetPiece != piece::NOPIECE) {
		int captureSquare = targetSquare;
		if (type == movetype::ENPASSANT) {
			captureSquare += (originColor == color::WHITE ? square::S : square::N);
		}
		remove(captureSquare);

		clearCastling(captureSquare);
	}

	// Move piece
	remove(originSquare);
	if (type == movetype::PAWNPROMOTION) {
		put(piece::valueOf(originColor, move::getPromotion(move)), targetSquare);
	} else {
		put(originPiece, targetSquare);
	}

	// Move rook and update castling rights
	if (type == movetype::CASTLING) {
		int rookOriginSquare;
		int rookTargetSquare;
		switch (targetSquare) {
			case square::g1:
				rookOriginSquare = square::h1;
				rookTargetSquare = square::f1;
				break;
			case square::c1:
				rookOriginSquare = square::a1;
				rookTargetSquare = square::d1;
				break;
			case square::g8:
				rookOriginSquare = square::h8;
				rookTargetSquare = square::f8;
				break;
			case square::c8:
				rookOriginSquare = square::a8;
				rookTargetSquare = square::d8;
				break;
			default:
				throw std::exception();
		}

		int rookPiece = remove(rookOriginSquare);
		put(rookPiece, rookTargetSquare);
	}

	// Update castling
	clearCastling(originSquare);

	// Update enPassantSquare
	if (enPassantSquare != square::NOSQUARE) {
		zobristKey ^= zobrist.enPassantSquare[enPassantSquare];
	}
	if (type == movetype::PAWNDOUBLE) {
		enPassantSquare = targetSquare + (originColor == color::WHITE ? square::S : square::N);
		zobristKey ^= zobrist.enPassantSquare[enPassantSquare];
	} else {
		enPassantSquare = square::NOSQUARE;
	}

	// Update activeColor
	activeColor = color::opposite(activeColor);
	zobristKey ^= zobrist.activeColor;

	// Update halfmoveClock
	if (piece::getType(originPiece) == piecetype::PAWN || targetPiece != piece::NOPIECE) {
		halfmoveClock = 0;
	} else {
		halfmoveClock++;
	}

	// Update fullMoveNumber
	halfmoveNumber++;
}

void Position::undoMove(int move) {
	// Get variables
	int type = move::getType(move);
	int originSquare = move::getOriginSquare(move);
	int targetSquare = move::getTargetSquare(move);
	int originPiece = move::getOriginPiece(move);
	int originColor = piece::getColor(originPiece);
	int targetPiece = move::getTargetPiece(move);

	// Update fullMoveNumber
	halfmoveNumber--;

	// Update activeColor
	activeColor = color::opposite(activeColor);

	// Undo move rook
	if (type == movetype::CASTLING) {
		int rookOriginSquare;
		int rookTargetSquare;
		switch (targetSquare) {
			case square::g1:
				rookOriginSquare = square::h1;
				rookTargetSquare = square::f1;
				break;
			case square::c1:
				rookOriginSquare = square::a1;
				rookTargetSquare = square::d1;
				break;
			case square::g8:
				rookOriginSquare = square::h8;
				rookTargetSquare = square::f8;
				break;
			case square::c8:
				rookOriginSquare = square::a8;
				rookTargetSquare = square::d8;
				break;
			default:
				throw std::exception();
		}

		int rookPiece = remove(rookTargetSquare);
		put(rookPiece, rookOriginSquare);
	}

	// Undo move piece
	remove(targetSquare);
	put(originPiece, originSquare);

	// Restore target piece
	if (targetPiece != piece::NOPIECE) {
		int captureSquare = targetSquare;
		if (type == movetype::ENPASSANT) {
			captureSquare += (originColor == color::WHITE ? square::S : square::N);
		}
		put(targetPiece, captureSquare);
	}

	// Restore state
	statesSize--;

	State& entry = states[statesSize];
	halfmoveClock = entry.halfmoveClock;
	enPassantSquare = entry.enPassantSquare;
	castlingRights = entry.castlingRights;
	zobristKey = entry.zobristKey;
}

void Position::clearCastling(int square) {
	int newCastlingRights = castlingRights;

	switch (square) {
		case square::a1:
			newCastlingRights &= ~castling::WHITE_QUEENSIDE;
			break;
		case square::a8:
			newCastlingRights &= ~castling::BLACK_QUEENSIDE;
			break;
		case square::h1:
			newCastlingRights &= ~castling::WHITE_KINGSIDE;
			break;
		case square::h8:
			newCastlingRights &= ~castling::BLACK_KINGSIDE;
			break;
		case square::e1:
			newCastlingRights &= ~(castling::WHITE_KINGSIDE | castling::WHITE_QUEENSIDE);
			break;
		case square::e8:
			newCastlingRights &= ~(castling::BLACK_KINGSIDE | castling::BLACK_QUEENSIDE);
			break;
		default:
			return;
	}

	if (newCastlingRights != castlingRights) {
		castlingRights = newCastlingRights;
		zobristKey ^= zobrist.castlingRights[newCastlingRights ^ castlingRights];
	}
}

bool Position::isCheck() {
	// Check whether our king is attacked by any opponent piece
	return isAttacked(bitboard::next(pieces[activeColor][piecetype::KING]), color::opposite(activeColor));
}

bool Position::isCheck(int color) {
	// Check whether the king for color is attacked by any opponent piece
	return isAttacked(bitboard::next(pieces[color][piecetype::KING]), color::opposite(color));
}

/**
 * Returns whether the targetSquare is attacked by any piece from the
 * attackerColor. We will backtrack from the targetSquare to find the piece.
 *
 * @param targetSquare  the target Square.
 * @param attackerColor the attacker Color.
 * @return whether the targetSquare is attacked.
 */
bool Position::isAttacked(int targetSquare, int attackerColor) {
	// Pawn attacks
	int pawnPiece = piece::valueOf(attackerColor, piecetype::PAWN);
	for (unsigned int i = 1; i < square::pawnDirections[attackerColor].size(); i++) {
		int attackerSquare = targetSquare - square::pawnDirections[attackerColor][i];
		if (square::isValid(attackerSquare)) {
			int attackerPawn = board[attackerSquare];

			if (attackerPawn == pawnPiece) {
				return true;
			}
		}
	}

	return isAttacked(targetSquare,
			piece::valueOf(attackerColor, piecetype::KNIGHT),
			square::knightDirections)

		   // The queen moves like a bishop, so check both piece types
		   || isAttacked(targetSquare,
			piece::valueOf(attackerColor, piecetype::BISHOP),
			piece::valueOf(attackerColor, piecetype::QUEEN),
			square::bishopDirections)

		   // The queen moves like a rook, so check both piece types
		   || isAttacked(targetSquare,
			piece::valueOf(attackerColor, piecetype::ROOK),
			piece::valueOf(attackerColor, piecetype::QUEEN),
			square::rookDirections)

		   || isAttacked(targetSquare,
			piece::valueOf(attackerColor, piecetype::KING),
			square::kingDirections);
}

/**
 * Returns whether the targetSquare is attacked by a non-sliding piece.
 */
bool Position::isAttacked(int targetSquare, int attackerPiece, const std::vector<int>& directions) {
	for (auto direction: directions) {
		int attackerSquare = targetSquare + direction;

		if (square::isValid(attackerSquare) && board[attackerSquare] == attackerPiece) {
			return true;
		}
	}

	return false;
}

/**
 * Returns whether the targetSquare is attacked by a sliding piece.
 */
bool Position::isAttacked(int targetSquare, int attackerPiece, int queenPiece, const std::vector<int>& directions) {
	for (auto direction: directions) {
		int attackerSquare = targetSquare + direction;

		while (square::isValid(attackerSquare)) {
			int piece = board[attackerSquare];

			if (piece::isValid(piece)) {
				if (piece == attackerPiece || piece == queenPiece) {
					return true;
				}

				break;
			} else {
				attackerSquare += direction;
			}
		}
	}

	return false;
}
}
