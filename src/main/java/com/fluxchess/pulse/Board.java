/*
 * Copyright 2013-2014 the original author or authors.
 *
 * This file is part of Pulse Chess.
 *
 * Pulse Chess is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pulse Chess is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pulse Chess.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericColor;
import com.fluxchess.jcpi.models.GenericFile;
import com.fluxchess.jcpi.models.GenericPiece;

import java.security.SecureRandom;

/**
 * This is our internal board.
 */
public final class Board {

  private static final int MAX_GAMEMOVES = Search.MAX_HEIGHT + 1024;

  private static final int BOARDSIZE = 128;

  public final int[] board = new int[BOARDSIZE];

  public final Bitboard[] pawns = new Bitboard[Color.values.length];
  public final Bitboard[] knights = new Bitboard[Color.values.length];
  public final Bitboard[] bishops = new Bitboard[Color.values.length];
  public final Bitboard[] rooks = new Bitboard[Color.values.length];
  public final Bitboard[] queens = new Bitboard[Color.values.length];
  public final Bitboard[] kings = new Bitboard[Color.values.length];

  public final int[][] colorCastling = new int[Color.values.length][Castling.values.length];
  public int enPassant = Square.NOSQUARE;
  public int activeColor = Color.WHITE;
  public int halfMoveClock = 0;
  private int halfMoveNumber;

  public long zobristCode = 0;
  private static final long[][] zobristPiece = new long[Piece.values.length][BOARDSIZE];
  private static final long[][] zobristCastling = new long[Color.values.length][Castling.values.length];
  private static final long[] zobristEnPassant = new long[BOARDSIZE];
  private static final long zobristActiveColor;

  // We will save some board parameters in a State before making a move.
  // Later we will restore them before undoing a move.
  private final State[] stack = new State[MAX_GAMEMOVES];
  private int stackSize = 0;

  private static final class State {
    public long zobristCode = 0;
    public final int[][] castling = new int[Color.values.length][Castling.values.length];
    public int enPassant = Square.NOSQUARE;
    public int halfMoveClock = 0;

    public State() {
      for (int color : Color.values) {
        for (int castling : Castling.values) {
          this.castling[color][castling] = File.NOFILE;
        }
      }
    }
  }

  private static final class Zobrist {
    final SecureRandom random = new SecureRandom();

    byte[] result() {
      // Generate some random bytes for our keys
      byte[] bytes = new byte[16];
      random.nextBytes(bytes);
      return bytes;
    }

    public long next() {
      byte[] result = result();

      long hash = 0L;
      for (int i = 0; i < result.length; ++i) {
        hash ^= ((long) (result[i] & 0xFF)) << ((i * 8) % 64);
      }

      return hash;
    }
  }

  // Initialize the zobrist keys
  static {
    Zobrist zobrist = new Zobrist();

    for (int piece : Piece.values) {
      for (int i = 0; i < BOARDSIZE; ++i) {
        zobristPiece[Piece.ordinal(piece)][i] = Math.abs(zobrist.next());
      }
    }

    zobristCastling[Color.WHITE][Castling.KINGSIDE] = Math.abs(zobrist.next());
    zobristCastling[Color.WHITE][Castling.QUEENSIDE] = Math.abs(zobrist.next());
    zobristCastling[Color.BLACK][Castling.KINGSIDE] = Math.abs(zobrist.next());
    zobristCastling[Color.BLACK][Castling.QUEENSIDE] = Math.abs(zobrist.next());

    for (int i = 0; i < BOARDSIZE; ++i) {
      zobristEnPassant[i] = Math.abs(zobrist.next());
    }

    zobristActiveColor = Math.abs(zobrist.next());
  }

