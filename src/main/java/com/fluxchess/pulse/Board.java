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
  public int enPassant = Position.NOPOSITION;
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
    public int enPassant = Position.NOPOSITION;
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

    // Initialize position lists
    for (int color : IntColor.values) {
      pawns[color] = new ChessmanList();
      knights[color] = new ChessmanList();
      bishops[color] = new ChessmanList();
      rooks[color] = new ChessmanList();
      queens[color] = new ChessmanList();
      kings[color] = new ChessmanList();
    }

    // Initialize board
    for (int position : Position.values) {
      board[position] = IntPiece.NOPIECE;

      GenericPiece genericPiece = genericBoard.getPiece(Position.toGenericPosition(position));
      if (genericPiece != null) {
        int piece = IntPiece.valueOf(genericPiece);
        put(piece, position);
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
      enPassant = Position.valueOf(genericBoard.getEnPassant());
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
    for (int position : Position.values) {
      if (board[position] != IntPiece.NOPIECE) {
        genericBoard.setPiece(IntPiece.toGenericPiece(board[position]), Position.toGenericPosition(position));
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
    if (enPassant != Position.NOPOSITION) {
      genericBoard.setEnPassant(Position.toGenericPosition(enPassant));
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

  private void put(int piece, int position) {
    assert piece != IntPiece.NOPIECE;
    assert (position & 0x88) == 0;
    assert board[position] == IntPiece.NOPIECE;

    int chessman = IntPiece.getChessman(piece);
    int color = IntPiece.getColor(piece);

    switch (chessman) {
      case IntChessman.PAWN:
        pawns[color].add(position);
        break;
      case IntChessman.KNIGHT:
        knights[color].add(position);
        break;
      case IntChessman.BISHOP:
        bishops[color].add(position);
        break;
      case IntChessman.ROOK:
        rooks[color].add(position);
        break;
      case IntChessman.QUEEN:
        queens[color].add(position);
        break;
      case IntChessman.KING:
        kings[color].add(position);
        break;
      default:
        assert false : chessman;
        break;
    }

    board[position] = piece;

    zobristCode ^= zobristPiece[IntPiece.ordinal(piece)][position];
  }

  private int remove(int position) {
    assert (position & 0x88) == 0;
    assert board[position] != IntPiece.NOPIECE;

    int piece = board[position];

    int chessman = IntPiece.getChessman(piece);
    int color = IntPiece.getColor(piece);

    switch (chessman) {
      case IntChessman.PAWN:
        pawns[color].remove(position);
        break;
      case IntChessman.KNIGHT:
        knights[color].remove(position);
        break;
      case IntChessman.BISHOP:
        bishops[color].remove(position);
        break;
      case IntChessman.ROOK:
        rooks[color].remove(position);
        break;
      case IntChessman.QUEEN:
        queens[color].remove(position);
        break;
      case IntChessman.KING:
        kings[color].remove(position);
        break;
      default:
        assert false : chessman;
        break;
    }

    board[position] = IntPiece.NOPIECE;

    zobristCode ^= zobristPiece[IntPiece.ordinal(piece)][position];

    return piece;
  }

  private int move(int originPosition, int targetPosition) {
    assert (originPosition & 0x88) == 0;
    assert (targetPosition & 0x88) == 0;
    assert board[originPosition] != IntPiece.NOPIECE;
    assert board[targetPosition] == IntPiece.NOPIECE;

    int piece = board[originPosition];
    int chessman = IntPiece.getChessman(piece);
    int color = IntPiece.getColor(piece);

    switch (chessman) {
      case IntChessman.PAWN:
        pawns[color].remove(originPosition);
        pawns[color].add(targetPosition);
        break;
      case IntChessman.KNIGHT:
        knights[color].remove(originPosition);
        knights[color].add(targetPosition);
        break;
      case IntChessman.BISHOP:
        bishops[color].remove(originPosition);
        bishops[color].add(targetPosition);
        break;
      case IntChessman.ROOK:
        rooks[color].remove(originPosition);
        rooks[color].add(targetPosition);
        break;
      case IntChessman.QUEEN:
        queens[color].remove(originPosition);
        queens[color].add(targetPosition);
        break;
      case IntChessman.KING:
        kings[color].remove(originPosition);
        kings[color].add(targetPosition);
        break;
      default:
        assert false : chessman;
        break;
    }

    board[originPosition] = IntPiece.NOPIECE;
    board[targetPosition] = piece;

    zobristCode ^= zobristPiece[IntPiece.ordinal(piece)][originPosition];
    zobristCode ^= zobristPiece[IntPiece.ordinal(piece)][targetPosition];

    return piece;
  }

  public void makeMove(int move) {
    assert Move.getOriginPiece(move) == board[Move.getOriginPosition(move)];

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
    int targetPosition = Move.getTargetPosition(move);
    int targetPiece = Move.getTargetPiece(move);
    if (targetPiece != IntPiece.NOPIECE) {
      assert targetPiece == board[targetPosition];
      remove(targetPosition);

      switch (targetPosition) {
        case Position.a1:
          if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.WHITEROOK;
            castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
          }
          break;
        case Position.a8:
          if (castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.BLACKROOK;
            castling[IntColor.BLACK][IntCastling.QUEENSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE];
          }
          break;
        case Position.h1:
          if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.WHITEROOK;
            castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
          }
          break;
        case Position.h8:
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
    int originPosition = Move.getOriginPosition(move);
    move(originPosition, targetPosition);

    // Update castling
    switch (originPosition) {
      case Position.a1:
        if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.WHITEROOK;
          castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
        }
        break;
      case Position.a8:
        if (castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.BLACKROOK;
          castling[IntColor.BLACK][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE];
        }
        break;
      case Position.h1:
        if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.WHITEROOK;
          castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
        }
        break;
      case Position.h8:
        if (castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          assert Move.getOriginPiece(move) == IntPiece.BLACKROOK;
          castling[IntColor.BLACK][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.KINGSIDE];
        }
        break;
      case Position.e1:
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
      case Position.e8:
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
    if (enPassant != Position.NOPOSITION) {
      zobristCode ^= zobristEnPassant[enPassant];
      enPassant = Position.NOPOSITION;
    }

    // Save and update half move clock
    entry.halfMoveClock = halfMoveClock;
    int originChessman = IntPiece.getChessman(Move.getOriginPiece(move));
    if (originChessman == IntChessman.PAWN || targetPiece != IntPiece.NOPIECE) {
      halfMoveClock = 0;
    } else {
      ++halfMoveClock;
    }
  }

  private void undoMoveNormal(int move, StackEntry entry) {
    // Restore half move clock
    halfMoveClock = entry.halfMoveClock;

    // Restore en passant
    if (entry.enPassant != Position.NOPOSITION) {
      enPassant = entry.enPassant;
      zobristCode ^= zobristEnPassant[enPassant];
    }

    // Move piece
    int originPosition = Move.getOriginPosition(move);
    int targetPosition = Move.getTargetPosition(move);
    assert Move.getOriginPiece(move) == board[targetPosition];
    move(targetPosition, originPosition);

    // Restore target piece
    int targetPiece = Move.getTargetPiece(move);
    if (targetPiece != IntPiece.NOPIECE) {
      put(targetPiece, targetPosition);
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
    int originPosition = Move.getOriginPosition(move);
    int targetPosition = Move.getTargetPosition(move);
    int originColor = IntPiece.getColor(Move.getOriginPiece(move));
    assert IntPiece.getChessman(Move.getOriginPiece(move)) == IntChessman.PAWN;
    assert (originPosition >>> 4 == IntRank.R2 && originColor == IntColor.WHITE) || (originPosition >>> 4 == IntRank.R7 && originColor == IntColor.BLACK);
    assert (targetPosition >>> 4 == IntRank.R4 && originColor == IntColor.WHITE) || (targetPosition >>> 4 == IntRank.R5 && originColor == IntColor.BLACK);
    assert Math.abs(originPosition - targetPosition) == 32;
    move(originPosition, targetPosition);

    // Save and calculate en passant position
    entry.enPassant = enPassant;
    if (enPassant != Position.NOPOSITION) {
      zobristCode ^= zobristEnPassant[enPassant];
    }
    if (originColor == IntColor.WHITE) {
      enPassant = targetPosition - 16;
    } else {
      enPassant = targetPosition + 16;
    }
    assert (enPassant & 0x88) == 0;
    assert Math.abs(originPosition - enPassant) == 16;
    zobristCode ^= zobristEnPassant[enPassant];

    // Save and update half move clock
    entry.halfMoveClock = halfMoveClock;
    halfMoveClock = 0;
  }

  private void undoMovePawnDouble(int move, StackEntry entry) {
    // Restore half move clock
    halfMoveClock = entry.halfMoveClock;

    // Restore en passant
    assert enPassant != Position.NOPOSITION;
    zobristCode ^= zobristEnPassant[enPassant];
    enPassant = entry.enPassant;
    if (entry.enPassant != Position.NOPOSITION) {
      zobristCode ^= zobristEnPassant[enPassant];
    }

    // Move pawn
    move(Move.getTargetPosition(move), Move.getOriginPosition(move));
  }

  private void makeMovePawnPromotion(int move, StackEntry entry) {
    // Save castling rights
    for (int color : IntColor.values) {
      for (int castling : IntCastling.values) {
        entry.castling[color][castling] = this.castling[color][castling];
      }
    }

    // Remove target piece and adjust castling rights.
    int targetPosition = Move.getTargetPosition(move);
    int targetPiece = Move.getTargetPiece(move);
    if (targetPiece != IntPiece.NOPIECE) {
      assert targetPiece == board[targetPosition];
      remove(targetPosition);

      switch (targetPosition) {
        case Position.a1:
          if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.WHITEROOK;
            castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
          }
          break;
        case Position.a8:
          if (castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.BLACKROOK;
            castling[IntColor.BLACK][IntCastling.QUEENSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE];
          }
          break;
        case Position.h1:
          if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
            assert targetPiece == IntPiece.WHITEROOK;
            castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
            zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
          }
          break;
        case Position.h8:
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

    // Remove pawn at the origin position
    int originPosition = Move.getOriginPosition(move);
    int originColor = IntPiece.getColor(Move.getOriginPiece(move));
    assert IntPiece.getChessman(Move.getOriginPiece(move)) == IntChessman.PAWN;
    assert (originPosition >>> 4 == IntRank.R7 && originColor == IntColor.WHITE) || (originPosition >>> 4 == IntRank.R2 && originColor == IntColor.BLACK);
    remove(originPosition);

    // Create promotion chessman
    int promotion = Move.getPromotion(move);
    assert promotion != IntChessman.NOCHESSMAN;
    int promotionPiece = IntPiece.valueOf(promotion, originColor);
    assert (targetPosition >>> 4 == IntRank.R8 && originColor == IntColor.WHITE) || (targetPosition >>> 4 == IntRank.R1 && originColor == IntColor.BLACK);
    put(promotionPiece, targetPosition);

    // Save and update en passant
    entry.enPassant = enPassant;
    if (enPassant != Position.NOPOSITION) {
      zobristCode ^= zobristEnPassant[enPassant];
      enPassant = Position.NOPOSITION;
    }

    // Save and update half move clock
    entry.halfMoveClock = halfMoveClock;
    halfMoveClock = 0;
  }

  private void undoMovePawnPromotion(int move, StackEntry entry) {
    // Restore half move clock
    halfMoveClock = entry.halfMoveClock;

    // Restore en passant
    if (entry.enPassant != Position.NOPOSITION) {
      enPassant = entry.enPassant;
      zobristCode ^= zobristEnPassant[enPassant];
    }

    // Remove promotion chessman at the target position
    int targetPosition = Move.getTargetPosition(move);
    remove(targetPosition);

    // Put pawn at the origin position
    int originPiece = Move.getOriginPiece(move);
    put(originPiece, Move.getOriginPosition(move));

    // Restore target piece
    int targetPiece = Move.getTargetPiece(move);
    if (targetPiece != IntPiece.NOPIECE) {
      put(targetPiece, targetPosition);
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

  private void makeMoveEnPassant(int move, StackEntry entry) {
    // Remove target pawn
    int targetPosition = Move.getTargetPosition(move);
    int originColor = IntPiece.getColor(Move.getOriginPiece(move));
    int capturePosition;
    if (originColor == IntColor.WHITE) {
      capturePosition = targetPosition - 16;
    } else {
      capturePosition = targetPosition + 16;
    }
    assert Move.getTargetPiece(move) == board[capturePosition];
    assert IntPiece.getChessman(Move.getTargetPiece(move)) == IntChessman.PAWN;
    assert IntPiece.getColor(Move.getTargetPiece(move)) == IntColor.opposite(originColor);
    remove(capturePosition);

    // Move pawn
    assert IntPiece.getChessman(Move.getOriginPiece(move)) == IntChessman.PAWN;
    assert targetPosition == enPassant;
    move(Move.getOriginPosition(move), targetPosition);

    // Save and update en passant
    entry.enPassant = enPassant;
    zobristCode ^= zobristEnPassant[enPassant];
    enPassant = Position.NOPOSITION;

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
    int targetPosition = Move.getTargetPosition(move);
    move(targetPosition, Move.getOriginPosition(move));

    // Restore target pawn
    int capturePosition;
    if (IntPiece.getColor(Move.getOriginPiece(move)) == IntColor.WHITE) {
      capturePosition = targetPosition - 16;
    } else {
      capturePosition = targetPosition + 16;
    }
    put(Move.getTargetPiece(move), capturePosition);
  }

  private void makeMoveCastling(int move, StackEntry entry) {
    // Save castling rights
    for (int color : IntColor.values) {
      for (int castling : IntCastling.values) {
        entry.castling[color][castling] = this.castling[color][castling];
      }
    }

    // Move king
    int kingTargetPosition = Move.getTargetPosition(move);
    assert IntPiece.getChessman(Move.getOriginPiece(move)) == IntChessman.KING;
    move(Move.getOriginPosition(move), kingTargetPosition);

    // Get rook positions and update castling rights
    int rookOriginPosition = Position.NOPOSITION;
    int rookTargetPosition = Position.NOPOSITION;
    switch (kingTargetPosition) {
      case Position.g1:
        rookOriginPosition = Position.h1;
        rookTargetPosition = Position.f1;
        if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
        }
        if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
        }
        break;
      case Position.c1:
        rookOriginPosition = Position.a1;
        rookTargetPosition = Position.d1;
        if (castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          castling[IntColor.WHITE][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.QUEENSIDE];
        }
        if (castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          castling[IntColor.WHITE][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.WHITE][IntCastling.KINGSIDE];
        }
        break;
      case Position.g8:
        rookOriginPosition = Position.h8;
        rookTargetPosition = Position.f8;
        if (castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE) {
          castling[IntColor.BLACK][IntCastling.QUEENSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.QUEENSIDE];
        }
        if (castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE) {
          castling[IntColor.BLACK][IntCastling.KINGSIDE] = IntFile.NOFILE;
          zobristCode ^= zobristCastling[IntColor.BLACK][IntCastling.KINGSIDE];
        }
        break;
      case Position.c8:
        rookOriginPosition = Position.a8;
        rookTargetPosition = Position.d8;
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
        assert false : kingTargetPosition;
        break;
    }

    // Move rook
    int rook = move(rookOriginPosition, rookTargetPosition);
    assert IntPiece.getChessman(rook) == IntChessman.ROOK;

    // Save and update en passant
    entry.enPassant = enPassant;
    if (enPassant != Position.NOPOSITION) {
      zobristCode ^= zobristEnPassant[enPassant];
      enPassant = Position.NOPOSITION;
    }

    // Save and update half move clock
    entry.halfMoveClock = halfMoveClock;
    ++halfMoveClock;
  }

  private void undoMoveCastling(int move, StackEntry entry) {
    // Restore half move clock
    halfMoveClock = entry.halfMoveClock;

    // Restore en passant
    if (entry.enPassant != Position.NOPOSITION) {
      enPassant = entry.enPassant;
      zobristCode ^= zobristEnPassant[enPassant];
    }

    int targetPosition = Move.getTargetPosition(move);

    // Get rook positions
    int rookFrom = Position.NOPOSITION;
    int rookTo = Position.NOPOSITION;
    switch (targetPosition) {
      case Position.g1:
        rookFrom = Position.h1;
        rookTo = Position.f1;
        break;
      case Position.c1:
        rookFrom = Position.a1;
        rookTo = Position.d1;
        break;
      case Position.g8:
        rookFrom = Position.h8;
        rookTo = Position.f8;
        break;
      case Position.c8:
        rookFrom = Position.a8;
        rookTo = Position.d8;
        break;
      default:
        assert false : targetPosition;
        break;
    }

    // Move rook
    move(rookTo, rookFrom);

    // Move king
    move(targetPosition, Move.getOriginPosition(move));

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
