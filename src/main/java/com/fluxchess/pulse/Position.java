/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import java.security.SecureRandom;

final public class Position {

  private static final int MAX_MOVES = Depth.MAX_PLY + 1024;

  final int[] board = new int[Square.VALUES_LENGTH];

  final Bitboard[][] pieces = new Bitboard[Color.values.length][PieceType.values.length];

  final int[] material = new int[Color.values.length];

  int castlingRights = Castling.NOCASTLING;
  int enPassantSquare = Square.NOSQUARE;
  int activeColor = Color.WHITE;
  int halfmoveClock = 0;
  private int halfmoveNumber = 2;

  long zobristKey = 0;

  // We will save some position parameters in a State before making a move.
  // Later we will restore them before undoing a move.
  private final State[] states = new State[MAX_MOVES];
  private int statesSize = 0;

  private static final class Zobrist {
    private static final SecureRandom random = new SecureRandom();

    static final long[][] board = new long[Piece.values.length][Square.VALUES_LENGTH];
    static final long[] castlingRights = new long[Castling.VALUES_LENGTH];
    static final long[] enPassantSquare = new long[Square.VALUES_LENGTH];
    static final long activeColor = next();

    // Initialize the zobrist keys
    static {
      for (int piece : Piece.values) {
        for (int i = 0; i < Square.VALUES_LENGTH; ++i) {
          board[piece][i] = next();
        }
      }

      castlingRights[Castling.WHITE_KINGSIDE] = next();
      castlingRights[Castling.WHITE_QUEENSIDE] = next();
      castlingRights[Castling.BLACK_KINGSIDE] = next();
      castlingRights[Castling.BLACK_QUEENSIDE] = next();
      castlingRights[Castling.WHITE_KINGSIDE | Castling.WHITE_QUEENSIDE] =
          castlingRights[Castling.WHITE_KINGSIDE] ^ castlingRights[Castling.WHITE_QUEENSIDE];
      castlingRights[Castling.BLACK_KINGSIDE | Castling.BLACK_QUEENSIDE] =
          castlingRights[Castling.BLACK_KINGSIDE] ^ castlingRights[Castling.BLACK_QUEENSIDE];

      for (int i = 0; i < Square.VALUES_LENGTH; ++i) {
        enPassantSquare[i] = next();
      }
    }

    private static long next() {
      byte[] bytes = new byte[16];
      random.nextBytes(bytes);

      long hash = 0L;
      for (int i = 0; i < bytes.length; ++i) {
        hash ^= ((long) (bytes[i] & 0xFF)) << ((i * 8) % 64);
      }

      return hash;
    }
  }

  private static final class State {
    private long zobristKey = 0;
    private int castlingRights = Castling.NOCASTLING;
    private int enPassantSquare = Square.NOSQUARE;
    private int halfmoveClock = 0;
  }

  Position() {
    // Initialize board
    for (int square : Square.values) {
      board[square] = Piece.NOPIECE;
    }

    // Initialize piece type lists
    for (int color : Color.values) {
      for (int piecetype : PieceType.values) {
        pieces[color][piecetype] = new Bitboard();
      }
    }

    // Initialize states
    for (int i = 0; i < states.length; ++i) {
      states[i] = new State();
    }
  }

  void setActiveColor(int activeColor) {
    assert Color.isValid(activeColor);

    if (this.activeColor != activeColor) {
      this.activeColor = activeColor;
      zobristKey ^= Zobrist.activeColor;
    }
  }

  void setCastlingRight(int castling) {
    assert Castling.isValid(castling);

    if ((castlingRights & castling) == Castling.NOCASTLING) {
      castlingRights |= castling;
      zobristKey ^= Zobrist.castlingRights[castling];
    }
  }

  void setEnPassantSquare(int enPassantSquare) {
    if (this.enPassantSquare != Square.NOSQUARE) {
      zobristKey ^= Zobrist.enPassantSquare[this.enPassantSquare];
    }
    if (enPassantSquare != Square.NOSQUARE) {
      zobristKey ^= Zobrist.enPassantSquare[enPassantSquare];
    }
    this.enPassantSquare = enPassantSquare;
  }