  public Board(GenericBoard genericBoard) {
    assert genericBoard != null;

    // Initialize stack
    for (int i = 0; i < stack.length; ++i) {
      stack[i] = new State();
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

    // Initialize board
    for (int square : Square.values) {
      board[square] = Piece.NOPIECE;

      GenericPiece genericPiece = genericBoard.getPiece(Square.toGenericPosition(square));
      if (genericPiece != null) {
        int piece = Piece.valueOf(genericPiece);
        put(piece, square);
      }
    }

    // Initialize castling
    for (int color : Color.values) {
      for (int castling : Castling.values) {
        GenericFile genericFile = genericBoard.getCastling(
            Color.toGenericColor(color), Castling.toGenericCastling(castling)
        );
        if (genericFile != null) {
          colorCastling[color][castling] = File.valueOf(genericFile);
          zobristCode ^= zobristCastling[color][castling];
        } else {
          colorCastling[color][castling] = File.NOFILE;
        }
      }
    }

    // Initialize en passant
    if (genericBoard.getEnPassant() != null) {
      enPassant = Square.valueOf(genericBoard.getEnPassant());
      zobristCode ^= zobristEnPassant[enPassant];
    }

    // Initialize active color
    if (activeColor != Color.valueOf(genericBoard.getActiveColor())) {
      activeColor = Color.valueOf(genericBoard.getActiveColor());
      zobristCode ^= zobristActiveColor;
    }

    // Initialize half move clock
    halfMoveClock = genericBoard.getHalfMoveClock();

    // Initialize the full move number
    setFullMoveNumber(genericBoard.getFullMoveNumber());
  }

  public GenericBoard toGenericBoard() {
    GenericBoard genericBoard = new GenericBoard();

    // Set board
    for (int square : Square.values) {
      if (board[square] != Piece.NOPIECE) {
        genericBoard.setPiece(Piece.toGenericPiece(board[square]), Square.toGenericPosition(square));
      }
    }

    // Set castling
    for (int color : Color.values) {
      for (int castling : Castling.values) {
        if (colorCastling[color][castling] != File.NOFILE) {
          genericBoard.setCastling(
              Color.toGenericColor(color),
              Castling.toGenericCastling(castling),
              File.toGenericFile(colorCastling[color][castling])
          );
        }
      }
    }

    // Set en passant
    if (enPassant != Square.NOSQUARE) {
      genericBoard.setEnPassant(Square.toGenericPosition(enPassant));
    }

    // Set active color
    genericBoard.setActiveColor(Color.toGenericColor(activeColor));

    // Set half move clock
    genericBoard.setHalfMoveClock(halfMoveClock);

    // Set full move number
    genericBoard.setFullMoveNumber(getFullMoveNumber());

    return genericBoard;
  }

  public String toString() {
    return toGenericBoard().toString();
  }

  public int getFullMoveNumber() {
    return halfMoveNumber / 2;
  }

  private void setFullMoveNumber(int fullMoveNumber) {
    assert fullMoveNumber > 0;

    halfMoveNumber = fullMoveNumber * 2;
    if (activeColor == Color.valueOf(GenericColor.BLACK)) {
      ++halfMoveNumber;
    }
  }

  public boolean isRepetition() {
    int j = Math.max(0, stackSize - halfMoveClock);
    for (int i = stackSize - 2; i >= j; i -= 2) {
      if (zobristCode == stack[i].zobristCode) {
        return true;
      }
    }

    return false;
  }

  /**
   * Puts a piece at the square. We need to update our board and the appropriate
   * piece type list.
   *
   * @param piece  the Piece.
   * @param square the Square.
   */
  private void put(int piece, int square) {
    assert Piece.isValid(piece);
    assert Square.isValid(square);
    assert board[square] == Piece.NOPIECE;

    int pieceType = Piece.getType(piece);
    int color = Piece.getColor(piece);

    switch (pieceType) {
      case Piece.Type.PAWN:
        pawns[color].add(square);
        break;
      case Piece.Type.KNIGHT:
        knights[color].add(square);
        break;
      case Piece.Type.BISHOP:
        bishops[color].add(square);
        break;
      case Piece.Type.ROOK:
        rooks[color].add(square);
        break;
      case Piece.Type.QUEEN:
        queens[color].add(square);
        break;
      case Piece.Type.KING:
        kings[color].add(square);
        break;
      default:
        assert false : pieceType;
        break;
    }

    board[square] = piece;

    zobristCode ^= zobristPiece[Piece.ordinal(piece)][square];
  }

  /**
   * Removes a piece from the square. We need to update our board and the
   * appropriate piece type list.
   *
   * @param square the Square.
   * @return the Piece which was removed.
   */
  private int remove(int square) {
    assert Square.isValid(square);
    assert Piece.isValid(board[square]);

    int piece = board[square];

    int pieceType = Piece.getType(piece);
    int color = Piece.getColor(piece);

    switch (pieceType) {
      case Piece.Type.PAWN:
        pawns[color].remove(square);
        break;
      case Piece.Type.KNIGHT:
        knights[color].remove(square);
        break;
      case Piece.Type.BISHOP:
        bishops[color].remove(square);
        break;
      case Piece.Type.ROOK:
        rooks[color].remove(square);
        break;
      case Piece.Type.QUEEN:
        queens[color].remove(square);
        break;
      case Piece.Type.KING:
        kings[color].remove(square);
        break;
      default:
        assert false : pieceType;
        break;
    }

    board[square] = Piece.NOPIECE;

    zobristCode ^= zobristPiece[Piece.ordinal(piece)][square];

    return piece;
  }

  public void makeMove(int move) {
    State entry = stack[stackSize];

    // Get variables
    int type = Move.getType(move);
    int originSquare = Move.getOriginSquare(move);
    int targetSquare = Move.getTargetSquare(move);
    int originPiece = Move.getOriginPiece(move);
    int originColor = Piece.getColor(originPiece);
    int targetPiece = Move.getTargetPiece(move);
    int captureSquare = targetSquare;
    if (type == Move.Type.ENPASSANT) {
      captureSquare += (originColor == Color.WHITE ? Square.deltaS : Square.deltaN);
    }

    // Save zobristCode
    entry.zobristCode = zobristCode;

    // Save castling rights
    for (int color : Color.values) {
      for (int castling : Castling.values) {
        entry.castling[color][castling] = colorCastling[color][castling];
      }
    }

    // Save enPassant
    entry.enPassant = enPassant;

    // Save halfMoveClock
    entry.halfMoveClock = halfMoveClock;

    // Remove target piece and update castling rights
    if (targetPiece != Piece.NOPIECE) {
      assert targetPiece == board[captureSquare];
      remove(captureSquare);

      clearCastling(captureSquare);
    }

    // Move piece
    assert originPiece == board[originSquare];
    remove(originSquare);
    if (type == Move.Type.PAWNPROMOTION) {
      put(Piece.valueOf(Move.getPromotion(move), originColor), targetSquare);
    } else {
      put(originPiece, targetSquare);
    }

    // Move rook and update castling rights
    if (type == Move.Type.CASTLING) {
      int rookOriginSquare = Square.NOSQUARE;
      int rookTargetSquare = Square.NOSQUARE;
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
          assert false : targetSquare;
          break;
      }

      assert Piece.getType(board[rookOriginSquare]) == Piece.Type.ROOK;
      int rookPiece = remove(rookOriginSquare);
      put(rookPiece, rookTargetSquare);
    }

    // Update castling
    clearCastling(originSquare);

    // Update enPassant
    if (enPassant != Square.NOSQUARE) {
      zobristCode ^= zobristEnPassant[enPassant];
    }
    if (type == Move.Type.PAWNDOUBLE) {
      enPassant = targetSquare + (originColor == Color.WHITE ? Square.deltaS : Square.deltaN);
      assert Square.isValid(enPassant);
      zobristCode ^= zobristEnPassant[enPassant];
    } else {
      enPassant = Square.NOSQUARE;
    }

    // Update activeColor
    activeColor = Color.opposite(activeColor);
    zobristCode ^= zobristActiveColor;

    // Update halfMoveClock
    if (Piece.getType(originPiece) == Piece.Type.PAWN || targetPiece != Piece.NOPIECE) {
      halfMoveClock = 0;
    } else {
      ++halfMoveClock;
    }

    // Update fullMoveNumber
    ++halfMoveNumber;

    ++stackSize;
    assert stackSize < MAX_GAMEMOVES;
  }

