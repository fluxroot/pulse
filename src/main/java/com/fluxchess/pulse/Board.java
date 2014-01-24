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

import com.fluxchess.jcpi.models.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * This is our internal board.
 */
public final class Board {

  private static final int BOARDSIZE = 128;
  private static final int MAX_GAMEMOVES = 4096;

  public final int[] board = new int[BOARDSIZE];

  public final ChessmanList[] pawns = new ChessmanList[IntColor.values.length];
  public final ChessmanList[] knights = new ChessmanList[IntColor.values.length];
  public final ChessmanList[] bishops = new ChessmanList[IntColor.values.length];
  public final ChessmanList[] rooks = new ChessmanList[IntColor.values.length];
  public final ChessmanList[] queens = new ChessmanList[IntColor.values.length];
  public final ChessmanList[] kings = new ChessmanList[IntColor.values.length];

  public final int[][] castling = new int[IntColor.values.length][IntCastling.values.length];
  public int enPassant = Square.NOSQUARE;
  public int activeColor = IntColor.WHITE;
  public int halfMoveClock = 0;
  private int halfMoveNumber;

  public long zobristCode = 0;
  private static final long[][] zobristPiece = new long[IntPiece.values.length][BOARDSIZE];
  private static final long[][] zobristCastling = new long[IntColor.values.length][IntCastling.values.length];
  private static final long[] zobristEnPassant = new long[BOARDSIZE];
  private static final long zobristActiveColor;

  private final Set<Long> repetitionTable = new HashSet<>();

  // We will save some board parameters in a State before making a move.
  // Later we will restore them before undoing a move.
  private final State[] stack = new State[MAX_GAMEMOVES];
  private int stackSize = 0;

  private static final class State {
    public long zobristCode = 0;
    public final int[][] castling = new int[IntColor.values.length][IntCastling.values.length];
    public int enPassant = Square.NOSQUARE;
    public int halfMoveClock = 0;

    public State() {
      for (int color : IntColor.values) {
        for (int castling : IntCastling.values) {
          this.castling[color][castling] = IntFile.NOFILE;
        }
      }
    }
  }

