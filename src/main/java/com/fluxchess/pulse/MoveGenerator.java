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

public final class MoveGenerator {

  // Move deltas
  private static final int[] moveDeltaPawn = {16, 17, 15};
  private static final int[] moveDeltaKnight = {+33, +18, -14, -31, -33, -18, +14, +31};
  private static final int[] moveDeltaBishop = {+17, -15, -17, +15};
  private static final int[] moveDeltaRook = {+16, +1, -16, -1};
  private static final int[] moveDeltaQueen = {+16, +17, +1, -15, -16, -17, -1, +15};
  private static final int[] moveDeltaKing = {+16, +17, +1, -15, -16, -17, -1, +15};

  // Board
  private final Board board;

  public MoveGenerator(Board board) {
    assert board != null;

    this.board = board;
  }

  public boolean isCheck() {
    // Check whether our king is attacked by any opponent piece
    return isAttacked(ChessmanList.next(board.kings[board.activeColor].squares), IntColor.opposite(board.activeColor));
  }

  public MoveList getAll() {
    MoveList moveList = new MoveList();
    MoveList tempList = new MoveList();

    int activeColor = board.activeColor;

    for (long squares = board.pawns[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addPawnMoves(tempList, board.board[square], square);
    }
    for (long squares = board.knights[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(tempList, board.board[square], square, moveDeltaKnight);
    }
    for (long squares = board.bishops[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(tempList, board.board[square], square, moveDeltaBishop);
    }
    for (long squares = board.rooks[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(tempList, board.board[square], square, moveDeltaRook);
    }
    for (long squares = board.queens[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(tempList, board.board[square], square, moveDeltaQueen);
    }
    int square = ChessmanList.next(board.kings[activeColor].squares);
    addMoves(tempList, board.board[square], square, moveDeltaKing);

    if (!isAttacked(ChessmanList.next(board.kings[activeColor].squares), IntColor.opposite(activeColor))) {
      addCastlingMoves(tempList, board.board[square], square);
    }

    for (int i = 0; i < tempList.size; ++i) {
      int move = tempList.moves[i];
      if (isLegal(move)) {
        moveList.moves[moveList.size++] = move;
      }
    }

    return moveList;
  }

  private void addMoves(MoveList list, int originPiece, int originSquare, int[] moveDelta) {
    assert list != null;
    assert IntPiece.isValid(originPiece);
    assert Square.isValid(originSquare);
    assert board.board[originSquare] == originPiece;
    assert moveDelta != null;

    boolean sliding = IntChessman.isSliding(IntPiece.getChessman(originPiece));
    int oppositeColor = IntColor.opposite(IntPiece.getColor(originPiece));

    for (int delta : moveDelta) {
      int targetSquare = originSquare + delta;

      while (Square.isLegal(targetSquare)) {
        int targetPiece = board.board[targetSquare];

        if (targetPiece == IntPiece.NOPIECE) {
          int move = Move.valueOf(Move.Type.NORMAL, originSquare, targetSquare, originPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
          list.moves[list.size++] = move;

          if (!sliding) {
            break;
          }

          targetSquare += delta;
        } else {
          if (IntPiece.getColor(targetPiece) == oppositeColor
            && IntPiece.getChessman(targetPiece) != IntChessman.KING) {
            int move = Move.valueOf(Move.Type.NORMAL, originSquare, targetSquare, originPiece, targetPiece, IntChessman.NOCHESSMAN);
            list.moves[list.size++] = move;
          }
          break;
        }
      }
    }
  }

  private void addPawnMoves(MoveList list, int pawnPiece, int pawnSquare) {
    assert list != null;
    assert IntPiece.isValid(pawnPiece);
    assert IntPiece.getChessman(pawnPiece) == IntChessman.PAWN;
    assert Square.isValid(pawnSquare);
    assert board.board[pawnSquare] == pawnPiece;

    int pawnColor = IntPiece.getColor(pawnPiece);

    // Generate only capturing moves first (i = 1)
    for (int i = 1; i < moveDeltaPawn.length; ++i) {
      int delta = moveDeltaPawn[i];
      if (pawnColor == IntColor.BLACK) {
        delta *= -1;
      }

      int targetSquare = pawnSquare + delta;
      if (Square.isLegal(targetSquare)) {
        int targetPiece = board.board[targetSquare];

        if (targetPiece != IntPiece.NOPIECE) {
          if (IntColor.opposite(IntPiece.getColor(targetPiece)) == pawnColor
            && IntPiece.getChessman(targetPiece) != IntChessman.KING) {
            // Capturing move

            if ((pawnColor == IntColor.WHITE && Square.getRank(targetSquare) == IntRank.R8)
              || (pawnColor == IntColor.BLACK && Square.getRank(targetSquare) == IntRank.R1)) {
              // Pawn promotion capturing move

              int move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.QUEEN);
              list.moves[list.size++] = move;
              move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.ROOK);
              list.moves[list.size++] = move;
              move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.BISHOP);
              list.moves[list.size++] = move;
              move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.KNIGHT);
              list.moves[list.size++] = move;
            } else {
              // Normal capturing move

              int move = Move.valueOf(Move.Type.NORMAL, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.NOCHESSMAN);
              list.moves[list.size++] = move;
            }
          }
        } else if (targetSquare == board.enPassant) {
          // En passant move
          assert (pawnColor == IntColor.BLACK && Square.getRank(targetSquare) == IntRank.R3)
            || (pawnColor == IntColor.WHITE && Square.getRank(targetSquare) == IntRank.R6);

          int captureSquare;
          if (pawnColor == IntColor.WHITE) {
            captureSquare = targetSquare - 16;
          } else {
            captureSquare = targetSquare + 16;
          }
          targetPiece = board.board[captureSquare];
          assert IntPiece.getChessman(targetPiece) == IntChessman.PAWN;
          assert IntPiece.getColor(targetPiece) == IntColor.opposite(pawnColor);

          int move = Move.valueOf(Move.Type.ENPASSANT, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.NOCHESSMAN);
          list.moves[list.size++] = move;
        }
      }
    }

    // Generate non-capturing moves
    int delta = moveDeltaPawn[0];
    if (pawnColor == IntColor.BLACK) {
      delta *= -1;
    }

    // Move one rank forward
    int targetSquare = pawnSquare + delta;
    if (Square.isLegal(targetSquare) && board.board[targetSquare] == IntPiece.NOPIECE) {
      if ((pawnColor == IntColor.WHITE && Square.getRank(targetSquare) == IntRank.R8)
        || (pawnColor == IntColor.BLACK && Square.getRank(targetSquare) == IntRank.R1)) {
        // Pawn promotion move

        int move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.QUEEN);
        list.moves[list.size++] = move;
        move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.ROOK);
        list.moves[list.size++] = move;
        move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.BISHOP);
        list.moves[list.size++] = move;
        move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.KNIGHT);
        list.moves[list.size++] = move;
      } else {
        // Normal move

        int move = Move.valueOf(Move.Type.NORMAL, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        list.moves[list.size++] = move;

        // Move another rank forward
        targetSquare += delta;
        if (Square.isLegal(targetSquare) && board.board[targetSquare] == IntPiece.NOPIECE) {
          if ((pawnColor == IntColor.WHITE && Square.getRank(targetSquare) == IntRank.R4)
            || (pawnColor == IntColor.BLACK && Square.getRank(targetSquare) == IntRank.R5)) {
            // Pawn double move

            move = Move.valueOf(Move.Type.PAWNDOUBLE, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
            list.moves[list.size++] = move;
          }
        }
      }
    }
  }