  public void undoMove(int move) {
    --stackSize;
    assert stackSize >= 0;

    State entry = stack[stackSize];

    // Get variables
    int type = Move.getType(move);
    int originSquare = Move.getOriginSquare(move);
    int targetSquare = Move.getTargetSquare(move);
    int originPiece = Move.getOriginPiece(move);
    int originColor = Piece.getColor(originPiece);
    int targetPiece = Move.getTargetPiece(move);
    int captureSquare = targetSquare;
    if (type == Move.Type.ENPASSANT) {
      captureSquare += (originColor == Color.WHITE ? Square.deltaS : Square.deltaN);
      assert Square.isValid(captureSquare);
    }

    // Update fullMoveNumber
    --halfMoveNumber;

    // Update activeColor
    activeColor = Color.opposite(activeColor);

    // Undo move rook
    if (type == Move.Type.CASTLING) {
      int rookOriginSquare = Square.NOSQUARE;
      int rookTargetSquare = Square.NOSQUARE;
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
          assert false : targetSquare;
          break;
      }

      assert Piece.getType(board[rookTargetSquare]) == Piece.Type.ROOK;
      int rookPiece = remove(rookTargetSquare);
      put(rookPiece, rookOriginSquare);
    }

    // Undo move piece
    remove(targetSquare);
    put(originPiece, originSquare);