  // Initialize the zobrist keys
  static {
    Random random = new Random();

    for (int piece : IntPiece.values) {
      for (int i = 0; i < BOARDSIZE; ++i) {
        zobristPiece[IntPiece.ordinal(piece)][i] = Math.abs(random.nextLong());
      }
    }

    zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE] = Math.abs(random.nextLong());
    zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE] = Math.abs(random.nextLong());
    zobristCastling[IntColor.BLACK][IntCastling.KINGSIDE] = Math.abs(random.nextLong());
    zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE] = Math.abs(random.nextLong());

    for (int i = 0; i < BOARDSIZE; ++i) {
      zobristEnPassant[i] = Math.abs(random.nextLong());
    }

    zobristActiveColor = Math.abs(random.nextLong());
  }

  public Board(GenericBoard genericBoard) {
    assert genericBoard != null;

    // Initialize repetition table
    repetitionTable.clear();

    // Initialize stack
    for (int i = 0; i < stack.length; ++i) {
      stack[i] = new State();
    }

    // Initialize chessman lists
    for (int color : IntColor.values) {
      pawns[color] = new ChessmanList();
      knights[color] = new ChessmanList();
      bishops[color] = new ChessmanList();
      rooks[color] = new ChessmanList();
      queens[color] = new ChessmanList();
      kings[color] = new ChessmanList();
    }

    // Initialize board
    for (int square : Square.values) {
      board[square] = IntPiece.NOPIECE;

      GenericPiece genericPiece = genericBoard.getPiece(Square.toGenericPosition(square));
      if (genericPiece != null) {
        int piece = IntPiece.valueOf(genericPiece);
        put(piece, square);
      }
    }

    // Initialize castling
    for (int color : IntColor.values) {
      for (int castling : IntCastling.values) {
        GenericFile genericFile = genericBoard.getCastling(IntColor.toGenericColor(color), IntCastling.toGenericCastling(castling));
        if (genericFile != null) {
          this.castling[color][castling] = IntFile.valueOf(genericFile);
          zobristCode ^= zobristCastling[color][castling];
        } else {
          this.castling[color][castling] = IntFile.NOFILE;
        }
      }
    }

    // Initialize en passant
    if (genericBoard.getEnPassant() != null) {
      enPassant = Square.valueOf(genericBoard.getEnPassant());
      zobristCode ^= zobristEnPassant[enPassant];
    }

    // Initialize active color
    if (activeColor != IntColor.valueOf(genericBoard.getActiveColor())) {
      activeColor = IntColor.valueOf(genericBoard.getActiveColor());
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
      if (board[square] != IntPiece.NOPIECE) {
        genericBoard.setPiece(IntPiece.toGenericPiece(board[square]), Square.toGenericPosition(square));
      }
    }

    // Set castling
    for (int color : IntColor.values) {
      for (int castling : IntCastling.values) {
        if (this.castling[color][castling] != IntFile.NOFILE) {
          genericBoard.setCastling(IntColor.toGenericColor(color), IntCastling.toGenericCastling(castling), IntFile.toGenericFile(this.castling[color][castling]));
        }
      }
    }

    // Set en passant
    if (enPassant != Square.NOSQUARE) {
      genericBoard.setEnPassant(Square.toGenericPosition(enPassant));
    }

    // Set active color
    genericBoard.setActiveColor(IntColor.toGenericColor(activeColor));

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
    if (activeColor == IntColor.valueOf(GenericColor.BLACK)) {
      ++halfMoveNumber;
    }
  }

  public boolean isRepetition() {
    return repetitionTable.contains(zobristCode);
  }

  private void put(int piece, int square) {
    assert IntPiece.isValid(piece);
    assert Square.isValid(square);
    assert board[square] == IntPiece.NOPIECE;

    int chessman = IntPiece.getChessman(piece);
    int color = IntPiece.getColor(piece);

    switch (chessman) {
      case IntChessman.PAWN:
        pawns[color].add(square);
        break;
      case IntChessman.KNIGHT:
        knights[color].add(square);
        break;
      case IntChessman.BISHOP:
        bishops[color].add(square);
        break;
      case IntChessman.ROOK:
        rooks[color].add(square);
        break;
      case IntChessman.QUEEN:
        queens[color].add(square);
        break;
      case IntChessman.KING:
        kings[color].add(square);
        break;
      default:
        assert false : chessman;
        break;
    }

    board[square] = piece;

    zobristCode ^= zobristPiece[IntPiece.ordinal(piece)][square];
  }

  private int remove(int square) {
    assert Square.isValid(square);
    assert IntPiece.isValid(board[square]);

    int piece = board[square];

    int chessman = IntPiece.getChessman(piece);
    int color = IntPiece.getColor(piece);

    switch (chessman) {
      case IntChessman.PAWN:
        pawns[color].remove(square);
        break;
      case IntChessman.KNIGHT:
        knights[color].remove(square);
        break;
      case IntChessman.BISHOP:
        bishops[color].remove(square);
        break;
      case IntChessman.ROOK:
        rooks[color].remove(square);
        break;
      case IntChessman.QUEEN:
        queens[color].remove(square);
        break;
      case IntChessman.KING:
        kings[color].remove(square);
        break;
      default:
        assert false : chessman;
        break;
    }

    board[square] = IntPiece.NOPIECE;

    zobristCode ^= zobristPiece[IntPiece.ordinal(piece)][square];

    return piece;
  }

  public void makeMove(int move) {
    State entry = stack[stackSize];

    // Get variables
    int type = Move.getType(move);
    int originSquare = Move.getOriginSquare(move);
    int targetSquare = Move.getTargetSquare(move);
    int originPiece = Move.getOriginPiece(move);
    int originColor = IntPiece.getColor(originPiece);
    int targetPiece = Move.getTargetPiece(move);
    int captureSquare = targetSquare;
    if (type == Move.Type.ENPASSANT) {
      captureSquare += (originColor == IntColor.WHITE ? Square.deltaS : Square.deltaN);
    }

    // Update repetition table
    repetitionTable.add(zobristCode);

    // Save zobristCode
    entry.zobristCode = zobristCode;

    // Save castling rights
    for (int color : IntColor.values) {
      for (int castling : IntCastling.values) {
        entry.castling[color][castling] = this.castling[color][castling];
      }
    }

    // Save enPassant
    entry.enPassant = enPassant;

    // Save halfMoveClock
    entry.halfMoveClock = halfMoveClock;

    // Remove target piece and update castling rights
    if (targetPiece != IntPiece.NOPIECE) {
      assert targetPiece == board[captureSquare];
      remove(captureSquare);

      clearCastling(captureSquare);
    }

    // Move piece
    assert originPiece == board[originSquare];
    remove(originSquare);
    if (type == Move.Type.PAWNPROMOTION) {
      put(IntPiece.valueOf(Move.getPromotion(move), originColor), targetSquare);
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

      assert IntPiece.getChessman(board[rookOriginSquare]) == IntChessman.ROOK;
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
      enPassant = targetSquare + (originColor == IntColor.WHITE ? Square.deltaS : Square.deltaN);
      assert Square.isValid(enPassant);
      zobristCode ^= zobristEnPassant[enPassant];
    } else {
      enPassant = Square.NOSQUARE;
    }

    // Update activeColor
    activeColor = IntColor.opposite(activeColor);
    zobristCode ^= zobristActiveColor;

    // Update halfMoveClock
    if (IntPiece.getChessman(originPiece) == IntChessman.PAWN || targetPiece != IntPiece.NOPIECE) {
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
    int originColor = IntPiece.getColor(originPiece);
    int targetPiece = Move.getTargetPiece(move);
    int captureSquare = targetSquare;
    if (type == Move.Type.ENPASSANT) {
      captureSquare += (originColor == IntColor.WHITE ? Square.deltaS : Square.deltaN);
      assert Square.isValid(captureSquare);
    }

    // Update fullMoveNumber
    --halfMoveNumber;

    // Update activeColor
    activeColor = IntColor.opposite(activeColor);

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

      assert IntPiece.getChessman(board[rookTargetSquare]) == IntChessman.ROOK;
      int rookPiece = remove(rookTargetSquare);
      put(rookPiece, rookOriginSquare);
    }

    // Undo move piece
    remove(targetSquare);
    put(originPiece, originSquare);

    // Restore target piece
    if (targetPiece != IntPiece.NOPIECE) {
      put(targetPiece, captureSquare);
    }

    // Restore halfMoveClock
    halfMoveClock = entry.halfMoveClock;

    // Restore enPassant
    enPassant = entry.enPassant;

    // Restore castling rights
    for (int color : IntColor.values) {
      for (int castling : IntCastling.values) {
        if (entry.castling[color][castling] != this.castling[color][castling]) {
          this.castling[color][castling] = entry.castling[color][castling];
        }
      }
    }

    // Restore zobristCode
    zobristCode = entry.zobristCode;

    // Update repetition table
    repetitionTable.remove(zobristCode);
  }

  private void clearCastling(int color, int castling) {
    assert IntColor.isValid(color);
    assert IntCastling.isValid(castling);

    if (this.castling[color][castling] != IntFile.NOFILE) {
      this.castling[color][castling] = IntFile.NOFILE;
      zobristCode ^= zobristCastling[color][castling];
    }
  }

  private void clearCastling(int square) {
    assert Square.isLegal(square);

    switch (square) {
      case Square.a1:
        clearCastling(IntColor.WHITE, IntCastling.QUEENSIDE);
        break;
      case Square.h1:
        clearCastling(IntColor.WHITE, IntCastling.KINGSIDE);
        break;
      case Square.a8:
        clearCastling(IntColor.BLACK, IntCastling.QUEENSIDE);
        break;
      case Square.h8:
        clearCastling(IntColor.BLACK, IntCastling.KINGSIDE);
        break;
      case Square.e1:
        clearCastling(IntColor.WHITE, IntCastling.QUEENSIDE);
        clearCastling(IntColor.WHITE, IntCastling.KINGSIDE);
        break;
      case Square.e8:
        clearCastling(IntColor.BLACK, IntCastling.QUEENSIDE);
        clearCastling(IntColor.BLACK, IntCastling.KINGSIDE);
        break;
      default:
        break;
    }
  }

  public boolean isCheck() {
    // Check whether our king is attacked by any opponent piece
    return isAttacked(ChessmanList.next(kings[activeColor].squares), IntColor.opposite(activeColor));
  }

  public boolean isAttacked(int targetSquare, int attackerColor) {
    assert Square.isValid(targetSquare);
    assert IntColor.isValid(attackerColor);

    // Pawn attacks
    int pawnPiece = IntPiece.valueOf(IntChessman.PAWN, attackerColor);
    for (int i = 1; i < MoveGenerator.moveDeltaPawn[attackerColor].length; ++i) {
      int attackerSquare = targetSquare - MoveGenerator.moveDeltaPawn[attackerColor][i];
      if (Square.isLegal(attackerSquare)) {
        int attackerPawn = board[attackerSquare];

        if (attackerPawn == pawnPiece) {
          return true;
        }
      }
    }

    return isAttacked(targetSquare, attackerColor, IntChessman.KNIGHT, MoveGenerator.moveDeltaKnight)
      || isAttacked(targetSquare, attackerColor, IntChessman.BISHOP, MoveGenerator.moveDeltaBishop)
      || isAttacked(targetSquare, attackerColor, IntChessman.ROOK, MoveGenerator.moveDeltaRook)
      || isAttacked(targetSquare, attackerColor, IntChessman.QUEEN, MoveGenerator.moveDeltaQueen)
      || isAttacked(targetSquare, attackerColor, IntChessman.KING, MoveGenerator.moveDeltaKing);
  }

  private boolean isAttacked(int targetSquare, int attackerColor, int attackerChessman, int[] moveDelta) {
    assert Square.isValid(targetSquare);
    assert IntColor.isValid(attackerColor);
    assert IntChessman.isValid(attackerChessman);
    assert moveDelta != null;

    boolean sliding = IntChessman.isSliding(attackerChessman);

    for (int delta : moveDelta) {
      int attackerSquare = targetSquare + delta;

      while (Square.isLegal(attackerSquare)) {
        int attackerPiece = board[attackerSquare];

        if (IntPiece.isValid(attackerPiece)) {
          if (IntPiece.getChessman(attackerPiece) == attackerChessman
            && IntPiece.getColor(attackerPiece) == attackerColor) {
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
