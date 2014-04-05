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

import static com.fluxchess.pulse.Color.BLACK;
import static com.fluxchess.pulse.Color.WHITE;

final class MoveGenerator {

  // Move deltas
  static final int[][] moveDeltaPawn = {
      {Square.N, Square.NE, Square.NW}, // Color.WHITE
      {Square.S, Square.SE, Square.SW}  // Color.BLACK
  };
  static final int[] moveDeltaKnight = {
      Square.N + Square.N + Square.E,
      Square.N + Square.N + Square.W,
      Square.N + Square.E + Square.E,
      Square.N + Square.W + Square.W,
      Square.S + Square.S + Square.E,
      Square.S + Square.S + Square.W,
      Square.S + Square.E + Square.E,
      Square.S + Square.W + Square.W
  };
  static final int[] moveDeltaBishop = {
      Square.NE, Square.NW, Square.SE, Square.SW
  };
  static final int[] moveDeltaRook = {
      Square.N, Square.E, Square.S, Square.W
  };
  static final int[] moveDeltaQueen = {
      Square.N, Square.E, Square.S, Square.W,
      Square.NE, Square.NW, Square.SE, Square.SW
  };
  static final int[] moveDeltaKing = {
      Square.N, Square.E, Square.S, Square.W,
      Square.NE, Square.NW, Square.SE, Square.SW
  };

  // We will store a MoveGenerator for each ply so we don't have to create them
  // in search. (which is expensive)
  private static final MoveGenerator[] moveGenerators = new MoveGenerator[Search.MAX_PLY];

  // We will use a staged move generation so we can easily extend it with
  // other features like transposition tables.
  private static final State[] mainStates = {State.BEGIN, State.MAIN, State.END};
  private static final State[] quiescentStates = {State.BEGIN, State.QUIESCENT, State.END};

  private Board board = null;
  private boolean isCheck = false;

  private State[] states = null;
  private int stateIndex = 0;

  private final MoveList moves = new MoveList();
  private int moveIndex = 0;

  private static enum State {
    BEGIN,
    MAIN,
    QUIESCENT,
    END
  }

  static {
    for (int i = 0; i < Search.MAX_PLY; ++i) {
      moveGenerators[i] = new MoveGenerator();
    }
  }

