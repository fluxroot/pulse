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

  private static final class Attack {
    public static final int N = 0; // Neutral
    public static final int D = 1; // Diagonal
    public static final int u = 2; // Diagonal up in one
    public static final int d = 3; // Diagonal down in one
    public static final int S = 4; // Straight
    public static final int s = 5; // Straight in one
    public static final int K = 6; // Knight

    public static final int[] vector = {
      N,N,N,N,N,N,N,N,                 //   0 -   7
      D,N,N,N,N,N,N,S,N,N,N,N,N,N,D,N, //   8 -  23
      N,D,N,N,N,N,N,S,N,N,N,N,N,D,N,N, //  24 -  39
      N,N,D,N,N,N,N,S,N,N,N,N,D,N,N,N, //  40 -  55
      N,N,N,D,N,N,N,S,N,N,N,D,N,N,N,N, //  56 -  71
      N,N,N,N,D,N,N,S,N,N,D,N,N,N,N,N, //  72 -  87
      N,N,N,N,N,D,K,S,K,D,N,N,N,N,N,N, //  88 - 103
      N,N,N,N,N,K,d,s,d,K,N,N,N,N,N,N, // 104 - 119
      S,S,S,S,S,S,s,N,s,S,S,S,S,S,S,N, // 120 - 135
      N,N,N,N,N,K,u,s,u,K,N,N,N,N,N,N, // 136 - 151
      N,N,N,N,N,D,K,S,K,D,N,N,N,N,N,N, // 152 - 167
      N,N,N,N,D,N,N,S,N,N,D,N,N,N,N,N, // 168 - 183
      N,N,N,D,N,N,N,S,N,N,N,D,N,N,N,N, // 184 - 199
      N,N,D,N,N,N,N,S,N,N,N,N,D,N,N,N, // 200 - 215
      N,D,N,N,N,N,N,S,N,N,N,N,N,D,N,N, // 216 - 231
      D,N,N,N,N,N,N,S,N,N,N,N,N,N,D,N, // 232 - 247
      N,N,N,N,N,N,N,N                  // 248 - 255
    };

    public static final int[] deltas = {
      0,  0,  0,  0,  0,  0,  0,  0,                                 //   0 -   7
    -17,  0,  0,  0,  0,  0,  0,-16,  0,  0,  0,  0,  0,  0,-15,  0, //   8 -  23
      0,-17,  0,  0,  0,  0,  0,-16,  0,  0,  0,  0,  0,-15,  0,  0, //  24 -  39
      0,  0,-17,  0,  0,  0,  0,-16,  0,  0,  0,  0,-15,  0,  0,  0, //  40 -  55
      0,  0,  0,-17,  0,  0,  0,-16,  0,  0,  0,-15,  0,  0,  0,  0, //  56 -  71
      0,  0,  0,  0,-17,  0,  0,-16,  0,  0,-15,  0,  0,  0,  0,  0, //  72 -  87
      0,  0,  0,  0,  0,-17,-33,-16,-31,-15,  0,  0,  0,  0,  0,  0, //  88 - 103
      0,  0,  0,  0,  0,-18,-17,-16,-15,-14,  0,  0,  0,  0,  0,  0, // 104 - 119
     -1, -1, -1, -1, -1, -1, -1,  0,  1,  1,  1,  1,  1,  1,  1,  0, // 120 - 135
      0,  0,  0,  0,  0, 14, 15, 16, 17, 18,  0,  0,  0,  0,  0,  0, // 136 - 151
      0,  0,  0,  0,  0, 15, 31, 16, 33, 17,  0,  0,  0,  0,  0,  0, // 152 - 167
      0,  0,  0,  0, 15,  0,  0, 16,  0,  0, 17,  0,  0,  0,  0,  0, // 168 - 183
      0,  0,  0, 15,  0,  0,  0, 16,  0,  0,  0, 17,  0,  0,  0,  0, // 184 - 199
      0,  0, 15,  0,  0,  0,  0, 16,  0,  0,  0,  0, 17,  0,  0,  0, // 200 - 215
      0, 15,  0,  0,  0,  0,  0, 16,  0,  0,  0,  0,  0, 17,  0,  0, // 216 - 231
     15,  0,  0,  0,  0,  0,  0, 16,  0,  0,  0,  0,  0,  0, 17,  0, // 232 - 247
      0,  0,  0,  0,  0,  0,  0, 0                                   // 248 - 255
    };

    private static final int MAXATTACK = 16;

    public int count = 0;
    public final int[] delta = new int[MAXATTACK];
    public final int[] square = new int[MAXATTACK];
  }

  public MoveGenerator(Board board) {
    assert board != null;

    this.board = board;
  }

  public boolean isCheck() {
    // Check whether our king is attacked by any opponent piece
    return isAttacked(ChessmanList.next(board.kings[board.activeColor].squares), IntColor.opposite(board.activeColor));
  }

  /**
   * Returns all legal moves. If we are in check, we will generate evasion
   * moves to get out of it.
   *
   * @return all legal moves.
   */
  public MoveList getAll() {
    MoveList moveList = new MoveList();

    Attack attack = getAttack(ChessmanList.next(board.kings[board.activeColor].squares), IntColor.opposite(board.activeColor));
    if (attack.count > 0) {
      generateEvasion(moveList, attack);
    } else {
      MoveList tempList = new MoveList();

      generateMoves(tempList);

      for (int i = 0; i < tempList.size; ++i) {
        int move = tempList.moves[i];
        if (isLegal(move)) {
          moveList.moves[moveList.size++] = move;
        }
      }
    }

    return moveList;
  }

  /**
   * Returns all legal capturing moves. If we are in check, we will generate
   * evasion moves to get out of it.
   *
   * @return all legal capuring moves.
   */
  public MoveList getAllQuiescent() {
    MoveList moveList = new MoveList();

    Attack attack = getAttack(ChessmanList.next(board.kings[board.activeColor].squares), IntColor.opposite(board.activeColor));
    if (attack.count > 0) {
      generateEvasion(moveList, attack);
    } else {
      MoveList tempList = new MoveList();

      generateMoves(tempList);

      for (int i = 0; i < tempList.size; ++i) {
        int move = tempList.moves[i];
        if (isLegal(move) && Move.getTargetPiece(move) != IntPiece.NOPIECE) {
          moveList.moves[moveList.size++] = move;
        }
      }
    }

    return moveList;
  }

  private void generateMoves(MoveList list) {
    assert list != null;

    int activeColor = board.activeColor;

    for (long squares = board.pawns[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addPawnMoves(list, board.board[square], square);
    }
    for (long squares = board.knights[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(list, board.board[square], square, moveDeltaKnight);
    }
    for (long squares = board.bishops[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(list, board.board[square], square, moveDeltaBishop);
    }
    for (long squares = board.rooks[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(list, board.board[square], square, moveDeltaRook);
    }
    for (long squares = board.queens[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      addMoves(list, board.board[square], square, moveDeltaQueen);
    }
    int square = ChessmanList.next(board.kings[activeColor].squares);
    addMoves(list, board.board[square], square, moveDeltaKing);
    addCastlingMoves(list, board.board[square], square);
  }

  private void generateEvasion(MoveList list, Attack attack) {
    assert list != null;
    assert attack != null;
    assert attack.count > 0;

    int activeColor = board.activeColor;
    assert board.kings[activeColor].size() == 1;
    int kingSquare = ChessmanList.next(board.kings[activeColor].squares);
    int kingPiece = board.board[kingSquare];
    int attackerColor = IntColor.opposite(activeColor);
    int moveTemplate = Move.valueOf(Move.Type.NORMAL, kingSquare, kingSquare, kingPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);

    // Generate king moves
    for (int delta : moveDeltaKing) {
      boolean isOnCheckLine = false;
      for (int i = 0; i < attack.count; ++i) {
        if (IntChessman.isSliding(IntPiece.getChessman(board.board[attack.square[i]])) && delta == attack.delta[i]) {
          isOnCheckLine = true;
          break;
        }
      }
      if (!isOnCheckLine) {
        int targetSquare = kingSquare + delta;
        if ((targetSquare & 0x88) == 0 && !isAttacked(targetSquare, attackerColor)) {
          int targetPiece = board.board[targetSquare];
          if (targetPiece == IntPiece.NOPIECE) {
            int move = Move.setTargetSquare(moveTemplate, targetSquare);
            list.moves[list.size++] = move;
          } else {
            if (IntPiece.getColor(targetPiece) == attackerColor) {
              assert IntPiece.getChessman(targetPiece) != IntChessman.KING;
              int move = Move.setTargetSquareAndPiece(moveTemplate, targetSquare, targetPiece);
              list.moves[list.size++] = move;
            }
          }
        }
      }
    }

    // Double check
    if (attack.count >= 2) {
      return;
    }

    assert attack.count == 1;

    MoveList moves = new MoveList();

    int attackerSquare = attack.square[0];
    int attackerPiece = board.board[attackerSquare];

    // Generate all moves

    for (long squares = board.pawns[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      if (!isPinned(square, activeColor)) {
        addPawnMoves(moves, board.board[square], square);
      }
    }
    for (long squares = board.knights[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      if (!isPinned(square, activeColor)) {
        addMoves(moves, board.board[square], square, moveDeltaKnight);
      }
    }
    for (long squares = board.bishops[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      if (!isPinned(square, activeColor)) {
        addMoves(moves, board.board[square], square, moveDeltaBishop);
      }
    }
    for (long squares = board.rooks[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      if (!isPinned(square, activeColor)) {
        addMoves(moves, board.board[square], square, moveDeltaRook);
      }
    }
    for (long squares = board.queens[activeColor].squares; squares != 0; squares &= squares - 1) {
      int square = ChessmanList.next(squares);
      if (!isPinned(square, activeColor)) {
        addMoves(moves, board.board[square], square, moveDeltaQueen);
      }
    }

    // Capture the attacker
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.moves[i];

      if ((Move.getType(move) != Move.Type.ENPASSANT && Move.getTargetSquare(move) == attackerSquare)
        || (Move.getType(move) == Move.Type.ENPASSANT && Move.getTargetPiece(move) == attackerPiece)) {
        list.moves[list.size++] = move;
      }
    }

    // Interpose a chessman
    if (IntChessman.isSliding(IntPiece.getChessman(board.board[attackerSquare]))) {
      int attackDelta = attack.delta[0];
      int targetSquare = attackerSquare + attackDelta;
      while (targetSquare != kingSquare) {
        for (int i = 0; i < moves.size; ++i) {
          int move = moves.moves[i];

          if (Move.getTargetSquare(move) == targetSquare) {
            list.moves[list.size++] = move;
          }
        }

        targetSquare += attackDelta;
      }
    }
  }

  private void addMoves(MoveList list, int originPiece, int originSquare, int[] moveDelta) {
    assert list != null;
    assert IntPiece.isValid(originPiece);
    assert (originSquare & 0x88) == 0;
    assert board.board[originSquare] == originPiece;
    assert moveDelta != null;

    boolean sliding = IntChessman.isSliding(IntPiece.getChessman(originPiece));
    int oppositeColor = IntColor.opposite(IntPiece.getColor(originPiece));
    int moveTemplate = Move.valueOf(Move.Type.NORMAL, originSquare, originSquare, originPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);

    for (int delta : moveDelta) {
      int square = originSquare + delta;

      // Get moves to empty squares
      while ((square & 0x88) == 0) {
        int targetPiece = board.board[square];
        if (targetPiece == IntPiece.NOPIECE) {
          int move = Move.setTargetSquare(moveTemplate, square);
          list.moves[list.size++] = move;

          if (!sliding) {
            break;
          }

          square += delta;
        } else {
          // Get the move to the square the next chessman is standing on
          if (IntPiece.getColor(targetPiece) == oppositeColor
            && IntPiece.getChessman(targetPiece) != IntChessman.KING) {
            int move = Move.setTargetSquareAndPiece(moveTemplate, square, targetPiece);
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
    assert (pawnSquare & 0x88) == 0;
    assert board.board[pawnSquare] == pawnPiece;

    int pawnColor = IntPiece.getColor(pawnPiece);

    // Generate only capturing moves first (i = 1)
    for (int i = 1; i < moveDeltaPawn.length; ++i) {
      int delta = moveDeltaPawn[i];
      if (pawnColor == IntColor.BLACK) {
        delta *= -1;
      }

      int targetSquare = pawnSquare + delta;
      if ((targetSquare & 0x88) == 0) {
        int targetPiece = board.board[targetSquare];
        if (targetPiece != IntPiece.NOPIECE) {
          if (IntColor.opposite(IntPiece.getColor(targetPiece)) == pawnColor
            && IntPiece.getChessman(targetPiece) != IntChessman.KING) {
            // Capturing move

            if ((targetSquare >>> 4 == IntRank.R8 && pawnColor == IntColor.WHITE)
              || (targetSquare >>> 4 == IntRank.R1 && pawnColor == IntColor.BLACK)) {
              // Pawn promotion capturing move

              int moveTemplate = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.NOCHESSMAN);
              int move = Move.setPromotion(moveTemplate, IntChessman.QUEEN);
              list.moves[list.size++] = move;
              move = Move.setPromotion(moveTemplate, IntChessman.ROOK);
              list.moves[list.size++] = move;
              move = Move.setPromotion(moveTemplate, IntChessman.BISHOP);
              list.moves[list.size++] = move;
              move = Move.setPromotion(moveTemplate, IntChessman.KNIGHT);
              list.moves[list.size++] = move;
            } else {
              // Normal capturing move

              int move = Move.valueOf(Move.Type.NORMAL, pawnSquare, targetSquare, pawnPiece, targetPiece, IntChessman.NOCHESSMAN);
              list.moves[list.size++] = move;
            }
          }
        } else if (targetSquare == board.enPassant) {
          // En passant move
          assert (targetSquare >>> 4 == IntRank.R3 && pawnColor == IntColor.BLACK)
            || ((targetSquare >>> 4) == IntRank.R6 && pawnColor == IntColor.WHITE);

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
    if ((targetSquare & 0x88) == 0 && board.board[targetSquare] == IntPiece.NOPIECE) {
      if ((targetSquare >>> 4 == IntRank.R8 && pawnColor == IntColor.WHITE)
        || (targetSquare >>> 4 == IntRank.R1 && pawnColor == IntColor.BLACK)) {
        // Pawn promotion move

        int moveTemplate = Move.valueOf(Move.Type.PAWNPROMOTION, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        int move = Move.setPromotion(moveTemplate, IntChessman.QUEEN);
        list.moves[list.size++] = move;
        move = Move.setPromotion(moveTemplate, IntChessman.ROOK);
        list.moves[list.size++] = move;
        move = Move.setPromotion(moveTemplate, IntChessman.BISHOP);
        list.moves[list.size++] = move;
        move = Move.setPromotion(moveTemplate, IntChessman.KNIGHT);
        list.moves[list.size++] = move;
      } else {
        // Normal move

        int move = Move.valueOf(Move.Type.NORMAL, pawnSquare, targetSquare, pawnPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        list.moves[list.size++] = move;

        // Move another rank forward
        targetSquare += delta;
        if ((targetSquare & 0x88) == 0 && board.board[targetSquare] == IntPiece.NOPIECE) {
          if ((targetSquare >>> 4 == IntRank.R4 && pawnColor == IntColor.WHITE)
            || (targetSquare >>> 4 == IntRank.R5 && pawnColor == IntColor.BLACK)) {
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
    assert (kingSquare & 0x88) == 0;
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
    // Slow test for en passant
    if (Move.getType(move) == Move.Type.ENPASSANT) {
      board.makeMove(move);
      boolean isCheck = isCheck();
      board.undoMove(move);

      return !isCheck;
    }

    int originColor = IntPiece.getColor(Move.getOriginPiece(move));

    // Special test for king
    if (IntPiece.getChessman(Move.getOriginPiece(move)) == IntChessman.KING) {
      return !isAttacked(Move.getTargetSquare(move), IntColor.opposite(originColor));
    }

    assert board.kings[originColor].size() == 1;
    if (isPinned(Move.getOriginSquare(move), originColor)) {
      // We are pinned. Test if we move on the line.
      int kingSquare = ChessmanList.next(board.kings[originColor].squares);
      int attackDeltaOrigin = Attack.deltas[kingSquare - Move.getOriginSquare(move) + 127];
      int attackDeltaTarget = Attack.deltas[kingSquare - Move.getTargetSquare(move) + 127];
      return attackDeltaOrigin == attackDeltaTarget;
    }

    return true;
  }

  private boolean isPinned(int originSquare, int kingColor) {
    assert (originSquare & 0x88) == 0;
    assert IntColor.isValid(kingColor);

    int kingSquare = ChessmanList.next(board.kings[kingColor].squares);

    // We can only be pinned on an attack line
    int attackVector = Attack.vector[kingSquare - originSquare + 127];
    if (attackVector == Attack.N || attackVector == Attack.K) {
      // No line
      return false;
    }

    int delta = Attack.deltas[kingSquare - originSquare + 127];

    // Walk towards the king
    int square = originSquare + delta;
    assert (square & 0x88) == 0;
    while (board.board[square] == IntPiece.NOPIECE) {
      square += delta;
      assert (square & 0x88) == 0;
    }
    if (square != kingSquare) {
      // There's a blocker between me and the king
      return false;
    }

    // Walk away from the king
    square = originSquare - delta;
    while ((square & 0x88) == 0) {
      int attacker = board.board[square];
      if (attacker != IntPiece.NOPIECE) {
        int attackerColor = IntPiece.getColor(attacker);

        return kingColor != attackerColor && canSliderPseudoAttack(attacker, square, kingSquare);
      } else {
        square -= delta;
      }
    }

    return false;
  }

  private boolean isAttacked(int targetSquare, int attackerColor) {
    assert (targetSquare & 0x88) == 0;
    assert IntColor.isValid(attackerColor);

    return getAttack(targetSquare, attackerColor).count > 0;
  }

  private Attack getAttack(int targetSquare, int attackerColor) {
    assert (targetSquare & 0x88) == 0;
    assert IntColor.isValid(attackerColor);

    Attack attack = new Attack();

    // Pawn attacks
    int pawnPiece = IntPiece.WHITEPAWN;
    int sign = -1;
    if (attackerColor == IntColor.BLACK) {
      pawnPiece = IntPiece.BLACKPAWN;
      sign = 1;
    } else {
      assert attackerColor == IntColor.WHITE;
    }
    int pawnAttackerSquare = targetSquare + sign * 15;
    if ((pawnAttackerSquare & 0x88) == 0) {
      int pawn = board.board[pawnAttackerSquare];
      if (pawn != IntPiece.NOPIECE && pawn == pawnPiece) {
        assert Attack.deltas[targetSquare - pawnAttackerSquare + 127] == sign * -15;
        attack.square[attack.count] = pawnAttackerSquare;
        attack.delta[attack.count] = sign * -15;
        ++attack.count;
      }
    }
    pawnAttackerSquare = targetSquare + sign * 17;
    if ((pawnAttackerSquare & 0x88) == 0) {
      int pawn = board.board[pawnAttackerSquare];
      if (pawn != IntPiece.NOPIECE && pawn == pawnPiece) {
        assert Attack.deltas[targetSquare - pawnAttackerSquare + 127] == sign * -17;
        attack.square[attack.count] = pawnAttackerSquare;
        attack.delta[attack.count] = sign * -17;
        ++attack.count;
      }
    }
    for (long squares = board.knights[attackerColor].squares; squares != 0; squares &= squares - 1) {
      int attackerSquare = ChessmanList.next(squares);
      assert IntPiece.getChessman(board.board[attackerSquare]) == IntChessman.KNIGHT;
      assert attackerSquare != Square.NOSQUARE;
      assert board.board[attackerSquare] != IntPiece.NOPIECE;
      assert attackerColor == IntPiece.getColor(board.board[attackerSquare]);
      if (canAttack(IntChessman.KNIGHT, attackerColor, attackerSquare, targetSquare)) {
        int attackDelta = Attack.deltas[targetSquare - attackerSquare + 127];
        assert attackDelta != 0;
        attack.square[attack.count] = attackerSquare;
        attack.delta[attack.count] = attackDelta;
        ++attack.count;
      }
    }
    for (long squares = board.bishops[attackerColor].squares; squares != 0; squares &= squares - 1) {
      int attackerSquare = ChessmanList.next(squares);
      assert IntPiece.getChessman(board.board[attackerSquare]) == IntChessman.BISHOP;
      assert attackerSquare != Square.NOSQUARE;
      assert board.board[attackerSquare] != IntPiece.NOPIECE;
      assert attackerColor == IntPiece.getColor(board.board[attackerSquare]);
      if (canAttack(IntChessman.BISHOP, attackerColor, attackerSquare, targetSquare)) {
        int attackDelta = Attack.deltas[targetSquare - attackerSquare + 127];
        assert attackDelta != 0;
        attack.square[attack.count] = attackerSquare;
        attack.delta[attack.count] = attackDelta;
        ++attack.count;
      }
    }
    for (long squares = board.rooks[attackerColor].squares; squares != 0; squares &= squares - 1) {
      int attackerSquare = ChessmanList.next(squares);
      assert IntPiece.getChessman(board.board[attackerSquare]) == IntChessman.ROOK;
      assert attackerSquare != Square.NOSQUARE;
      assert board.board[attackerSquare] != IntPiece.NOPIECE;
      assert attackerColor == IntPiece.getColor(board.board[attackerSquare]);
      if (canAttack(IntChessman.ROOK, attackerColor, attackerSquare, targetSquare)) {
        int attackDelta = Attack.deltas[targetSquare - attackerSquare + 127];
        assert attackDelta != 0;
        attack.square[attack.count] = attackerSquare;
        attack.delta[attack.count] = attackDelta;
        ++attack.count;
      }
    }
    for (long squares = board.queens[attackerColor].squares; squares != 0; squares &= squares - 1) {
      int attackerSquare = ChessmanList.next(squares);
      assert IntPiece.getChessman(board.board[attackerSquare]) == IntChessman.QUEEN;
      assert attackerSquare != Square.NOSQUARE;
      assert board.board[attackerSquare] != IntPiece.NOPIECE;
      assert attackerColor == IntPiece.getColor(board.board[attackerSquare]);
      if (canAttack(IntChessman.QUEEN, attackerColor, attackerSquare, targetSquare)) {
        int attackDelta = Attack.deltas[targetSquare - attackerSquare + 127];
        assert attackDelta != 0;
        attack.square[attack.count] = attackerSquare;
        attack.delta[attack.count] = attackDelta;
        ++attack.count;
      }
    }
    assert board.kings[attackerColor].size() == 1;
    int attackerSquare = ChessmanList.next(board.kings[attackerColor].squares);
    assert IntPiece.getChessman(board.board[attackerSquare]) == IntChessman.KING;
    assert attackerSquare != Square.NOSQUARE;
    assert board.board[attackerSquare] != IntPiece.NOPIECE;
    assert attackerColor == IntPiece.getColor(board.board[attackerSquare]);
    if (canAttack(IntChessman.KING, attackerColor, attackerSquare, targetSquare)) {
      int attackDelta = Attack.deltas[targetSquare - attackerSquare + 127];
      assert attackDelta != 0;
      attack.square[attack.count] = attackerSquare;
      attack.delta[attack.count] = attackDelta;
      ++attack.count;
    }

    return attack;
  }

  private boolean canAttack(int attackerChessman, int attackerColor, int attackerSquare, int targetSquare) {
    assert IntChessman.isValid(attackerChessman);
    assert IntColor.isValid(attackerColor);
    assert (attackerSquare & 0x88) == 0;
    assert (targetSquare & 0x88) == 0;

    int attackVector = Attack.vector[targetSquare - attackerSquare + 127];

    switch (attackerChessman) {
      case IntChessman.PAWN:
        if (attackVector == Attack.u && attackerColor == IntColor.WHITE) {
          return true;
        } else if (attackVector == Attack.d && attackerColor == IntColor.BLACK) {
          return true;
        }
        break;
      case IntChessman.KNIGHT:
        if (attackVector == Attack.K) {
          return true;
        }
        break;
      case IntChessman.BISHOP:
        switch (attackVector) {
          case Attack.u:
          case Attack.d:
            return true;
          case Attack.D:
            if (canSliderAttack(attackerSquare, targetSquare)) {
              return true;
            }
            break;
          default:
            break;
        }
        break;
      case IntChessman.ROOK:
        switch (attackVector) {
          case Attack.s:
            return true;
          case Attack.S:
            if (canSliderAttack(attackerSquare, targetSquare)) {
              return true;
            }
            break;
          default:
            break;
        }
        break;
      case IntChessman.QUEEN:
        switch (attackVector) {
          case Attack.u:
          case Attack.d:
          case Attack.s:
            return true;
          case Attack.D:
          case Attack.S:
            if (canSliderAttack(attackerSquare, targetSquare)) {
              return true;
            }
            break;
          default:
            break;
        }
        break;
      case IntChessman.KING:
        switch (attackVector) {
          case Attack.u:
          case Attack.d:
          case Attack.s:
            return true;
          default:
            break;
        }
        break;
      default:
        assert false : attackerChessman;
        break;
    }

    return false;
  }

  private boolean canSliderAttack(int attackerSquare, int targetSquare) {
    assert (attackerSquare & 0x88) == 0;
    assert (targetSquare & 0x88) == 0;

    int attackDelta = Attack.deltas[targetSquare - attackerSquare + 127];

    int square = attackerSquare + attackDelta;
    while ((square & 0x88) == 0 && square != targetSquare && board.board[square] == IntPiece.NOPIECE) {
      square += attackDelta;
    }

    return square == targetSquare;
  }

  private boolean canSliderPseudoAttack(int attacker, int attackerSquare, int targetSquare) {
    assert IntPiece.isValid(attacker);
    assert (attackerSquare & 0x88) == 0;
    assert (targetSquare & 0x88) == 0;

    int attackVector;

    switch (IntPiece.getChessman(attacker)) {
      case IntChessman.PAWN:
        break;
      case IntChessman.KNIGHT:
        break;
      case IntChessman.BISHOP:
        attackVector = Attack.vector[targetSquare - attackerSquare + 127];
        switch (attackVector) {
          case Attack.u:
          case Attack.d:
          case Attack.D:
            return true;
          default:
            break;
        }
        break;
      case IntChessman.ROOK:
        attackVector = Attack.vector[targetSquare - attackerSquare + 127];
        switch (attackVector) {
          case Attack.s:
          case Attack.S:
            return true;
          default:
            break;
        }
        break;
      case IntChessman.QUEEN:
        attackVector = Attack.vector[targetSquare - attackerSquare + 127];
        switch (attackVector) {
          case Attack.u:
          case Attack.d:
          case Attack.s:
          case Attack.D:
          case Attack.S:
            return true;
          default:
            break;
        }
        break;
      case IntChessman.KING:
        break;
      default:
        assert false : IntPiece.getChessman(attacker);
        break;
    }

    return false;
  }

}