  void setHalfmoveClock(int halfmoveClock) {
    assert halfmoveClock >= 0;

    this.halfmoveClock = halfmoveClock;
  }

  int getFullmoveNumber() {
    return halfmoveNumber / 2;
  }

  void setFullmoveNumber(int fullmoveNumber) {
    assert fullmoveNumber > 0;

    halfmoveNumber = fullmoveNumber * 2;
    if (activeColor == Color.BLACK) {
      ++halfmoveNumber;
    }
  }

  boolean isRepetition() {
    // Search back until the last halfmoveClock reset
    int j = Math.max(0, statesSize - halfmoveClock);
    for (int i = statesSize - 2; i >= j; i -= 2) {
      if (zobristKey == states[i].zobristKey) {
        return true;
      }
    }

    return false;
  }

  boolean hasInsufficientMaterial() {
    // If there is only one minor left, we are unable to checkmate
    return pieces[Color.WHITE][PieceType.PAWN].size() == 0 && pieces[Color.BLACK][PieceType.PAWN].size() == 0
        && pieces[Color.WHITE][PieceType.ROOK].size() == 0 && pieces[Color.BLACK][PieceType.ROOK].size() == 0
        && pieces[Color.WHITE][PieceType.QUEEN].size() == 0 && pieces[Color.BLACK][PieceType.QUEEN].size() == 0
        && (pieces[Color.WHITE][PieceType.KNIGHT].size() + pieces[Color.WHITE][PieceType.BISHOP].size() <= 1)
        && (pieces[Color.BLACK][PieceType.KNIGHT].size() + pieces[Color.BLACK][PieceType.BISHOP].size() <= 1);
  }

  /**
   * Puts a piece at the square. We need to update our board and the appropriate
   * piece type list.
   *
   * @param piece  the Piece.
   * @param square the Square.
   */
  void put(int piece, int square) {
    assert Piece.isValid(piece);
    assert Square.isValid(square);
    assert board[square] == Piece.NOPIECE;

    int piecetype = Piece.getType(piece);
    int color = Piece.getColor(piece);

    board[square] = piece;
    pieces[color][piecetype].add(square);
    material[color] += PieceType.getValue(piecetype);

    zobristKey ^= Zobrist.board[piece][square];
  }

  /**
   * Removes a piece from the square. We need to update our board and the
   * appropriate piece type list.
   *
   * @param square the Square.
   * @return the Piece which was removed.
   */
  int remove(int square) {
    assert Square.isValid(square);
    assert Piece.isValid(board[square]);

    int piece = board[square];

    int piecetype = Piece.getType(piece);
    int color = Piece.getColor(piece);

    board[square] = Piece.NOPIECE;
    pieces[color][piecetype].remove(square);
    material[color] -= PieceType.getValue(piecetype);

    zobristKey ^= Zobrist.board[piece][square];

    return piece;
  }