  public static MoveGenerator getMoveGenerator(Board board, int depth, int ply, boolean isCheck) {
    assert board != null;
    assert ply >= 0 && ply < Search.MAX_PLY;

    MoveGenerator moveGenerator = moveGenerators[ply];
    moveGenerator.board = board;
    moveGenerator.isCheck = isCheck;
    moveGenerator.stateIndex = 0;
    moveGenerator.moves.size = 0;
    moveGenerator.moveIndex = 0;

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
   * Returns the next legal move.
   */
  public int nextLegal() {
    boolean isLegal = false;
    int move = Move.NOMOVE;

    while (!isLegal && (move = next()) != Move.NOMOVE) {
      isLegal = board.makeMove(move);
      board.undoMove(move);
    }

    return move;
  }

  /**
   * Returns the next pseudo-legal move. We will go through our states and
   * generate the appropriate moves for the current state.
   *
   * @return the next pseudo-legal move,
   * or Move.NOMOVE if there is no next move.
   */
  public int next() {
    while (true) {
      // Check whether we have any move in the list
      if (moveIndex < moves.size) {
        int move = moves.entries[moveIndex++].move;

        switch (states[stateIndex]) {
          case MAIN:
            break;
          case QUIESCENT:
            // Return only capturing moves, if not in check
            if (!isCheck && Move.getTargetPiece(move) == Piece.NOPIECE) {
              continue;
            }
            break;
          default:
            throw new IllegalStateException();
        }

        return move;
      }

      // If we don't have any move in the list, lets generate the moves for the
      // next state.
      ++stateIndex;
      moveIndex = 0;
      moves.size = 0;

      // We simply generate all moves at once here. However we could also
      // generate capturing moves first and then all non-capturing moves.
      switch (states[stateIndex]) {
        case MAIN:
          addMoves(moves);

          if (!isCheck) {
            int square = Bitboard.next(board.kings[board.activeColor].squares);
            addCastlingMoves(moves, square);
          }

          moves.rateFromMVVLVA();
          moves.sort();
          break;
        case QUIESCENT:
          addMoves(moves);

          moves.rateFromMVVLVA();
          moves.sort();
          break;
        case END:
          return Move.NOMOVE;
        default:
          throw new IllegalStateException();
      }
    }
  }

  private void addMoves(MoveList list) {
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

    // Go through all move deltas for this piece
    for (int delta : moveDelta) {
      int targetSquare = originSquare + delta;

      // Check if we're still on the board
      while (Square.isValid(targetSquare)) {
        int targetPiece = board.board[targetSquare];

        if (targetPiece == Piece.NOPIECE) {
          // quiet move
          list.entries[list.size++].move = Move.valueOf(
              Move.Type.NORMAL, originSquare, targetSquare, originPiece, Piece.NOPIECE, Piece.Type.NOPIECETYPE);

          if (!sliding) {
            break;
          }

          targetSquare += delta;
        } else {
          if (Piece.getColor(targetPiece) == oppositeColor) {
            // capturing move
            list.entries[list.size++].move = Move.valueOf(
                Move.Type.NORMAL, originSquare, targetSquare, originPiece, targetPiece, Piece.Type.NOPIECETYPE);
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
      if (Square.isValid(targetSquare)) {
        int targetPiece = board.board[targetSquare];

        if (targetPiece != Piece.NOPIECE) {
          if (Piece.getColor(targetPiece) == Color.opposite(pawnColor)) {
            // Capturing move

            if ((pawnColor == WHITE && Square.getRank(targetSquare) == Rank.r8)
                || (pawnColor == BLACK && Square.getRank(targetSquare) == Rank.r1)) {
              // Pawn promotion capturing move

              list.entries[list.size++].move = Move.valueOf(
                  Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.QUEEN);
              list.entries[list.size++].move = Move.valueOf(
                  Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.ROOK);
              list.entries[list.size++].move = Move.valueOf(
                  Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.BISHOP);
              list.entries[list.size++].move = Move.valueOf(
                  Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.KNIGHT);
            } else {
              // Normal capturing move

              list.entries[list.size++].move = Move.valueOf(
                  Move.Type.NORMAL, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.NOPIECETYPE);
            }
          }
        } else if (targetSquare == board.enPassant) {
          // En passant move
          assert (pawnColor == BLACK && Square.getRank(targetSquare) == Rank.r3)
              || (pawnColor == WHITE && Square.getRank(targetSquare) == Rank.r6);

          int captureSquare = targetSquare + (pawnColor == WHITE ? Square.S : Square.N);
          targetPiece = board.board[captureSquare];
          assert Piece.getType(targetPiece) == Piece.Type.PAWN;
          assert Piece.getColor(targetPiece) == Color.opposite(pawnColor);

          list.entries[list.size++].move = Move.valueOf(
              Move.Type.ENPASSANT, pawnSquare, targetSquare, pawnPiece, targetPiece, Piece.Type.NOPIECETYPE);
        }
      }
    }

    // Generate non-capturing moves
    int delta = moveDeltaPawn[pawnColor][0];

    // Move one rank forward
    int targetSquare = pawnSquare + delta;
    if (Square.isValid(targetSquare) && board.board[targetSquare] == Piece.NOPIECE) {
      if ((pawnColor == WHITE && Square.getRank(targetSquare) == Rank.r8)
          || (pawnColor == BLACK && Square.getRank(targetSquare) == Rank.r1)) {
        // Pawn promotion move

        list.entries[list.size++].move = Move.valueOf(
            Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.QUEEN);
        list.entries[list.size++].move = Move.valueOf(
            Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.ROOK);
        list.entries[list.size++].move = Move.valueOf(
            Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.BISHOP);
        list.entries[list.size++].move = Move.valueOf(
            Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.KNIGHT);
      } else {
        // Normal move

        list.entries[list.size++].move = Move.valueOf(
            Move.Type.NORMAL, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.NOPIECETYPE);

        // Move another rank forward
        targetSquare += delta;
        if (Square.isValid(targetSquare) && board.board[targetSquare] == Piece.NOPIECE) {
          if ((pawnColor == WHITE && Square.getRank(targetSquare) == Rank.r4)
              || (pawnColor == BLACK && Square.getRank(targetSquare) == Rank.r5)) {
            // Pawn double move

            list.entries[list.size++].move = Move.valueOf(
                Move.Type.PAWNDOUBLE, pawnSquare, targetSquare, pawnPiece, Piece.NOPIECE, Piece.Type.NOPIECETYPE);
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

    if (Piece.getColor(kingPiece) == WHITE) {
      // Do not test g1 whether it is attacked as we will test it in isLegal()
      if (board.castlingRights[Castling.WHITE_KINGSIDE] != File.NOFILE
          && board.board[Square.f1] == Piece.NOPIECE
          && board.board[Square.g1] == Piece.NOPIECE
          && !board.isAttacked(Square.f1, BLACK)) {
        assert board.board[Square.e1] == Piece.WHITE_KING;
        assert board.board[Square.h1] == Piece.WHITE_ROOK;

        list.entries[list.size++].move = Move.valueOf(
            Move.Type.CASTLING, kingSquare, Square.g1, kingPiece, Piece.NOPIECE, Piece.Type.NOPIECETYPE);
      }
      // Do not test c1 whether it is attacked as we will test it in isLegal()
      if (board.castlingRights[Castling.WHITE_QUEENSIDE] != File.NOFILE
          && board.board[Square.b1] == Piece.NOPIECE
          && board.board[Square.c1] == Piece.NOPIECE
          && board.board[Square.d1] == Piece.NOPIECE
          && !board.isAttacked(Square.d1, BLACK)) {
        assert board.board[Square.e1] == Piece.WHITE_KING;
        assert board.board[Square.a1] == Piece.WHITE_ROOK;

        list.entries[list.size++].move = Move.valueOf(
            Move.Type.CASTLING, kingSquare, Square.c1, kingPiece, Piece.NOPIECE, Piece.Type.NOPIECETYPE);
      }
    } else {
      // Do not test g8 whether it is attacked as we will test it in isLegal()
      if (board.castlingRights[Castling.BLACK_KINGSIDE] != File.NOFILE
          && board.board[Square.f8] == Piece.NOPIECE
          && board.board[Square.g8] == Piece.NOPIECE
          && !board.isAttacked(Square.f8, WHITE)) {
        assert board.board[Square.e8] == Piece.BLACK_KING;
        assert board.board[Square.h8] == Piece.BLACK_ROOK;

        list.entries[list.size++].move = Move.valueOf(
            Move.Type.CASTLING, kingSquare, Square.g8, kingPiece, Piece.NOPIECE, Piece.Type.NOPIECETYPE);
      }
      // Do not test c8 whether it is attacked as we will test it in isLegal()
      if (board.castlingRights[Castling.BLACK_QUEENSIDE] != File.NOFILE
          && board.board[Square.b8] == Piece.NOPIECE
          && board.board[Square.c8] == Piece.NOPIECE
          && board.board[Square.d8] == Piece.NOPIECE
          && !board.isAttacked(Square.d8, WHITE)) {
        assert board.board[Square.e8] == Piece.BLACK_KING;
        assert board.board[Square.a8] == Piece.BLACK_ROOK;

        list.entries[list.size++].move = Move.valueOf(
            Move.Type.CASTLING, kingSquare, Square.c8, kingPiece, Piece.NOPIECE, Piece.Type.NOPIECETYPE);
      }
    }
  }

}