    // Restore target piece
    if (targetPiece != Piece.NOPIECE) {
      put(targetPiece, captureSquare);
    }

    // Restore halfMoveClock
    halfMoveClock = entry.halfMoveClock;

    // Restore enPassant
    enPassant = entry.enPassant;

    // Restore castling rights
    for (int color : Color.values) {
      for (int castling : Castling.values) {
        if (entry.castling[color][castling] != colorCastling[color][castling]) {
          colorCastling[color][castling] = entry.castling[color][castling];
        }
      }
    }

    // Restore zobristCode
    zobristCode = entry.zobristCode;
  }

  private void clearCastling(int color, int castling) {
    assert Color.isValid(color);
    assert Castling.isValid(castling);

    if (colorCastling[color][castling] != File.NOFILE) {
      colorCastling[color][castling] = File.NOFILE;
      zobristCode ^= zobristCastling[color][castling];
    }
  }

  private void clearCastling(int square) {
    assert Square.isLegal(square);

    switch (square) {
      case Square.a1:
        clearCastling(Color.WHITE, Castling.QUEENSIDE);
        break;
      case Square.h1:
        clearCastling(Color.WHITE, Castling.KINGSIDE);
        break;
      case Square.a8:
        clearCastling(Color.BLACK, Castling.QUEENSIDE);
        break;
      case Square.h8:
        clearCastling(Color.BLACK, Castling.KINGSIDE);
        break;
      case Square.e1:
        clearCastling(Color.WHITE, Castling.QUEENSIDE);
        clearCastling(Color.WHITE, Castling.KINGSIDE);
        break;
      case Square.e8:
        clearCastling(Color.BLACK, Castling.QUEENSIDE);
        clearCastling(Color.BLACK, Castling.KINGSIDE);
        break;
      default:
        break;
    }
  }

  public boolean isCheck() {
    // Check whether our king is attacked by any opponent piece
    return isAttacked(Bitboard.next(kings[activeColor].squares), Color.opposite(activeColor));
  }

  /**
   * Returns whether the targetSquare is attacked by any piece from the
   * attackerColor. We will backtrack from the targetSquare to find the piece.
   *
   * @param targetSquare  the target Square.
   * @param attackerColor the attacker Color.
   * @return whether the targetSquare is attacked.
   */
  public boolean isAttacked(int targetSquare, int attackerColor) {
    assert Square.isValid(targetSquare);
    assert Color.isValid(attackerColor);

    // Pawn attacks
    int pawnPiece = Piece.valueOf(Piece.Type.PAWN, attackerColor);
    for (int i = 1; i < MoveGenerator.moveDeltaPawn[attackerColor].length; ++i) {
      int attackerSquare = targetSquare - MoveGenerator.moveDeltaPawn[attackerColor][i];
      if (Square.isLegal(attackerSquare)) {
        int attackerPawn = board[attackerSquare];

        if (attackerPawn == pawnPiece) {
          return true;
        }
      }
    }

    return isAttacked(targetSquare, attackerColor, Piece.Type.KNIGHT, MoveGenerator.moveDeltaKnight)
        || isAttacked(targetSquare, attackerColor, Piece.Type.BISHOP, MoveGenerator.moveDeltaBishop)
        || isAttacked(targetSquare, attackerColor, Piece.Type.ROOK, MoveGenerator.moveDeltaRook)
        || isAttacked(targetSquare, attackerColor, Piece.Type.QUEEN, MoveGenerator.moveDeltaQueen)
        || isAttacked(targetSquare, attackerColor, Piece.Type.KING, MoveGenerator.moveDeltaKing);
  }

  private boolean isAttacked(int targetSquare, int attackerColor, int attackerPieceType, int[] moveDelta) {
    assert Square.isValid(targetSquare);
    assert Color.isValid(attackerColor);
    assert Piece.Type.isValid(attackerPieceType);
    assert moveDelta != null;

    boolean sliding = Piece.Type.isSliding(attackerPieceType);

    for (int delta : moveDelta) {
      int attackerSquare = targetSquare + delta;

      while (Square.isLegal(attackerSquare)) {
        int attackerPiece = board[attackerSquare];

        if (Piece.isValid(attackerPiece)) {
          if (Piece.getType(attackerPiece) == attackerPieceType
              && Piece.getColor(attackerPiece) == attackerColor) {
            return true;
          }

          break;
        } else {
          if (!sliding) {
            break;
          }

          attackerSquare += delta;
        }
      }
    }

    return false;
  }

}