  void makeMove(int move) {
    // Save state
    State entry = states[statesSize];
    entry.zobristKey = zobristKey;
    entry.castlingRights = castlingRights;
    entry.enPassantSquare = enPassantSquare;
    entry.halfmoveClock = halfmoveClock;

    ++statesSize;
    assert statesSize < MAX_MOVES;

    // Get variables
    int type = Move.getType(move);
    int originSquare = Move.getOriginSquare(move);
    int targetSquare = Move.getTargetSquare(move);
    int originPiece = Move.getOriginPiece(move);
    int originColor = Piece.getColor(originPiece);
    int targetPiece = Move.getTargetPiece(move);

    // Remove target piece and update castling rights
    if (targetPiece != Piece.NOPIECE) {
      int captureSquare = targetSquare;
      if (type == MoveType.ENPASSANT) {
        captureSquare += (originColor == Color.WHITE ? Square.S : Square.N);
      }
      assert targetPiece == board[captureSquare];
      assert Piece.getType(targetPiece) != PieceType.KING;
      remove(captureSquare);

      clearCastling(captureSquare);
    }

    // Move piece
    assert originPiece == board[originSquare];
    remove(originSquare);
    if (type == MoveType.PAWNPROMOTION) {
      put(Piece.valueOf(originColor, Move.getPromotion(move)), targetSquare);
    } else {
      put(originPiece, targetSquare);
    }

    // Move rook and update castling rights
    if (type == MoveType.CASTLING) {
      int rookOriginSquare;
      int rookTargetSquare;
      switch (targetSquare) {
        case Square.g1:
          rookOriginSquare = Square.h1;
          rookTargetSquare = Square.f1;
          break;
        case Square.c1:
          rookOriginSquare = Square.a1;
          rookTargetSquare = Square.d1;
          break;
        case Square.g8:
          rookOriginSquare = Square.h8;
          rookTargetSquare = Square.f8;
          break;
        case Square.c8:
          rookOriginSquare = Square.a8;
          rookTargetSquare = Square.d8;
          break;
        default:
          throw new IllegalArgumentException();
      }

      assert Piece.getType(board[rookOriginSquare]) == PieceType.ROOK;
      int rookPiece = remove(rookOriginSquare);
      put(rookPiece, rookTargetSquare);
    }

    // Update castling
    clearCastling(originSquare);

    // Update enPassantSquare
    if (enPassantSquare != Square.NOSQUARE) {
      zobristKey ^= Zobrist.enPassantSquare[enPassantSquare];
    }
    if (type == MoveType.PAWNDOUBLE) {
      enPassantSquare = targetSquare + (originColor == Color.WHITE ? Square.S : Square.N);
      assert Square.isValid(enPassantSquare);
      zobristKey ^= Zobrist.enPassantSquare[enPassantSquare];
    } else {
      enPassantSquare = Square.NOSQUARE;
    }

    // Update activeColor
    activeColor = Color.opposite(activeColor);
    zobristKey ^= Zobrist.activeColor;

    // Update halfmoveClock
    if (Piece.getType(originPiece) == PieceType.PAWN || targetPiece != Piece.NOPIECE) {
      halfmoveClock = 0;
    } else {
      ++halfmoveClock;
    }

    // Update fullMoveNumber
    ++halfmoveNumber;
  }

  void undoMove(int move) {
    // Get variables
    int type = Move.getType(move);
    int originSquare = Move.getOriginSquare(move);
    int targetSquare = Move.getTargetSquare(move);
    int originPiece = Move.getOriginPiece(move);
    int originColor = Piece.getColor(originPiece);
    int targetPiece = Move.getTargetPiece(move);

    // Update fullMoveNumber
    --halfmoveNumber;

    // Update activeColor
    activeColor = Color.opposite(activeColor);

    // Undo move rook
    if (type == MoveType.CASTLING) {
      int rookOriginSquare;
      int rookTargetSquare;
      switch (targetSquare) {
        case Square.g1:
          rookOriginSquare = Square.h1;
          rookTargetSquare = Square.f1;
          break;
        case Square.c1:
          rookOriginSquare = Square.a1;
          rookTargetSquare = Square.d1;
          break;
        case Square.g8:
          rookOriginSquare = Square.h8;
          rookTargetSquare = Square.f8;
          break;
        case Square.c8:
          rookOriginSquare = Square.a8;
          rookTargetSquare = Square.d8;
          break;
        default:
          throw new IllegalArgumentException();
      }

      assert Piece.getType(board[rookTargetSquare]) == PieceType.ROOK;
      int rookPiece = remove(rookTargetSquare);
      put(rookPiece, rookOriginSquare);
    }

    // Undo move piece
    remove(targetSquare);
    put(originPiece, originSquare);

    // Restore target piece
    if (targetPiece != Piece.NOPIECE) {
      int captureSquare = targetSquare;
      if (type == MoveType.ENPASSANT) {
        captureSquare += (originColor == Color.WHITE ? Square.S : Square.N);
        assert Square.isValid(captureSquare);
      }
      put(targetPiece, captureSquare);
    }

    // Restore state
    assert statesSize > 0;
    --statesSize;

    State entry = states[statesSize];
    halfmoveClock = entry.halfmoveClock;
    enPassantSquare = entry.enPassantSquare;
    castlingRights = entry.castlingRights;
    zobristKey = entry.zobristKey;
  }

