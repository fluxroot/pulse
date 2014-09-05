/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import java.security.SecureRandom;

final class Board {

  private static final int MAX_MOVES = Depth.MAX_PLY + 1024;

  final int[] board = new int[Square.LENGTH];

  final Bitboard[] pawns = new Bitboard[Color.values.length];
  final Bitboard[] knights = new Bitboard[Color.values.length];
  final Bitboard[] bishops = new Bitboard[Color.values.length];
  final Bitboard[] rooks = new Bitboard[Color.values.length];
  final Bitboard[] queens = new Bitboard[Color.values.length];
  final Bitboard[] kings = new Bitboard[Color.values.length];

  final int[] material = new int[Color.values.length];

  final int[] castlingRights = new int[Castling.values.length];
  int enPassantSquare = Square.NOSQUARE;
  int activeColor = Color.WHITE;
  int halfmoveClock = 0;
  private int halfmoveNumber = 2;

  long zobristKey = 0;

  // We will save some board parameters in a State before making a move.
  // Later we will restore them before undoing a move.
  private final State[] states = new State[MAX_MOVES];
  private int statesSize = 0;

  private static final class Zobrist {
    private static final SecureRandom random = new SecureRandom();

    static final long[][] board = new long[Piece.values.length][Square.LENGTH];
    static final long[] castlingRights = new long[Castling.values.length];
    static final long[] enPassantSquare = new long[Square.LENGTH];
    static final long activeColor = next();

