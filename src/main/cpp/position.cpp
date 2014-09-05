/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

#include "position.h"
#include "castlingtype.h"
#include "file.h"
#include "rank.h"
#include "piecetype.h"
#include "move.h"

#include <cassert>
#include <algorithm>
#include <sstream>

namespace pulse {

// Initialize the zobrist keys
Position::Zobrist::Zobrist() {
  for (auto piece : Piece::values) {
    for (int i = 0; i < Square::LENGTH; ++i) {
      board[piece][i] = next();
    }
  }

  for (auto castling : Castling::values) {
    castlingRights[castling] = next();
  }

  for (int i = 0; i < Square::LENGTH; ++i) {
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
  for (int i = 0; i < 16; ++i) {
    bytes[i] = generator();
  }

  uint64_t hash = 0;
  for (int i = 0; i < 16; ++i) {
    hash ^= bytes[i] << ((i * 8) % 64);
  }

  return hash;
}

Position::State::State() {
  castlingRights.fill(+File::NOFILE);
}

Position::Position()
    : zobrist(Zobrist::instance()) {
  board.fill(+Piece::NOPIECE);
  castlingRights.fill(+File::NOFILE);
}

Position::Position(const Position& position)
    : Position() {
  this->board = position.board;
  this->pawns = position.pawns;
  this->knights = position.knights;
  this->bishops = position.bishops;
  this->rooks = position.rooks;
  this->queens = position.queens;
  this->kings = position.kings;

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
  this->pawns = position.pawns;
  this->knights = position.knights;
  this->bishops = position.bishops;
  this->rooks = position.rooks;
  this->queens = position.queens;
  this->kings = position.kings;

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
    && this->pawns == position.pawns
    && this->knights == position.knights
    && this->bishops == position.bishops
    && this->rooks == position.rooks
    && this->queens == position.queens
    && this->kings == position.kings

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

void Position::setActiveColor(int activeColor) {
  assert(Color::isValid(activeColor));

  if (this->activeColor != activeColor) {
    this->activeColor = activeColor;
    zobristKey ^= zobrist.activeColor;
  }
}

void Position::setCastlingRight(int castling, int file) {
  assert(Castling::isValid(castling));

  if (castlingRights[castling] != File::NOFILE) {
    zobristKey ^= zobrist.castlingRights[castling];
  }
  if (file != File::NOFILE) {
    zobristKey ^= zobrist.castlingRights[castling];
  }
  castlingRights[castling] = file;
}

void Position::setEnPassantSquare(int enPassantSquare) {
  if (this->enPassantSquare != Square::NOSQUARE) {
    zobristKey ^= zobrist.enPassantSquare[this->enPassantSquare];
  }
  if (enPassantSquare != Square::NOSQUARE) {
    zobristKey ^= zobrist.enPassantSquare[enPassantSquare];
  }
  this->enPassantSquare = enPassantSquare;
}

void Position::setHalfmoveClock(int halfmoveClock) {
  assert(halfmoveClock >= 0);

  this->halfmoveClock = halfmoveClock;
}

int Position::getFullmoveNumber() const {
  return halfmoveNumber / 2;
}

void Position::setFullmoveNumber(int fullmoveNumber) {
  assert(fullmoveNumber > 0);

  halfmoveNumber = fullmoveNumber * 2;
  if (activeColor == Color::BLACK) {
    ++halfmoveNumber;
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
  return pawns[Color::WHITE].size() == 0 && pawns[Color::BLACK].size() == 0
      && rooks[Color::WHITE].size() == 0 && rooks[Color::BLACK].size() == 0
      && queens[Color::WHITE].size() == 0 && queens[Color::BLACK].size() == 0
      && (knights[Color::WHITE].size() + bishops[Color::WHITE].size() <= 1)
      && (knights[Color::BLACK].size() + bishops[Color::BLACK].size() <= 1);
}

/**
 * Puts a piece at the square. We need to update our board and the appropriate
 * piece type list.
 *
 * @param piece  the Piece.
 * @param square the Square.
 */
void Position::put(int piece, int square) {
  assert(Piece::isValid(piece));
  assert(Square::isValid(square));
  assert(board[square] == Piece::NOPIECE);

  int pieceType = Piece::getType(piece);
  int color = Piece::getColor(piece);

  switch (pieceType) {
    case PieceType::PAWN:
      pawns[color].add(square);
      material[color] += PieceType::PAWN_VALUE;
      break;
    case PieceType::KNIGHT:
      knights[color].add(square);
      material[color] += PieceType::KNIGHT_VALUE;
      break;
    case PieceType::BISHOP:
      bishops[color].add(square);
      material[color] += PieceType::BISHOP_VALUE;
      break;
    case PieceType::ROOK:
      rooks[color].add(square);
      material[color] += PieceType::ROOK_VALUE;
      break;
    case PieceType::QUEEN:
      queens[color].add(square);
      material[color] += PieceType::QUEEN_VALUE;
      break;
    case PieceType::KING:
      kings[color].add(square);
      material[color] += PieceType::KING_VALUE;
      break;
    default:
      throw std::exception();
  }

  board[square] = piece;

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
  assert(Square::isValid(square));
  assert(Piece::isValid(board[square]));

  int piece = board[square];

  int pieceType = Piece::getType(piece);
  int color = Piece::getColor(piece);

  switch (pieceType) {
    case PieceType::PAWN:
      pawns[color].remove(square);
      material[color] -= PieceType::PAWN_VALUE;
      break;
    case PieceType::KNIGHT:
      knights[color].remove(square);
      material[color] -= PieceType::KNIGHT_VALUE;
      break;
    case PieceType::BISHOP:
      bishops[color].remove(square);
      material[color] -= PieceType::BISHOP_VALUE;
      break;
    case PieceType::ROOK:
      rooks[color].remove(square);
      material[color] -= PieceType::ROOK_VALUE;
      break;
    case PieceType::QUEEN:
      queens[color].remove(square);
      material[color] -= PieceType::QUEEN_VALUE;
      break;
    case PieceType::KING:
      kings[color].remove(square);
      material[color] -= PieceType::KING_VALUE;
      break;
    default:
      throw std::exception();
  }

  board[square] = Piece::NOPIECE;

  zobristKey ^= zobrist.board[piece][square];

  return piece;
}

void Position::makeMove(int move) {
  // Save state
  State& entry = states[statesSize];
  entry.zobristKey = zobristKey;
  for (auto castling : Castling::values) {
    entry.castlingRights[castling] = castlingRights[castling];
  }
  entry.enPassantSquare = enPassantSquare;
  entry.halfmoveClock = halfmoveClock;

  ++statesSize;
  assert(statesSize < MAX_MOVES);

  // Get variables
  int type = Move::getType(move);
  int originSquare = Move::getOriginSquare(move);
  int targetSquare = Move::getTargetSquare(move);
  int originPiece = Move::getOriginPiece(move);
  int originColor = Piece::getColor(originPiece);
  int targetPiece = Move::getTargetPiece(move);

  // Remove target piece and update castling rights
  if (targetPiece != Piece::NOPIECE) {
    int captureSquare = targetSquare;
    if (type == MoveType::ENPASSANT) {
      captureSquare += (originColor == Color::WHITE ? Square::S : Square::N);
    }
    assert(targetPiece == board[captureSquare]);
    assert(Piece::getType(targetPiece) != PieceType::KING);
    remove(captureSquare);

    clearCastling(captureSquare);
  }

  // Move piece
  assert(originPiece == board[originSquare]);
  remove(originSquare);
  if (type == MoveType::PAWNPROMOTION) {
    put(Piece::valueOf(originColor, Move::getPromotion(move)), targetSquare);
  } else {
    put(originPiece, targetSquare);
  }

  // Move rook and update castling rights
  if (type == MoveType::CASTLING) {
    int rookOriginSquare;
    int rookTargetSquare;
    switch (targetSquare) {
      case Square::g1:
        rookOriginSquare = Square::h1;
        rookTargetSquare = Square::f1;
        break;
      case Square::c1:
        rookOriginSquare = Square::a1;
        rookTargetSquare = Square::d1;
        break;
      case Square::g8:
        rookOriginSquare = Square::h8;
        rookTargetSquare = Square::f8;
        break;
      case Square::c8:
        rookOriginSquare = Square::a8;
        rookTargetSquare = Square::d8;
        break;
      default:
        throw std::exception();
    }

    assert(Piece::getType(board[rookOriginSquare]) == PieceType::ROOK);
    int rookPiece = remove(rookOriginSquare);
    put(rookPiece, rookTargetSquare);
  }

  // Update castling
  clearCastling(originSquare);

  // Update enPassantSquare
  if (enPassantSquare != Square::NOSQUARE) {
    zobristKey ^= zobrist.enPassantSquare[enPassantSquare];
  }
  if (type == MoveType::PAWNDOUBLE) {
    enPassantSquare = targetSquare + (originColor == Color::WHITE ? Square::S : Square::N);
    assert(Square::isValid(enPassantSquare));
    zobristKey ^= zobrist.enPassantSquare[enPassantSquare];
  } else {
    enPassantSquare = Square::NOSQUARE;
  }

  // Update activeColor
  activeColor = Color::opposite(activeColor);
  zobristKey ^= zobrist.activeColor;

  // Update halfmoveClock
  if (Piece::getType(originPiece) == PieceType::PAWN || targetPiece != Piece::NOPIECE) {
    halfmoveClock = 0;
  } else {
    ++halfmoveClock;
  }

  // Update fullMoveNumber
  ++halfmoveNumber;
}

void Position::undoMove(int move) {
  // Get variables
  int type = Move::getType(move);
  int originSquare = Move::getOriginSquare(move);
  int targetSquare = Move::getTargetSquare(move);
  int originPiece = Move::getOriginPiece(move);
  int originColor = Piece::getColor(originPiece);
  int targetPiece = Move::getTargetPiece(move);

  // Update fullMoveNumber
  --halfmoveNumber;

  // Update activeColor
  activeColor = Color::opposite(activeColor);

  // Undo move rook
  if (type == MoveType::CASTLING) {
    int rookOriginSquare;
    int rookTargetSquare;
    switch (targetSquare) {
      case Square::g1:
        rookOriginSquare = Square::h1;
        rookTargetSquare = Square::f1;
        break;
      case Square::c1:
        rookOriginSquare = Square::a1;
        rookTargetSquare = Square::d1;
        break;
      case Square::g8:
        rookOriginSquare = Square::h8;
        rookTargetSquare = Square::f8;
        break;
      case Square::c8:
        rookOriginSquare = Square::a8;
        rookTargetSquare = Square::d8;
        break;
      default:
        throw std::exception();
    }

    assert(Piece::getType(board[rookTargetSquare]) == PieceType::ROOK);
    int rookPiece = remove(rookTargetSquare);
    put(rookPiece, rookOriginSquare);
  }

  // Undo move piece
  remove(targetSquare);
  put(originPiece, originSquare);

  // Restore target piece
  if (targetPiece != Piece::NOPIECE) {
    int captureSquare = targetSquare;
    if (type == MoveType::ENPASSANT) {
      captureSquare += (originColor == Color::WHITE ? Square::S : Square::N);
      assert(Square::isValid(captureSquare));
    }
    put(targetPiece, captureSquare);
  }

  // Restore state
  assert(statesSize > 0);
  --statesSize;

  State& entry = states[statesSize];
  halfmoveClock = entry.halfmoveClock;
  enPassantSquare = entry.enPassantSquare;
  for (auto castling : Castling::values) {
    if (entry.castlingRights[castling] != castlingRights[castling]) {
      castlingRights[castling] = entry.castlingRights[castling];
    }
  }
  zobristKey = entry.zobristKey;
}

void Position::clearCastlingRights(int castling) {
  assert(Castling::isValid(castling));

  if (castlingRights[castling] != File::NOFILE) {
    castlingRights[castling] = File::NOFILE;
    zobristKey ^= zobrist.castlingRights[castling];
  }
}

void Position::clearCastling(int square) {
  assert(Square::isValid(square));

  switch (square) {
    case Square::a1:
      clearCastlingRights(Castling::WHITE_QUEENSIDE);
      break;
    case Square::h1:
      clearCastlingRights(Castling::WHITE_KINGSIDE);
      break;
    case Square::a8:
      clearCastlingRights(Castling::BLACK_QUEENSIDE);
      break;
    case Square::h8:
      clearCastlingRights(Castling::BLACK_KINGSIDE);
      break;
    case Square::e1:
      clearCastlingRights(Castling::WHITE_QUEENSIDE);
      clearCastlingRights(Castling::WHITE_KINGSIDE);
      break;
    case Square::e8:
      clearCastlingRights(Castling::BLACK_QUEENSIDE);
      clearCastlingRights(Castling::BLACK_KINGSIDE);
      break;
    default:
      break;
  }
}

bool Position::isCheck() {
  // Check whether our king is attacked by any opponent piece
  return isAttacked(Bitboard::next(kings[activeColor].squares), Color::opposite(activeColor));
}

bool Position::isCheck(int color) {
  // Check whether the king for color is attacked by any opponent piece
  return isAttacked(Bitboard::next(kings[color].squares), Color::opposite(color));
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
  assert(Square::isValid(targetSquare));
  assert(Color::isValid(attackerColor));

  // Pawn attacks
  int pawnPiece = Piece::valueOf(attackerColor, PieceType::PAWN);
  for (unsigned int i = 1; i < Square::pawnDirections[attackerColor].size(); ++i) {
    int attackerSquare = targetSquare - Square::pawnDirections[attackerColor][i];
    if (Square::isValid(attackerSquare)) {
      int attackerPawn = board[attackerSquare];

      if (attackerPawn == pawnPiece) {
        return true;
      }
    }
  }

  return isAttacked(targetSquare,
    Piece::valueOf(attackerColor, PieceType::KNIGHT),
    Square::knightDirections)

      // The queen moves like a bishop, so check both piece types
      || isAttacked(targetSquare,
      Piece::valueOf(attackerColor, PieceType::BISHOP),
      Piece::valueOf(attackerColor, PieceType::QUEEN),
      Square::bishopDirections)

      // The queen moves like a rook, so check both piece types
      || isAttacked(targetSquare,
      Piece::valueOf(attackerColor, PieceType::ROOK),
      Piece::valueOf(attackerColor, PieceType::QUEEN),
      Square::rookDirections)

      || isAttacked(targetSquare,
      Piece::valueOf(attackerColor, PieceType::KING),
      Square::kingDirections);
}

/**
 * Returns whether the targetSquare is attacked by a non-sliding piece.
 */
bool Position::isAttacked(int targetSquare, int attackerPiece, const std::vector<int>& moveDelta) {
  assert(Square::isValid(targetSquare));
  assert(Piece::isValid(attackerPiece));

  for (auto delta : moveDelta) {
    int attackerSquare = targetSquare + delta;

    if (Square::isValid(attackerSquare) && board[attackerSquare] == attackerPiece) {
      return true;
    }
  }

  return false;
}

/**
 * Returns whether the targetSquare is attacked by a sliding piece.
 */
bool Position::isAttacked(int targetSquare, int attackerPiece, int queenPiece, const std::vector<int>& moveDelta) {
  assert(Square::isValid(targetSquare));
  assert(Piece::isValid(attackerPiece));
  assert(Piece::isValid(queenPiece));

  for (auto delta : moveDelta) {
    int attackerSquare = targetSquare + delta;

    while (Square::isValid(attackerSquare)) {
      int piece = board[attackerSquare];

      if (Piece::isValid(piece)) {
        if (piece == attackerPiece || piece == queenPiece) {
          return true;
        }

        break;
      } else {
        attackerSquare += delta;
      }
    }
  }

  return false;
}

}