  private void clearCastling(int square) {
    assert Square.isValid(square);

    int newCastlingRights = castlingRights;

    switch (square) {
      case Square.a1:
        newCastlingRights &= ~Castling.WHITE_QUEENSIDE;
        break;
      case Square.a8:
        newCastlingRights &= ~Castling.BLACK_QUEENSIDE;
        break;
      case Square.h1:
        newCastlingRights &= ~Castling.WHITE_KINGSIDE;
        break;
      case Square.h8:
        newCastlingRights &= ~Castling.BLACK_KINGSIDE;
        break;
      case Square.e1:
        newCastlingRights &= ~(Castling.WHITE_KINGSIDE | Castling.WHITE_QUEENSIDE);
        break;
      case Square.e8:
        newCastlingRights &= ~(Castling.BLACK_KINGSIDE | Castling.BLACK_QUEENSIDE);
        break;
      default:
        return;
    }

    if (newCastlingRights != castlingRights) {
      castlingRights = newCastlingRights;
      zobristKey ^= Zobrist.castlingRights[newCastlingRights ^ castlingRights];
    }
  }

  boolean isCheck() {
    // Check whether our king is attacked by any opponent piece
    return isAttacked(Bitboard.next(pieces[activeColor][PieceType.KING].squares), Color.opposite(activeColor));
  }

  boolean isCheck(int color) {
    // Check whether the king for color is attacked by any opponent piece
    return isAttacked(Bitboard.next(pieces[color][PieceType.KING].squares), Color.opposite(color));
  }

  /**
   * Returns whether the targetSquare is attacked by any piece from the
   * attackerColor. We will backtrack from the targetSquare to find the piece.
   *
   * @param targetSquare  the target Square.
   * @param attackerColor the attacker Color.
   * @return whether the targetSquare is attacked.
   */
  boolean isAttacked(int targetSquare, int attackerColor) {
    assert Square.isValid(targetSquare);
    assert Color.isValid(attackerColor);

    // Pawn attacks
    int pawnPiece = Piece.valueOf(attackerColor, PieceType.PAWN);
    for (int i = 1; i < Square.pawnDirections[attackerColor].length; ++i) {
      int attackerSquare = targetSquare - Square.pawnDirections[attackerColor][i];
      if (Square.isValid(attackerSquare)) {
        int attackerPawn = board[attackerSquare];

        if (attackerPawn == pawnPiece) {
          return true;
        }
      }
    }

    return isAttacked(targetSquare,
        Piece.valueOf(attackerColor, PieceType.KNIGHT),
        Square.knightDirections)

        // The queen moves like a bishop, so check both piece types
        || isAttacked(targetSquare,
        Piece.valueOf(attackerColor, PieceType.BISHOP),
        Piece.valueOf(attackerColor, PieceType.QUEEN),
        Square.bishopDirections)

        // The queen moves like a rook, so check both piece types
        || isAttacked(targetSquare,
        Piece.valueOf(attackerColor, PieceType.ROOK),
        Piece.valueOf(attackerColor, PieceType.QUEEN),
        Square.rookDirections)

        || isAttacked(targetSquare,
        Piece.valueOf(attackerColor, PieceType.KING),
        Square.kingDirections);
  }

  /**
   * Returns whether the targetSquare is attacked by a non-sliding piece.
   */
  private boolean isAttacked(int targetSquare, int attackerPiece, int[] directions) {
    assert Square.isValid(targetSquare);
    assert Piece.isValid(attackerPiece);
    assert directions != null;

    for (int direction : directions) {
      int attackerSquare = targetSquare + direction;

      if (Square.isValid(attackerSquare) && board[attackerSquare] == attackerPiece) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns whether the targetSquare is attacked by a sliding piece.
   */
  private boolean isAttacked(int targetSquare, int attackerPiece, int queenPiece, int[] directions) {
    assert Square.isValid(targetSquare);
    assert Piece.isValid(attackerPiece);
    assert Piece.isValid(queenPiece);
    assert directions != null;

    for (int direction : directions) {
      int attackerSquare = targetSquare + direction;

      while (Square.isValid(attackerSquare)) {
        int piece = board[attackerSquare];

        if (Piece.isValid(piece)) {
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
