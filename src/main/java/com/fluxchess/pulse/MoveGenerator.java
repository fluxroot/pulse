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

public final class MoveGenerator {

  // Move deltas
  public static final int[][] moveDeltaPawn = {
      {Square.deltaN, Square.deltaNE, Square.deltaNW}, // Color.WHITE
      {Square.deltaS, Square.deltaSE, Square.deltaSW}  // Color.BLACK
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

  private final MoveList moveList = new MoveList();
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

  public static MoveGenerator getMoveGenerator(int depth, Board board, int height, boolean isCheck) {
    assert board != null;
    assert height >= 0 && height <= Search.MAX_HEIGHT;

    MoveGenerator moveGenerator = moveGenerators[height];
    moveGenerator.board = board;
    moveGenerator.isCheck = isCheck;
    moveGenerator.currentStateIndex = 0;
    moveGenerator.moveList.size = 0;
    moveGenerator.currentMoveIndex = 0;

    if (depth > 0) {
      moveGenerator.states = mainStates;
    } else {
      moveGenerator.states = quiescentStates;
    }

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
            if (!isLegal(move) || (!isCheck && Move.getTargetPiece(move) == Piece.NOPIECE)) {
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
            int square = Bitboard.next(board.kings[board.activeColor].squares);
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
      int square = Bitboard.next(squares);
      addPawnMoves(list, square);
    }
    for (long squares = board.knights[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      addMoves(list, square, moveDeltaKnight);
    }
    for (long squares = board.bishops[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      addMoves(list, square, moveDeltaBishop);
    }
    for (long squares = board.rooks[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      addMoves(list, square, moveDeltaRook);
    }
    for (long squares = board.queens[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = Bitboard.next(squares);
      addMoves(list, square, moveDeltaQueen);
    }
    int square = Bitboard.next(board.kings[activeColor].squares);
    addMoves(list, square, moveDeltaKing);
  }

  private void addMoves(MoveList list, int originSquare, int[] moveDelta) {
    assert list != null;
    assert Square.isValid(originSquare);
    assert moveDelta != null;

    int originPiece = board.board[originSquare];
    assert Piece.isValid(originPiece);
    boolean sliding = Piece.Type.isSliding(Piece.getType(originPiece));
    int oppositeColor = Color.opposite(Piece.getColor(originPiece));

    for (int delta : moveDelta) {
      int targetSquare = originSquare + delta;

      while (Square.isLegal(targetSquare)) {
        int targetPiece = board.board[targetSquare];

        if (targetPiece == Piece.NOPIECE) {
          list.entries[list.size++].move = Move.valueOf(Move.Type.NORMAL, originSquare, targetSquare, originPiece, Piece.NOPIECE, Piece.Type.NOTYPE);

          if (!sliding) {
            break;
          }

          targetSquare += delta;
        } else {
          if (Piece.getColor(targetPiece) == oppositeColor
              && Piece.getType(targetPiece) != Piece.Type.KING) {
            list.entries[list.size++].move = Move.valueOf(Move.Type.NORMAL, originSquare, targetSquare, originPiece, targetPiece, Piece.Type.NOTYPE);
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
    assert Piece.isValid(pawnPiece);
    assert Piece.getType(pawnPiece) == Piece.Type.PAWN;
    int pawnColor = Piece.getColor(pawnPiece);

    // Generate only capturing moves first (i = 1)
    for (int i = 1; i < moveDeltaPawn[pawnColor].length; ++i) {
      int delta = moveDeltaPawn[pawnColor][i];

      int targetSquare = pawnSquare + delta;
      if (Square.isLegal(targetSquare)) {
        int targetPiece = board.board[targetSquare];

        if (targetPiece != Piece.NOPIECE) {
          if (Piece.getColor(targetPiece) == Color.opposite(pawnColor)
              && Piece.getType(targetPiece) != Piece.Type.KING) {
            // Capturing move

            if ((pawnColor == Color.WHITE && Square.getRank(targetSquare) == Rank.R8)
                || (pawnColor == Color.BLACK && Square.getRank(targetSquare) == Rank.R1)) {
              // Pawn promotion capturing move

              list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.QUEEN);
              list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.ROOK);
              list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.BISHOP);
              list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.KNIGHT);
            } else {
              // Normal capturing move

              list.entries[list.size++].move = Move.valueOf(Move.Type.NORMAL, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.NOTYPE);
            }
          }
        } else if (targetSquare == board.enPassant) {
          // En passant move
          assert (pawnColor == Color.BLACK && Square.getRank(targetSquare) == Rank.R3)
              || (pawnColor == Color.WHITE && Square.getRank(targetSquare) == Rank.R6);

          int captureSquare = targetSquare + (pawnColor == Color.WHITE ? Square.deltaS : Square.deltaN);
          targetPiece = board.board[captureSquare];
          assert Piece.getType(targetPiece) == Piece.Type.PAWN;
          assert Piece.getColor(targetPiece) == Color.opposite(pawnColor);

          list.entries[list.size++].move = Move.valueOf(Move.Type.ENPASSANT, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.NOTYPE);
        }
      }
    }

    // Generate non-capturing moves
    int delta = moveDeltaPawn[pawnColor][0];

    // Move one rank forward
    int targetSquare = pawnSquare + delta;
    if (Square.isLegal(targetSquare) && board.board[targetSquare] == Piece.NOPIECE) {
      if ((pawnColor == Color.WHITE && Square.getRank(targetSquare) == Rank.R8)
          || (pawnColor == Color.BLACK && Square.getRank(targetSquare) == Rank.R1)) {
        // Pawn promotion move

        list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.QUEEN);
        list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.ROOK);
        list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.BISHOP);
        list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.KNIGHT);
      } else {
        // Normal move

        list.entries[list.size++].move = Move.valueOf(Move.Type.NORMAL, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.NOTYPE);

        // Move another rank forward
        targetSquare += delta;
        if (Square.isLegal(targetSquare) && board.board[targetSquare] == Piece.NOPIECE) {
          if ((pawnColor == Color.WHITE && Square.getRank(targetSquare) == Rank.R4)
              || (pawnColor == Color.BLACK && Square.getRank(targetSquare) == Rank.R5)) {
            // Pawn double move

            list.entries[list.size++].move = Move.valueOf(Move.Type.PAWNDOUBLE, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.NOTYPE);
          }
        }
      }
    }
  }

  private void addCastlingMoves(MoveList list, int kingSquare) {
    assert list != null;
    assert Square.isValid(kingSquare);

    int kingPiece = board.board[kingSquare];
    assert Piece.isValid(kingPiece);
    assert Piece.getType(kingPiece) == Piece.Type.KING;

    if (Piece.getColor(kingPiece) == Color.WHITE) {
      // Do not test g1 whether it is attacked as we will test it in isLegal()
      if (board.castling[Color.WHITE][Castling.KINGSIDE] != File.NOFILE
          && board.board[Square.f1] == Piece.NOPIECE
          && board.board[Square.g1] == Piece.NOPIECE
          && !board.isAttacked(Square.f1, Color.BLACK)) {
        assert board.board[Square.e1] == Piece.WHITEKING;
        assert board.board[Square.h1] == Piece.WHITEROOK;

        list.entries[list.size++].move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.g1, kingPiece, Piece.NOPIECE, Piece.Type.NOTYPE);
      }
      // Do not test c1 whether it is attacked as we will test it in isLegal()
      if (board.castling[Color.WHITE][Castling.QUEENSIDE] != File.NOFILE
          && board.board[Square.b1] == Piece.NOPIECE
          && board.board[Square.c1] == Piece.NOPIECE
          && board.board[Square.d1] == Piece.NOPIECE
          && !board.isAttacked(Square.d1, Color.BLACK)) {
        assert board.board[Square.e1] == Piece.WHITEKING;
        assert board.board[Square.a1] == Piece.WHITEROOK;

        list.entries[list.size++].move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.c1, kingPiece, Piece.NOPIECE, Piece.Type.NOTYPE);
      }
    } else {
      // Do not test g8 whether it is attacked as we will test it in isLegal()
      if (board.castling[Color.BLACK][Castling.KINGSIDE] != File.NOFILE
          && board.board[Square.f8] == Piece.NOPIECE
          && board.board[Square.g8] == Piece.NOPIECE
          && !board.isAttacked(Square.f8, Color.WHITE)) {
        assert board.board[Square.e8] == Piece.BLACKKING;
        assert board.board[Square.h8] == Piece.BLACKROOK;

        list.entries[list.size++].move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.g8, kingPiece, Piece.NOPIECE, Piece.Type.NOTYPE);
      }
      // Do not test c8 whether it is attacked as we will test it in isLegal()
      if (board.castling[Color.BLACK][Castling.QUEENSIDE] != File.NOFILE
          && board.board[Square.b8] == Piece.NOPIECE
          && board.board[Square.c8] == Piece.NOPIECE
          && board.board[Square.d8] == Piece.NOPIECE
          && !board.isAttacked(Square.d8, Color.WHITE)) {
        assert board.board[Square.e8] == Piece.BLACKKING;
        assert board.board[Square.a8] == Piece.BLACKROOK;

        list.entries[list.size++].move = Move.valueOf(Move.Type.CASTLING, kingSquare, Square.c8, kingPiece, Piece.NOPIECE, Piece.Type.NOTYPE);
      }
    }
  }

  private boolean isLegal(int move) {
    int activeColor = board.activeColor;

    board.makeMove(move);
    boolean isCheck = board.isAttacked(Bitboard.next(board.kings[activeColor].squares), Color.opposite(activeColor));
    board.undoMove(move);

    return !isCheck;
  }

}
