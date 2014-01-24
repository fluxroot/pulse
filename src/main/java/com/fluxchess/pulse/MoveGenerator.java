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
  public static final int[][] moveDeltaPawn = {
    {Square.deltaN, Square.deltaNE, Square.deltaNW}, // IntColor.WHITE
    {Square.deltaS, Square.deltaSE, Square.deltaSW}  // IntColor.BLACK
  };
  public static final int[] moveDeltaKnight = {
    Square.deltaN + Square.deltaN + Square.deltaE,
    Square.deltaN + Square.deltaN + Square.deltaW,
    Square.deltaN + Square.deltaE + Square.deltaE,
    Square.deltaN + Square.deltaW + Square.deltaW,
    Square.deltaS + Square.deltaS + Square.deltaE,
    Square.deltaS + Square.deltaS + Square.deltaW,
    Square.deltaS + Square.deltaE + Square.deltaE,
    Square.deltaS + Square.deltaW + Square.deltaW
  };
  public static final int[] moveDeltaBishop = {
    Square.deltaNE, Square.deltaNW, Square.deltaSE, Square.deltaSW
  };
  public static final int[] moveDeltaRook = {
    Square.deltaN, Square.deltaE, Square.deltaS, Square.deltaW
  };
  public static final int[] moveDeltaQueen = {
    Square.deltaN, Square.deltaE, Square.deltaS, Square.deltaW,
    Square.deltaNE, Square.deltaNW, Square.deltaSE, Square.deltaSW
  };
  public static final int[] moveDeltaKing = {
    Square.deltaN, Square.deltaE, Square.deltaS, Square.deltaW,
    Square.deltaNE, Square.deltaNW, Square.deltaSE, Square.deltaSW
  };

  // We will store a MoveGenerator for each ply so we don't have to create them
  // in search. (which is expensive)
  private static final MoveGenerator[] moveGenerators = new MoveGenerator[Search.MAX_HEIGHT];

  // We will use a staged move generation so we can easily extend it with
  // other features like transposition tables.
  private static final State[] mainStates = {State.BEGIN, State.MAIN, State.END};
  private static final State[] quiescentStates = {State.BEGIN, State.QUIESCENT, State.END};

  private Board board = null;
  private boolean isCheck = false;

  private State[] states = null;
  private int currentStateIndex = 0;

  private MoveList moveList = new MoveList();
  private int currentMoveIndex = 0;

  private static enum State {
    BEGIN,
    MAIN,
    QUIESCENT,
    END
  }

  static {
    for (int i = 0; i < Search.MAX_HEIGHT; ++i) {
      moveGenerators[i] = new MoveGenerator();
    }
  }

  public static MoveGenerator getMainGenerator(Board board, int height, boolean isCheck) {
    assert board != null;
    assert height >= 0 && height <= Search.MAX_HEIGHT;

    MoveGenerator moveGenerator = moveGenerators[height];
    moveGenerator.board = board;
    moveGenerator.isCheck = isCheck;
    moveGenerator.states = mainStates;
    moveGenerator.currentStateIndex = 0;
    moveGenerator.moveList.size = 0;
    moveGenerator.currentMoveIndex = 0;

    return moveGenerator;
  }

  public static MoveGenerator getQuiescentGenerator(Board board, int height, boolean isCheck) {
    assert board != null;
    assert height >= 0 && height <= Search.MAX_HEIGHT;

    MoveGenerator moveGenerator = moveGenerators[height];
    moveGenerator.board = board;
    moveGenerator.isCheck = isCheck;
    moveGenerator.states = quiescentStates;
    moveGenerator.currentStateIndex = 0;
    moveGenerator.moveList.size = 0;
    moveGenerator.currentMoveIndex = 0;

    return moveGenerator;
  }

  private MoveGenerator() {
  }

  /**
   * Returns the next legal move. We will go through our states and generate
   * the appropriate moves for the current state.
   *
   * @return the next legal move, or Move.NOMOVE if there's is no next move.
   */
  public int next() {
    while (true) {
      // Check whether we have any move in the list
      if (currentMoveIndex < moveList.size) {
        int move = moveList.entries[currentMoveIndex++].move;

        switch (states[currentStateIndex]) {
          case MAIN:
            // Discard all non-legal moves
            if (!isLegal(move)) {
              continue;
            }
            break;
          case QUIESCENT:
            // Discard all non-legal moves. If not in check return only capturing moves.
            if (!isLegal(move) || (!isCheck && Move.getTargetPiece(move) == IntPiece.NOPIECE)) {
              continue;
            }
            break;
          default:
            assert false : states[currentStateIndex];
            break;
        }

        return move;
      }

      // If we don't have any move in the list, lets generate the moves for the
      // next state.
      ++currentStateIndex;
      currentMoveIndex = 0;
      moveList.size = 0;

      // We simply generate all moves at once here. However we could also
      // generate capturing moves first and then all non-capturing moves.
      switch (states[currentStateIndex]) {
        case MAIN:
          addDefaultMoves(moveList);

          if (!isCheck) {
            int square = ChessmanList.next(board.kings[board.activeColor].squares);
            addCastlingMoves(moveList, square);
          }

          moveList.rateFromMVVLVA();
          moveList.sort();
          break;
        case QUIESCENT:
          addDefaultMoves(moveList);

          moveList.rateFromMVVLVA();
          moveList.sort();
          break;
        case END:
          return Move.NOMOVE;
        default:
          assert false;
          break;
      }
    }
  }

  private void addDefaultMoves(MoveList list) {
    assert list != null;

    int activeColor = board.activeColor;

    for (long squares = board.pawns[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addPawnMoves(list, square);
    }
    for (long squares = board.knights[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(list, square, moveDeltaKnight);
    }
    for (long squares = board.bishops[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(list, square, moveDeltaBishop);
    }
    for (long squares = board.rooks[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(list, square, moveDeltaRook);
    }
    for (long squares = board.queens[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(list, square, moveDeltaQueen);
    }
    int square = ChessmanList.next(board.kings[activeColor].squares);
    addMoves(list, square, moveDeltaKing);
  }

  private void addMoves(MoveList list, int originSquare, int[] moveDelta) {
    assert list != null;
    assert Square.isValid(originSquare);
    assert moveDelta != null;

    int originPiece = board.board[originSquare];
    assert IntPiece.isValid(originPiece);
    boolean sliding = IntChessman.isSliding(IntPiece.getChessman(originPiece));
    int oppositeColor = IntColor.opposite(IntPiece.getColor(originPiece));

    for (int delta : moveDelta) {
      int targetSquare = originSquare + delta;

      while (Square.isLegal(targetSquare)) {
        int targetPiece = board.board[targetSquare];

        if (targetPiece == IntPiece.NOPIECE) {
          list.entries[list.size++].move = Move.valueOf(Move.Type.NORMAL, originSquare, targetSquare, originPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);

          if (!sliding) {
            break;
          }

          targetSquare += delta;
        } else {
          if (IntPiece.getColor(targetPiece) == oppositeColor
            && IntPiece.getChessman(targetPiece) != IntChessman.KING) {
            list.entries[list.size++].move = Move.valueOf(Move.Type.NORMAL, originSquare, targetSquare, originPiece, targetPiece, IntChessman.NOCHESSMAN);
          }

          break;
        }
      }
    }
  }

  private void addPawnMoves(MoveList list, int pawnSquare) {
    assert list != null;
    assert Square.isValid(pawnSquare);

    int pawnPiece = board.board[pawnSquare];
    assert IntPiece.isValid(pawnPiece);
    assert IntPiece.getChessman(pawnPiece) == IntChessman.PAWN;
    int pawnColor = IntPiece.getColor(pawnPiece);

    // Generate only capturing moves first (i = 1)
    for (int i = 1; i < moveDeltaPawn[pawnColor].length; ++i) {
      int delta = moveDeltaPawn[pawnColor][i];

      int targetSquare = pawnSquare + delta;
      if (Square.isLegal(targetSquare)) {
        int targetPiece = board.board[targetSquare];

        if (targetPiece != IntPiece.NOPIECE) {
          if (IntPiece.getColor(targetPiece) == IntColor.opposite(pawnColor)
            && IntPiece.getChessman(targetPiece) != IntChessman.KING) {
            // Capturing move

            if ((pawnColor == IntColor.WHITE && Square.getRank(targetSquare) == IntRank.R8)
              || (pawnColor == IntColor.BLACK && Square.getRank(targetSquare) == IntRank.R1)) {
              // Pawn promotion capturing move

              list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.QUEEN);
              list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.ROOK);
              list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.BISHOP);
              list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.KNIGHT);
            } else {
              // Normal capturing move

              list.entries[list.size++].move = Move.valueOf(Move.Type.NORMAL, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.NOCHESSMAN);
            }
          }
        } else if (targetSquare == board.enPassant) {
          // En passant move
          assert (pawnColor == IntColor.BLACK && Square.getRank(targetSquare) == IntRank.R3)
            || (pawnColor == IntColor.WHITE && Square.getRank(targetSquare) == IntRank.R6);

          int captureSquare = targetSquare + (pawnColor == IntColor.WHITE ? Square.deltaS : Square.deltaN);
          targetPiece = board.board[captureSquare];
          assert IntPiece.getChessman(targetPiece) == IntChessman.PAWN;
          assert IntPiece.getColor(targetPiece) == IntColor.opposite(pawnColor);

          list.entries[list.size++].move = Move.valueOf(Move.Type.ENPASSANT, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.NOCHESSMAN);
        }
      }
    }

    // Generate non-capturing moves
    int delta = moveDeltaPawn[pawnColor][0];

    // Move one rank forward
    int targetSquare = pawnSquare + delta;
    if (Square.isLegal(targetSquare) && board.board[targetSquare] == IntPiece.NOPIECE) {
      if ((pawnColor == IntColor.WHITE && Square.getRank(targetSquare) == IntRank.R8)
        || (pawnColor == IntColor.BLACK && Square.getRank(targetSquare) == IntRank.R1)) {
        // Pawn promotion move

        list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.QUEEN);
        list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.ROOK);
        list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.BISHOP);
        list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.KNIGHT);
      } else {
        // Normal move

        list.entries[list.size++].move = Move.valueOf(Move.Type.NORMAL, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);

        // Move another rank forward
        targetSquare += delta;
        if (Square.isLegal(targetSquare) && board.board[targetSquare] == IntPiece.NOPIECE) {
          if ((pawnColor == IntColor.WHITE && Square.getRank(targetSquare) == IntRank.R4)
            || (pawnColor == IntColor.BLACK && Square.getRank(targetSquare) == IntRank.R5)) {
            // Pawn double move

            list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNDOUBLE, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
          }
        }
      }
    }
  }

  private void addCastlingMoves(MoveList list, int kingSquare) {
    assert list != null;
    assert Square.isValid(kingSquare);

    int kingPiece = board.board[kingSquare];
    assert IntPiece.isValid(kingPiece);
    assert IntPiece.getChessman(kingPiece) == IntChessman.KING;

    if (IntPiece.getColor(kingPiece) == IntColor.WHITE) {
      // Do not test g1 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE
        && board.board[Square.f1] == IntPiece.NOPIECE
        && board.board[Square.g1] == IntPiece.NOPIECE
        && !board.isAttacked(Square.f1, IntColor.BLACK)) {
        assert board.board[Square.e1] == IntPiece.WHITEKING;
        assert board.board[Square.h1] == IntPiece.WHITEROOK;

        list.entries[list.size++].move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.g1, kingPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
      }
      // Do not test c1 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE
        && board.board[Square.b1] == IntPiece.NOPIECE
        && board.board[Square.c1] == IntPiece.NOPIECE
        && board.board[Square.d1] == IntPiece.NOPIECE
        && !board.isAttacked(Square.d1, IntColor.BLACK)) {
        assert board.board[Square.e1] == IntPiece.WHITEKING;
        assert board.board[Square.a1] == IntPiece.WHITEROOK;

        list.entries[list.size++].move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.c1, kingPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
      }
    } else {
      // Do not test g8 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE
        && board.board[Square.f8] == IntPiece.NOPIECE
        && board.board[Square.g8] == IntPiece.NOPIECE
        && !board.isAttacked(Square.f8, IntColor.WHITE)) {
        assert board.board[Square.e8] == IntPiece.BLACKKING;
        assert board.board[Square.h8] == IntPiece.BLACKROOK;

        list.entries[list.size++].move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.g8, kingPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
      }
      // Do not test c8 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE
        && board.board[Square.b8] == IntPiece.NOPIECE
        && board.board[Square.c8] == IntPiece.NOPIECE
        && board.board[Square.d8] == IntPiece.NOPIECE
        && !board.isAttacked(Square.d8, IntColor.WHITE)) {
        assert board.board[Square.e8] == IntPiece.BLACKKING;
        assert board.board[Square.a8] == IntPiece.BLACKROOK;

        list.entries[list.size++].move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.c8, kingPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
      }
    }
  }

  private boolean isLegal(int move) {
    int activeColor = board.activeColor;

    board.makeMove(move);
    boolean isCheck = board.isAttacked(ChessmanList.next(board.kings[activeColor].squares), IntColor.opposite(activeColor));
    board.undoMove(move);

    return !isCheck;
  }

}