    // Initialize the zobrist keys
    static {
      for (int piece : Piece.values) {
        for (int i = 0; i < Square.LENGTH; ++i) {
          board[piece][i] = next();
        }
      }

      for (int castling : Castling.values) {
        castlingRights[castling] = next();
      }

      for (int i = 0; i < Square.LENGTH; ++i) {
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
    private final int[] castlingRights = new int[Castling.values.length];
    private int enPassantSquare = Square.NOSQUARE;
    private int halfmoveClock = 0;

    private State() {
      for (int castling : Castling.values) {
        castlingRights[castling] = File.NOFILE;
      }
    }
  }

  Board() {
    // Initialize board
    for (int square : Square.values) {
      board[square] = Piece.NOPIECE;
    }

    // Initialize piece type lists
    for (int color : Color.values) {
      pawns[color] = new Bitboard();
      knights[color] = new Bitboard();
      bishops[color] = new Bitboard();
      rooks[color] = new Bitboard();
      queens[color] = new Bitboard();
      kings[color] = new Bitboard();
    }

    for (int castling : Castling.values) {
      castlingRights[castling] = File.NOFILE;
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

  void setCastlingRight(int castling, int file) {
    assert Castling.isValid(castling);

    if (castlingRights[castling] != File.NOFILE) {
      zobristKey ^= Zobrist.castlingRights[castling];
    }
    if (file != File.NOFILE) {
      zobristKey ^= Zobrist.castlingRights[castling];
    }
    castlingRights[castling] = file;
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
    return pawns[Color.WHITE].size() == 0 && pawns[Color.BLACK].size() == 0
        && rooks[Color.WHITE].size() == 0 && rooks[Color.BLACK].size() == 0
        && queens[Color.WHITE].size() == 0 && queens[Color.BLACK].size() == 0
        && (knights[Color.WHITE].size() + bishops[Color.WHITE].size() <= 1)
        && (knights[Color.BLACK].size() + bishops[Color.BLACK].size() <= 1);
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

    int pieceType = Piece.getType(piece);
    int color = Piece.getColor(piece);

    switch (pieceType) {
      case PieceType.PAWN:
        pawns[color].add(square);
        material[color] += PieceType.PAWN_VALUE;
        break;
      case PieceType.KNIGHT:
        knights[color].add(square);
        material[color] += PieceType.KNIGHT_VALUE;
        break;
      case PieceType.BISHOP:
        bishops[color].add(square);
        material[color] += PieceType.BISHOP_VALUE;
        break;
      case PieceType.ROOK:
        rooks[color].add(square);
        material[color] += PieceType.ROOK_VALUE;
        break;
      case PieceType.QUEEN:
        queens[color].add(square);
        material[color] += PieceType.QUEEN_VALUE;
        break;
      case PieceType.KING:
        kings[color].add(square);
        material[color] += PieceType.KING_VALUE;
        break;
      default:
        throw new IllegalArgumentException();
    }

    board[square] = piece;

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

    int pieceType = Piece.getType(piece);
    int color = Piece.getColor(piece);

    switch (pieceType) {
      case PieceType.PAWN:
        pawns[color].remove(square);
        material[color] -= PieceType.PAWN_VALUE;
        break;
      case PieceType.KNIGHT:
        knights[color].remove(square);
        material[color] -= PieceType.KNIGHT_VALUE;
        break;
      case PieceType.BISHOP:
        bishops[color].remove(square);
        material[color] -= PieceType.BISHOP_VALUE;
        break;
      case PieceType.ROOK:
        rooks[color].remove(square);
        material[color] -= PieceType.ROOK_VALUE;
        break;
      case PieceType.QUEEN:
        queens[color].remove(square);
        material[color] -= PieceType.QUEEN_VALUE;
        break;
      case PieceType.KING:
        kings[color].remove(square);
        material[color] -= PieceType.KING_VALUE;
        break;
      default:
        throw new IllegalArgumentException();
    }

    board[square] = Piece.NOPIECE;

    zobristKey ^= Zobrist.board[piece][square];

    return piece;
  }

  void makeMove(int move) {
    State entry = states[statesSize];

    // Get variables
    int type = Move.getType(move);
    int originSquare = Move.getOriginSquare(move);
    int targetSquare = Move.getTargetSquare(move);
    int originPiece = Move.getOriginPiece(move);
    int originColor = Piece.getColor(originPiece);
    int targetPiece = Move.getTargetPiece(move);

    // Save zobristKey
    entry.zobristKey = zobristKey;

    // Save castling rights
    for (int castling : Castling.values) {
      entry.castlingRights[castling] = castlingRights[castling];
    }

    // Save enPassantSquare
    entry.enPassantSquare = enPassantSquare;

    // Save halfmoveClock
    entry.halfmoveClock = halfmoveClock;

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

    ++statesSize;
    assert statesSize < MAX_MOVES;
  }

  void undoMove(int move) {
    --statesSize;
    assert statesSize >= 0;

    State entry = states[statesSize];

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

    // Restore halfmoveClock
    halfmoveClock = entry.halfmoveClock;

    // Restore enPassantSquare
    enPassantSquare = entry.enPassantSquare;

    // Restore castling rights
    for (int castling : Castling.values) {
      if (entry.castlingRights[castling] != castlingRights[castling]) {
        castlingRights[castling] = entry.castlingRights[castling];
      }
    }

    // Restore zobristKey
    zobristKey = entry.zobristKey;
  }

  private void clearCastlingRights(int castling) {
    assert Castling.isValid(castling);

    if (castlingRights[castling] != File.NOFILE) {
      castlingRights[castling] = File.NOFILE;
      zobristKey ^= Zobrist.castlingRights[castling];
    }
  }

  private void clearCastling(int square) {
    assert Square.isValid(square);

    switch (square) {
      case Square.a1:
        clearCastlingRights(Castling.WHITE_QUEENSIDE);
        break;
      case Square.h1:
        clearCastlingRights(Castling.WHITE_KINGSIDE);
        break;
      case Square.a8:
        clearCastlingRights(Castling.BLACK_QUEENSIDE);
        break;
      case Square.h8:
        clearCastlingRights(Castling.BLACK_KINGSIDE);
        break;
      case Square.e1:
        clearCastlingRights(Castling.WHITE_QUEENSIDE);
        clearCastlingRights(Castling.WHITE_KINGSIDE);
        break;
      case Square.e8:
        clearCastlingRights(Castling.BLACK_QUEENSIDE);
        clearCastlingRights(Castling.BLACK_KINGSIDE);
        break;
      default:
        break;
    }
  }

  boolean isCheck() {
    // Check whether our king is attacked by any opponent piece
    return isAttacked(Bitboard.next(kings[activeColor].squares), Color.opposite(activeColor));
  }

  boolean isCheck(int color) {
    // Check whether the king for color is attacked by any opponent piece
    return isAttacked(Bitboard.next(kings[color].squares), Color.opposite(color));
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
  private boolean isAttacked(int targetSquare, int attackerPiece, int[] moveDelta) {
    assert Square.isValid(targetSquare);
    assert Piece.isValid(attackerPiece);
    assert moveDelta != null;

    for (int delta : moveDelta) {
      int attackerSquare = targetSquare + delta;

      if (Square.isValid(attackerSquare) && board[attackerSquare] == attackerPiece) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns whether the targetSquare is attacked by a sliding piece.
   */
  private boolean isAttacked(int targetSquare, int attackerPiece, int queenPiece, int[] moveDelta) {
    assert Square.isValid(targetSquare);
    assert Piece.isValid(attackerPiece);
    assert Piece.isValid(queenPiece);
    assert moveDelta != null;

    for (int delta : moveDelta) {
      int attackerSquare = targetSquare + delta;

      while (Square.isValid(attackerSquare)) {
        int piece = board[attackerSquare];

        if (Piece.isValid(piece)) {
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