  private void addCastlingMoves(MoveList list, int kingPiece, int kingSquare) {
    assert list != null;
    assert IntPiece.isValid(kingPiece);
    assert IntPiece.getChessman(kingPiece) == IntChessman.KING;
    assert Square.isValid(kingSquare);
    assert board.board[kingSquare] == kingPiece;

    if (IntPiece.getColor(kingPiece) == IntColor.WHITE) {
      // Do not test g1 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE
        && board.board[Square.f1] == IntPiece.NOPIECE
        && board.board[Square.g1] == IntPiece.NOPIECE
        && !isAttacked(Square.f1, IntColor.BLACK)) {
        assert board.board[Square.e1] == IntPiece.WHITEKING;
        assert board.board[Square.h1] == IntPiece.WHITEROOK;

        int move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.g1, kingPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        list.moves[list.size++] = move;
      }
      // Do not test c1 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE
        && board.board[Square.b1] == IntPiece.NOPIECE
        && board.board[Square.c1] == IntPiece.NOPIECE
        && board.board[Square.d1] == IntPiece.NOPIECE
        && !isAttacked(Square.d1, IntColor.BLACK)) {
        assert board.board[Square.e1] == IntPiece.WHITEKING;
        assert board.board[Square.a1] == IntPiece.WHITEROOK;

        int move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.c1, kingPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        list.moves[list.size++] = move;
      }
    } else {
      // Do not test g8 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE
        && board.board[Square.f8] == IntPiece.NOPIECE
        && board.board[Square.g8] == IntPiece.NOPIECE
        && !isAttacked(Square.f8, IntColor.WHITE)) {
        assert board.board[Square.e8] == IntPiece.BLACKKING;
        assert board.board[Square.h8] == IntPiece.BLACKROOK;

        int move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.g8, kingPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        list.moves[list.size++] = move;
      }
      // Do not test c8 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE
        && board.board[Square.b8] == IntPiece.NOPIECE
        && board.board[Square.c8] == IntPiece.NOPIECE
        && board.board[Square.d8] == IntPiece.NOPIECE
        && !isAttacked(Square.d8, IntColor.WHITE)) {
        assert board.board[Square.e8] == IntPiece.BLACKKING;
        assert board.board[Square.a8] == IntPiece.BLACKROOK;

        int move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.c8, kingPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        list.moves[list.size++] = move;
      }
    }
  }

  private boolean isLegal(int move) {
    int activeColor = board.activeColor;

    board.makeMove(move);
    boolean isCheck = isAttacked(ChessmanList.next(board.kings[activeColor].squares), IntColor.opposite(activeColor));
    board.undoMove(move);

    return !isCheck;
  }

  private boolean isAttacked(int targetSquare, int attackerColor) {
    assert Square.isValid(targetSquare);
    assert IntColor.isValid(attackerColor);

    // Pawn attacks
    int pawnPiece;
    int sign;
    if (attackerColor == IntColor.WHITE) {
      pawnPiece = IntPiece.WHITEPAWN;
      sign = -1;
    } else {
      pawnPiece = IntPiece.BLACKPAWN;
      sign = 1;
    }
    for (int i = 1; i < moveDeltaPawn.length; ++i) {
      int attackerSquare = targetSquare + sign * moveDeltaPawn[i];
      if (Square.isLegal(attackerSquare)) {
        int attackerPawn = board.board[attackerSquare];

        if (attackerPawn == pawnPiece) {
          return true;
        }
      }
    }

    return isAttacked(targetSquare, attackerColor, IntChessman.KNIGHT, moveDeltaKnight)
      || isAttacked(targetSquare, attackerColor, IntChessman.BISHOP, moveDeltaBishop)
      || isAttacked(targetSquare, attackerColor, IntChessman.ROOK, moveDeltaRook)
      || isAttacked(targetSquare, attackerColor, IntChessman.QUEEN, moveDeltaQueen)
      || isAttacked(targetSquare, attackerColor, IntChessman.KING, moveDeltaKing);
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
        int attackerPiece = board.board[attackerSquare];

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
