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

  // We will save some board parameters in a StackEntry before making a move.
  // Later we will restore them before undoing a move.
  private final StackEntry[] stack = new StackEntry[MAX_GAMEMOVES];
  private int stackSize = 0;

  private static final class StackEntry {
    public final int[][] castling = new int[IntColor.values.length][IntCastling.values.length];
    public int enPassant = Square.NOSQUARE;
    public int halfMoveClock = 0;

    public StackEntry() {
      for (int color : IntColor.values) {
        for (int castling : IntCastling.values) {
          this.castling[color][castling] = IntFile.NOFILE;
        }
      }
    }
  }

  // Initialize the zobrist keys
  static {
    Random random = new Random(0);

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
      stack[i] = new StackEntry();
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
    assert piece != IntPiece.NOPIECE;
    assert (square & 0x88) == 0;
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

  private void remove(int square) {
    assert (square & 0x88) == 0;
    assert board[square] != IntPiece.NOPIECE;

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
  }

  private void move(int originSquare, int targetSquare) {
    assert (originSquare & 0x88) == 0;
    assert (targetSquare & 0x88) == 0;
    assert board[originSquare] != IntPiece.NOPIECE;
    assert board[targetSquare] == IntPiece.NOPIECE;

    int piece = board[originSquare];
    int chessman = IntPiece.getChessman(piece);
    int color = IntPiece.getColor(piece);

    switch (chessman) {
      case IntChessman.PAWN:
        pawns[color].remove(originSquare);
        pawns[color].add(targetSquare);
        break;
      case IntChessman.KNIGHT:
        knights[color].remove(originSquare);
        knights[color].add(targetSquare);
        break;
      case IntChessman.BISHOP:
        bishops[color].remove(originSquare);
        bishops[color].add(targetSquare);
        break;
      case IntChessman.ROOK:
        rooks[color].remove(originSquare);
        rooks[color].add(targetSquare);
        break;
      case IntChessman.QUEEN:
        queens[color].remove(originSquare);
        queens[color].add(targetSquare);
        break;
      case IntChessman.KING:
        kings[color].remove(originSquare);
        kings[color].add(targetSquare);
        break;
      default:
        assert false : chessman;
        break;
    }

    board[originSquare] = IntPiece.NOPIECE;
    board[targetSquare] = piece;

    zobristCode ^= zobristPiece[IntPiece.ordinal(piece)][originSquare];
    zobristCode ^= zobristPiece[IntPiece.ordinal(piece)][targetSquare];
  }

  public void makeMove(int move) {
    assert Move.getOriginPiece(move) == board[Move.getOriginSquare(move)];

    repetitionTable.add(zobristCode);

    StackEntry entry = stack[stackSize];

    int type = Move.getType(move);
    switch (type) {
      case Move.Type.NORMAL:
        makeMoveNormal(move, entry);
        break;
      case Move.Type.PAWNDOUBLE:
        makeMovePawnDouble(move, entry);
        break;
      case Move.Type.PAWNPROMOTION:
        makeMovePawnPromotion(move, entry);
        break;
      case Move.Type.ENPASSANT:
        makeMoveEnPassant(move, entry);
        break;
      case Move.Type.CASTLING:
        makeMoveCastling(move, entry);
        break;
      default:
        assert false : type;
        break;
    }

    activeColor = IntColor.opposite(activeColor);
    zobristCode ^= zobristActiveColor;

    ++halfMoveNumber;

    ++stackSize;
    assert stackSize < MAX_GAMEMOVES;
  }

  public void undoMove(int move) {
    --stackSize;
    StackEntry entry = stack[stackSize];

    --halfMoveNumber;

    activeColor = IntColor.opposite(activeColor);
    zobristCode ^= zobristActiveColor;

    int type = Move.getType(move);
    switch (type) {
      case Move.Type.NORMAL:
        undoMoveNormal(move, entry);
        break;
      case Move.Type.PAWNDOUBLE:
        undoMovePawnDouble(move, entry);
        break;
      case Move.Type.PAWNPROMOTION:
        undoMovePawnPromotion(move, entry);
        break;
      case Move.Type.ENPASSANT:
        undoMoveEnPassant(move, entry);
        break;
      case Move.Type.CASTLING:
        undoMoveCastling(move, entry);
        break;
      default:
        assert false : type;
        break;
    }

    repetitionTable.remove(zobristCode);
  }

  private void makeMoveNormal(int move, StackEntry entry) {
    // Save castling rights
    for (int color : IntColor.values) {
      for (int castling : IntCastling.values) {
        entry.castling[color][castling] = this.castling[color][castling];
      }
    }

    // Remove target piece and adjust castling rights.
    int targetSquare = Move.getTargetSquare(move);
    int targetPiece = Move.getTargetPiece(move);
    if (targetPiece != IntPiece.NOPIECE) {
      assert targetPiece == board[targetSquare];
      remove(targetSquare);

      switch (targetSquare) {
        case Square.a1:
          if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.WHITEROOK;
            castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
          }
          break;
        case Square.a8:
          if (castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.BLACKROOK;
            castling[IntColor.BLACK][IntCastling.QUEENSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE];
          }
          break;
        case Square.h1:
          if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.WHITEROOK;
            castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
          }
          break;
        case Square.h8:
          if (castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.BLACKROOK;
            castling[IntColor.BLACK][IntCastling.KINGSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.KINGSIDE];
          }
          break;
        default:
          break;
      }
    }

    // Move piece
    int originSquare = Move.getOriginSquare(move);
    move(originSquare, targetSquare);

    // Update castling
    switch (originSquare) {
      case Square.a1:
        if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.WHITEROOK;
          castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
        }
        break;
      case Square.a8:
        if (castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.BLACKROOK;
          castling[IntColor.BLACK][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE];
        }
        break;
      case Square.h1:
        if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.WHITEROOK;
          castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
        }
        break;
      case Square.h8:
        if (castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.BLACKROOK;
          castling[IntColor.BLACK][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.KINGSIDE];
        }
        break;
      case Square.e1:
        if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.WHITEKING;
          castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
        }
        if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.WHITEKING;
          castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
        }
        break;
      case Square.e8:
        if (castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.BLACKKING;
          castling[IntColor.BLACK][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE];
        }
        if (castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.BLACKKING;
          castling[IntColor.BLACK][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.KINGSIDE];
        }
        break;
      default:
        break;
    }

    // Save and update en passant
    entry.enPassant = enPassant;
    if (enPassant != Square.NOSQUARE) {
      zobristCode ^= zobristEnPassant[enPassant];
      enPassant = Square.NOSQUARE;
    }

    // Save and update half move clock
    entry.halfMoveClock = halfMoveClock;
    if (IntPiece.getChessman(Move.getOriginPiece(move)) == IntChessman.PAWN || targetPiece != IntPiece.NOPIECE) {
      halfMoveClock = 0;
    } else {
      ++halfMoveClock;
    }
  }

  private void undoMoveNormal(int move, StackEntry entry) {
    // Restore half move clock
    halfMoveClock = entry.halfMoveClock;

    // Restore en passant
    if (entry.enPassant != Square.NOSQUARE) {
      enPassant = entry.enPassant;
      zobristCode ^= zobristEnPassant[enPassant];
    }

    // Move piece
    int originSquare = Move.getOriginSquare(move);
    int targetSquare = Move.getTargetSquare(move);
    assert Move.getOriginPiece(move) == board[targetSquare];
    move(targetSquare, originSquare);

    // Restore target piece
    int targetPiece = Move.getTargetPiece(move);
    if (targetPiece != IntPiece.NOPIECE) {
      put(targetPiece, targetSquare);
    }

    // Restore castling rights
    for (int color : IntColor.values) {
      for (int castling : IntCastling.values) {
        if (entry.castling[color][castling] != this.castling[color][castling]) {
          this.castling[color][castling] = entry.castling[color][castling];
          zobristCode ^= zobristCastling[color][castling];
        }
      }
    }
  }

  private void makeMovePawnDouble(int move, StackEntry entry) {
    // Move pawn
    int originSquare = Move.getOriginSquare(move);
    int targetSquare = Move.getTargetSquare(move);
    int originColor = IntPiece.getColor(Move.getOriginPiece(move));
    assert IntPiece.getChessman(Move.getOriginPiece(move)) == IntChessman.PAWN;
    assert (originSquare >>> 4 == IntRank.R2 && originColor == IntColor.WHITE) || (originSquare >>> 4 == IntRank.R7 && originColor == IntColor.BLACK);
    assert (targetSquare >>> 4 == IntRank.R4 && originColor == IntColor.WHITE) || (targetSquare >>> 4 == IntRank.R5 && originColor == IntColor.BLACK);
    assert Math.abs(originSquare - targetSquare) == 32;
    move(originSquare, targetSquare);

    // Save and calculate en passant square
    entry.enPassant = enPassant;
    if (enPassant != Square.NOSQUARE) {
      zobristCode ^= zobristEnPassant[enPassant];
    }
    if (originColor == IntColor.WHITE) {
      enPassant = targetSquare - 16;
    } else {
      enPassant = targetSquare + 16;
    }
    assert (enPassant & 0x88) == 0;
    assert Math.abs(originSquare - enPassant) == 16;
    zobristCode ^= zobristEnPassant[enPassant];

    // Save and update half move clock
    entry.halfMoveClock = halfMoveClock;
    halfMoveClock = 0;
  }

  private void undoMovePawnDouble(int move, StackEntry entry) {
    // Restore half move clock
    halfMoveClock = entry.halfMoveClock;

    // Restore en passant
    assert enPassant != Square.NOSQUARE;
    zobristCode ^= zobristEnPassant[enPassant];
    enPassant = entry.enPassant;
    if (entry.enPassant != Square.NOSQUARE) {
      zobristCode ^= zobristEnPassant[enPassant];
    }

    // Move pawn
    move(Move.getTargetSquare(move), Move.getOriginSquare(move));
  }

  private void makeMovePawnPromotion(int move, StackEntry entry) {
    // Remove target piece and adjust castling rights.
    int targetSquare = Move.getTargetSquare(move);
    int targetPiece = Move.getTargetPiece(move);
    if (targetPiece != IntPiece.NOPIECE) {
      // Save castling rights
      for (int color : IntColor.values) {
        for (int castling : IntCastling.values) {
          entry.castling[color][castling] = this.castling[color][castling];
        }
      }

      assert targetPiece == board[targetSquare];
      remove(targetSquare);

      switch (targetSquare) {
        case Square.a1:
          if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.WHITEROOK;
            castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
          }
          break;
        case Square.a8:
          if (castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.BLACKROOK;
            castling[IntColor.BLACK][IntCastling.QUEENSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE];
          }
          break;
        case Square.h1:
          if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.WHITEROOK;
            castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
          }
          break;
        case Square.h8:
          if (castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.BLACKROOK;
            castling[IntColor.BLACK][IntCastling.KINGSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.KINGSIDE];
          }
          break;
        default:
          break;
      }
    }

    // Remove pawn at the origin square
    int originSquare = Move.getOriginSquare(move);
    int originColor = IntPiece.getColor(Move.getOriginPiece(move));
    assert IntPiece.getChessman(Move.getOriginPiece(move)) == IntChessman.PAWN;
    assert (originSquare >>> 4 == IntRank.R7 && originColor == IntColor.WHITE) || (originSquare >>> 4 == IntRank.R2 && originColor == IntColor.BLACK);
    remove(originSquare);

    // Create promotion chessman
    int promotion = Move.getPromotion(move);
    assert promotion != IntChessman.NOCHESSMAN;
    int promotionPiece = IntPiece.valueOf(promotion, originColor);
    assert (targetSquare >>> 4 == IntRank.R8 && originColor == IntColor.WHITE) || (targetSquare >>> 4 == IntRank.R1 && originColor == IntColor.BLACK);
    put(promotionPiece, targetSquare);

    // Save and update en passant
    entry.enPassant = enPassant;
    if (enPassant != Square.NOSQUARE) {
      zobristCode ^= zobristEnPassant[enPassant];
      enPassant = Square.NOSQUARE;
    }

    // Save and update half move clock
    entry.halfMoveClock = halfMoveClock;
    halfMoveClock = 0;
  }

  private void undoMovePawnPromotion(int move, StackEntry entry) {
    // Restore half move clock
    halfMoveClock = entry.halfMoveClock;

    // Restore en passant
    if (entry.enPassant != Square.NOSQUARE) {
      enPassant = entry.enPassant;
      zobristCode ^= zobristEnPassant[enPassant];
    }

    // Remove promotion chessman at the target square
    int targetSquare = Move.getTargetSquare(move);
    remove(targetSquare);

    // Restore target piece
    int targetPiece = Move.getTargetPiece(move);
    if (targetPiece != IntPiece.NOPIECE) {
      put(targetPiece, targetSquare);

      // Restore castling rights
      for (int color : IntColor.values) {
        for (int castling : IntCastling.values) {
          if (entry.castling[color][castling] != this.castling[color][castling]) {
            this.castling[color][castling] = entry.castling[color][castling];
            zobristCode ^= zobristCastling[color][castling];
          }
        }
      }
    }

    // Put pawn at the origin square
    put(Move.getOriginPiece(move), Move.getOriginSquare(move));
  }

  private void makeMoveEnPassant(int move, StackEntry entry) {
    // Remove target pawn
    int targetSquare = Move.getTargetSquare(move);
    int originColor = IntPiece.getColor(Move.getOriginPiece(move));
    int captureSquare;
    if (originColor == IntColor.WHITE) {
      captureSquare = targetSquare - 16;
    } else {
      captureSquare = targetSquare + 16;
    }
    assert Move.getTargetPiece(move) == board[captureSquare];
    assert IntPiece.getChessman(Move.getTargetPiece(move)) == IntChessman.PAWN;
    assert IntPiece.getColor(Move.getTargetPiece(move)) == IntColor.opposite(originColor);
    remove(captureSquare);

    // Move pawn
    assert IntPiece.getChessman(Move.getOriginPiece(move)) == IntChessman.PAWN;
    assert targetSquare == enPassant;
    move(Move.getOriginSquare(move), targetSquare);

    // Save and update en passant
    entry.enPassant = enPassant;
    zobristCode ^= zobristEnPassant[enPassant];
    enPassant = Square.NOSQUARE;

    // Update half move clock
    entry.halfMoveClock = halfMoveClock;
    halfMoveClock = 0;
  }

  private void undoMoveEnPassant(int move, StackEntry entry) {
    // Restore half move clock
    halfMoveClock = entry.halfMoveClock;

    // Restore en passant
    enPassant = entry.enPassant;
    zobristCode ^= zobristEnPassant[enPassant];

    // Move pawn
    int targetSquare = Move.getTargetSquare(move);
    move(targetSquare, Move.getOriginSquare(move));

    // Restore target pawn
    int captureSquare;
    if (IntPiece.getColor(Move.getOriginPiece(move)) == IntColor.WHITE) {
      captureSquare = targetSquare - 16;
    } else {
      captureSquare = targetSquare + 16;
    }
    put(Move.getTargetPiece(move), captureSquare);
  }

  private void makeMoveCastling(int move, StackEntry entry) {
    // Save castling rights
    for (int color : IntColor.values) {
      for (int castling : IntCastling.values) {
        entry.castling[color][castling] = this.castling[color][castling];
      }
    }

    // Move king
    int kingTargetSquare = Move.getTargetSquare(move);
    assert IntPiece.getChessman(Move.getOriginPiece(move)) == IntChessman.KING;
    move(Move.getOriginSquare(move), kingTargetSquare);

    // Get rook squares and update castling rights
    int rookOriginSquare = Square.NOSQUARE;
    int rookTargetSquare = Square.NOSQUARE;
    switch (kingTargetSquare) {
      case Square.g1:
        rookOriginSquare = Square.h1;
        rookTargetSquare = Square.f1;
        if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
        }
        if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
        }
        break;
      case Square.c1:
        rookOriginSquare = Square.a1;
        rookTargetSquare = Square.d1;
        if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
        }
        if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
        }
        break;
      case Square.g8:
        rookOriginSquare = Square.h8;
        rookTargetSquare = Square.f8;
        if (castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          castling[IntColor.BLACK][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE];
        }
        if (castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          castling[IntColor.BLACK][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.KINGSIDE];
        }
        break;
      case Square.c8:
        rookOriginSquare = Square.a8;
        rookTargetSquare = Square.d8;
        if (castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          castling[IntColor.BLACK][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE];
        }
        if (castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          castling[IntColor.BLACK][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.KINGSIDE];
        }
        break;
      default:
        assert false : kingTargetSquare;
        break;
    }

    // Move rook
    assert IntPiece.getChessman(board[rookOriginSquare]) == IntChessman.ROOK;
    move(rookOriginSquare, rookTargetSquare);

    // Save and update en passant
    entry.enPassant = enPassant;
    if (enPassant != Square.NOSQUARE) {
      zobristCode ^= zobristEnPassant[enPassant];
      enPassant = Square.NOSQUARE;
    }

    // Save and update half move clock
    entry.halfMoveClock = halfMoveClock;
    ++halfMoveClock;
  }

  private void undoMoveCastling(int move, StackEntry entry) {
    // Restore half move clock
    halfMoveClock = entry.halfMoveClock;

    // Restore en passant
    if (entry.enPassant != Square.NOSQUARE) {
      enPassant = entry.enPassant;
      zobristCode ^= zobristEnPassant[enPassant];
    }

    int kingTargetSquare = Move.getTargetSquare(move);

    // Get rook squares
    int rookOriginSquare = Square.NOSQUARE;
    int rookTargetSquare = Square.NOSQUARE;
    switch (kingTargetSquare) {
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
        assert false : kingTargetSquare;
        break;
    }

    // Move rook
    move(rookTargetSquare, rookOriginSquare);

    // Move king
    move(kingTargetSquare, Move.getOriginSquare(move));

    // Restore the castling rights
    for (int color : IntColor.values) {
      for (int castling : IntCastling.values) {
        if (entry.castling[color][castling] != this.castling[color][castling]) {
          this.castling[color][castling] = entry.castling[color][castling];
          zobristCode ^= zobristCastling[color][castling];
        }
      }
    }
  }

}
