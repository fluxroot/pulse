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
    public final int[] position = new int[MAXATTACK];
  }

  public MoveGenerator(Board board) {
    assert board != null;

    this.board = board;
  }

  public boolean isCheck() {
    // Check whether our king is attacked by any opponent piece
    return isAttacked(ChessmanList.next(board.kings[board.activeColor].positions), IntColor.opposite(board.activeColor));
  }

  /**
   * Returns all legal moves. If we are in check, we will generate evasion
   * moves to get out of it.
   *
   * @return all legal moves.
   */
  public MoveList getAll() {
    MoveList moveList = new MoveList();

    Attack attack = getAttack(ChessmanList.next(board.kings[board.activeColor].positions), IntColor.opposite(board.activeColor));
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

    Attack attack = getAttack(ChessmanList.next(board.kings[board.activeColor].positions), IntColor.opposite(board.activeColor));
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

    for (long positions = board.pawns[activeColor].positions; positions != 0; positions &= positions - 1) {
      int position = ChessmanList.next(positions);
      addPawnMoves(list, board.board[position], position);
    }
    for (long positions = board.knights[activeColor].positions; positions != 0; positions &= positions - 1) {
      int position = ChessmanList.next(positions);
      addMoves(list, board.board[position], position, moveDeltaKnight);
    }
    for (long positions = board.bishops[activeColor].positions; positions != 0; positions &= positions - 1) {
      int position = ChessmanList.next(positions);
      addMoves(list, board.board[position], position, moveDeltaBishop);
    }
    for (long positions = board.rooks[activeColor].positions; positions != 0; positions &= positions - 1) {
      int position = ChessmanList.next(positions);
      addMoves(list, board.board[position], position, moveDeltaRook);
    }
    for (long positions = board.queens[activeColor].positions; positions != 0; positions &= positions - 1) {
      int position = ChessmanList.next(positions);
      addMoves(list, board.board[position], position, moveDeltaQueen);
    }
    int position = ChessmanList.next(board.kings[activeColor].positions);
    addMoves(list, board.board[position], position, moveDeltaKing);
    addCastlingMoves(list, board.board[position], position);
  }

  private void generateEvasion(MoveList list, Attack attack) {
    assert list != null;
    assert attack != null;
    assert attack.count > 0;

    int activeColor = board.activeColor;
    assert board.kings[activeColor].size() == 1;
    int kingPosition = ChessmanList.next(board.kings[activeColor].positions);
    int king = board.board[kingPosition];
    int attackerColor = IntColor.opposite(activeColor);
    int oppositeColor = IntColor.opposite(IntPiece.getColor(king));
    int moveTemplate = Move.valueOf(Move.Type.NORMAL, kingPosition, kingPosition, king, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);

    // Generate king moves
    for (int delta : moveDeltaKing) {
      boolean isOnCheckLine = false;
      for (int i = 0; i < attack.count; ++i) {
        if (IntChessman.isSliding(IntPiece.getChessman(board.board[attack.position[i]])) && delta == attack.delta[i]) {
          isOnCheckLine = true;
          break;
        }
      }
      if (!isOnCheckLine) {
        int targetPosition = kingPosition + delta;
        if ((targetPosition & 0x88) == 0 && !isAttacked(targetPosition, attackerColor)) {
          int targetPiece = board.board[targetPosition];
          if (targetPiece == IntPiece.NOPIECE) {
            int move = Move.setTargetPosition(moveTemplate, targetPosition);
            list.moves[list.size++] = move;
          } else {
            if (IntPiece.getColor(targetPiece) == oppositeColor) {
              assert IntPiece.getChessman(targetPiece) != IntChessman.KING;
              int move = Move.setTargetPositionAndPiece(moveTemplate, targetPosition, targetPiece);
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

    int attackerPosition = attack.position[0];
    int attacker = board.board[attackerPosition];

    // Generate all moves

    for (long positions = board.pawns[activeColor].positions; positions != 0; positions &= positions - 1) {
      int position = ChessmanList.next(positions);
      if (!isPinned(position, activeColor)) {
        addPawnMoves(moves, board.board[position], position);
      }
    }
    for (long positions = board.knights[activeColor].positions; positions != 0; positions &= positions - 1) {
      int position = ChessmanList.next(positions);
      if (!isPinned(position, activeColor)) {
        addMoves(moves, board.board[position], position, moveDeltaKnight);
      }
    }
    for (long positions = board.bishops[activeColor].positions; positions != 0; positions &= positions - 1) {
      int position = ChessmanList.next(positions);
      if (!isPinned(position, activeColor)) {
        addMoves(moves, board.board[position], position, moveDeltaBishop);
      }
    }
    for (long positions = board.rooks[activeColor].positions; positions != 0; positions &= positions - 1) {
      int position = ChessmanList.next(positions);
      if (!isPinned(position, activeColor)) {
        addMoves(moves, board.board[position], position, moveDeltaRook);
      }
    }
    for (long positions = board.queens[activeColor].positions; positions != 0; positions &= positions - 1) {
      int position = ChessmanList.next(positions);
      if (!isPinned(position, activeColor)) {
        addMoves(moves, board.board[position], position, moveDeltaQueen);
      }
    }

    // Capture the attacker
    for (int i = 0; i < moves.size; ++i) {
      int move = moves.moves[i];

      if ((Move.getType(move) != Move.Type.ENPASSANT && Move.getTargetPosition(move) == attackerPosition)
        || (Move.getType(move) == Move.Type.ENPASSANT && Move.getTargetPiece(move) == attacker)) {
        list.moves[list.size++] = move;
      }
    }

    // Interpose a chessman
    if (IntChessman.isSliding(IntPiece.getChessman(board.board[attackerPosition]))) {
      int attackDelta = attack.delta[0];
      int targetPosition = attackerPosition + attackDelta;
      while (targetPosition != kingPosition) {
        for (int i = 0; i < moves.size; ++i) {
          int move = moves.moves[i];

          if (Move.getTargetPosition(move) == targetPosition) {
            list.moves[list.size++] = move;
          }
        }

        targetPosition += attackDelta;
      }
    }
  }

  private void addMoves(MoveList list, int originPiece, int originPosition, int[] moveDelta) {
    assert originPiece != IntPiece.NOPIECE;
    assert moveDelta != null;
    assert list != null;

    boolean sliding = IntChessman.isSliding(IntPiece.getChessman(originPiece));
    int oppositeColor = IntColor.opposite(IntPiece.getColor(originPiece));
    int moveTemplate = Move.valueOf(Move.Type.NORMAL, originPosition, originPosition, originPiece, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);

    for (int delta : moveDelta) {
      int position = originPosition + delta;

      // Get moves to empty position
      while ((position & 0x88) == 0) {
        int targetPiece = board.board[position];
        if (targetPiece == IntPiece.NOPIECE) {
          int move = Move.setTargetPosition(moveTemplate, position);
          list.moves[list.size++] = move;

          if (!sliding) {
            break;
          }

          position += delta;
        } else {
          // Get the move to the position the next chessman is standing on
          if (IntPiece.getColor(targetPiece) == oppositeColor
            && IntPiece.getChessman(targetPiece) != IntChessman.KING) {
            int move = Move.setTargetPositionAndPiece(moveTemplate, position, targetPiece);
            list.moves[list.size++] = move;
          }
          break;
        }
      }
    }
  }

  private void addPawnMoves(MoveList list, int pawn, int pawnPosition) {
    assert list != null;
    assert IntPiece.getChessman(pawn) == IntChessman.PAWN;
    assert (pawnPosition & 0x88) == 0;
    assert board.board[pawnPosition] == pawn;

    int pawnColor = IntPiece.getColor(pawn);

    // Generate only capturing moves first (i = 1)
    for (int i = 1; i < moveDeltaPawn.length; ++i) {
      int delta = moveDeltaPawn[i];
      if (pawnColor == IntColor.BLACK) {
        delta *= -1;
      }

      int targetPosition = pawnPosition + delta;
      if ((targetPosition & 0x88) == 0) {
        int targetPiece = board.board[targetPosition];
        if (targetPiece != IntPiece.NOPIECE) {
          if (IntColor.opposite(IntPiece.getColor(targetPiece)) == pawnColor
            && IntPiece.getChessman(targetPiece) != IntChessman.KING) {
            // Capturing move

            if ((targetPosition >>> 4 == IntRank.R8 && pawnColor == IntColor.WHITE)
              || (targetPosition >>> 4 == IntRank.R1 && pawnColor == IntColor.BLACK)) {
              // Pawn promotion capturing move

              int moveTemplate = Move.valueOf(Move.Type.PAWNPROMOTION, pawnPosition, targetPosition, pawn, targetPiece, IntChessman.NOCHESSMAN);
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

              int move = Move.valueOf(Move.Type.NORMAL, pawnPosition, targetPosition, pawn, targetPiece, IntChessman.NOCHESSMAN);
              list.moves[list.size++] = move;
            }
          }
        } else if (targetPosition == board.enPassant) {
          // En passant move
          assert (targetPosition >>> 4 == IntRank.R3 && pawnColor == IntColor.BLACK)
            || ((targetPosition >>> 4) == IntRank.R6 && pawnColor == IntColor.WHITE);

          int capturePosition;
          if (pawnColor == IntColor.WHITE) {
            capturePosition = targetPosition - 16;
          } else {
            capturePosition = targetPosition + 16;
          }
          targetPiece = board.board[capturePosition];
          assert IntPiece.getChessman(targetPiece) == IntChessman.PAWN;
          assert IntPiece.getColor(targetPiece) == IntColor.opposite(pawnColor);

          int move = Move.valueOf(Move.Type.ENPASSANT, pawnPosition, targetPosition, pawn, targetPiece, IntChessman.NOCHESSMAN);
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
    int targetPosition = pawnPosition + delta;
    if ((targetPosition & 0x88) == 0 && board.board[targetPosition] == IntPiece.NOPIECE) {
      if ((targetPosition >>> 4 == IntRank.R8 && pawnColor == IntColor.WHITE)
        || (targetPosition >>> 4 == IntRank.R1 && pawnColor == IntColor.BLACK)) {
        // Pawn promotion move

        int moveTemplate = Move.valueOf(Move.Type.PAWNPROMOTION, pawnPosition, targetPosition, pawn, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
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

        int move = Move.valueOf(Move.Type.NORMAL, pawnPosition, targetPosition, pawn, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        list.moves[list.size++] = move;

        // Move another rank forward
        targetPosition += delta;
        if ((targetPosition & 0x88) == 0 && board.board[targetPosition] == IntPiece.NOPIECE) {
          if ((targetPosition >>> 4 == IntRank.R4 && pawnColor == IntColor.WHITE)
            || (targetPosition >>> 4 == IntRank.R5 && pawnColor == IntColor.BLACK)) {
            // Pawn double move

            move = Move.valueOf(Move.Type.PAWNDOUBLE, pawnPosition, targetPosition, pawn, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
            list.moves[list.size++] = move;
          }
        }
      }
    }
  }

  private void addCastlingMoves(MoveList list, int king, int kingPosition) {
    assert king != IntPiece.NOPIECE;
    assert (kingPosition & 0x88) == 0;
    assert list != null;

    int color = IntPiece.getColor(king);
    if (color == IntColor.WHITE) {
      // Do not test g1 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.WHITE][IntCastling.KINGSIDE] != IntFile.NOFILE
        && board.board[Position.f1] == IntPiece.NOPIECE
        && board.board[Position.g1] == IntPiece.NOPIECE
        && !isAttacked(Position.f1, IntColor.BLACK)) {
        assert board.board[Position.e1] == IntPiece.WHITEKING;
        assert board.board[Position.h1] == IntPiece.WHITEROOK;

        int move = Move.valueOf(Move.Type.CASTLING, kingPosition, Position.g1, king, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        list.moves[list.size++] = move;
      }
      // Do not test c1 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.WHITE][IntCastling.QUEENSIDE] != IntFile.NOFILE
        && board.board[Position.b1] == IntPiece.NOPIECE
        && board.board[Position.c1] == IntPiece.NOPIECE
        && board.board[Position.d1] == IntPiece.NOPIECE
        && !isAttacked(Position.d1, IntColor.BLACK)) {
        assert board.board[Position.e1] == IntPiece.WHITEKING;
        assert board.board[Position.a1] == IntPiece.WHITEROOK;

        int move = Move.valueOf(Move.Type.CASTLING, kingPosition, Position.c1, king, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        list.moves[list.size++] = move;
      }
    } else {
      // Do not test g8 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.BLACK][IntCastling.KINGSIDE] != IntFile.NOFILE
        && board.board[Position.f8] == IntPiece.NOPIECE
        && board.board[Position.g8] == IntPiece.NOPIECE
        && !isAttacked(Position.f8, IntColor.WHITE)) {
        assert board.board[Position.e8] == IntPiece.BLACKKING;
        assert board.board[Position.h8] == IntPiece.BLACKROOK;

        int move = Move.valueOf(Move.Type.CASTLING, kingPosition, Position.g8, king, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
        list.moves[list.size++] = move;
      }
      // Do not test c8 whether it is attacked as we will test it in isLegal()
      if (board.castling[IntColor.BLACK][IntCastling.QUEENSIDE] != IntFile.NOFILE
        && board.board[Position.b8] == IntPiece.NOPIECE
        && board.board[Position.c8] == IntPiece.NOPIECE
        && board.board[Position.d8] == IntPiece.NOPIECE
        && !isAttacked(Position.d8, IntColor.WHITE)) {
        assert board.board[Position.e8] == IntPiece.BLACKKING;
        assert board.board[Position.a8] == IntPiece.BLACKROOK;

        int move = Move.valueOf(Move.Type.CASTLING, kingPosition, Position.c8, king, IntPiece.NOPIECE, IntChessman.NOCHESSMAN);
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

    int chessman = Move.getOriginPiece(move);
    int chessmanColor = IntPiece.getColor(chessman);

    // Special test for king
    if (IntPiece.getChessman(chessman) == IntChessman.KING) {
      return !isAttacked(Move.getTargetPosition(move), IntColor.opposite(chessmanColor));
    }

    assert board.kings[chessmanColor].size() == 1;
    if (isPinned(Move.getOriginPosition(move), chessmanColor)) {
      // We are pinned. Test if we move on the line.
      int kingPosition = ChessmanList.next(board.kings[chessmanColor].positions);
      int attackDeltaOrigin = Attack.deltas[kingPosition - Move.getOriginPosition(move) + 127];
      int attackDeltaTarget = Attack.deltas[kingPosition - Move.getTargetPosition(move) + 127];
      return attackDeltaOrigin == attackDeltaTarget;
    }

    return true;
  }

  private boolean isPinned(int chessmanPosition, int kingColor) {
    assert chessmanPosition != Position.NOPOSITION;
    assert kingColor != IntColor.NOCOLOR;

    int myKingPosition = ChessmanList.next(board.kings[kingColor].positions);

    // We can only be pinned on an attack line
    int vector = Attack.vector[myKingPosition - chessmanPosition + 127];
    if (vector == Attack.N || vector == Attack.K) {
      // No line
      return false;
    }

    int delta = Attack.deltas[myKingPosition - chessmanPosition + 127];

    // Walk towards the king
    int targetPosition = chessmanPosition + delta;
    assert (targetPosition & 0x88) == 0;
    while (board.board[targetPosition] == IntPiece.NOPIECE) {
      targetPosition += delta;
      assert (targetPosition & 0x88) == 0;
    }
    if (targetPosition != myKingPosition) {
      // There's a blocker between me and the king
      return false;
    }

    // Walk away from the king
    targetPosition = chessmanPosition - delta;
    while ((targetPosition & 0x88) == 0) {
      int attacker = board.board[targetPosition];
      if (attacker != IntPiece.NOPIECE) {
        int attackerColor = IntPiece.getColor(attacker);

        return kingColor != attackerColor && canSliderPseudoAttack(attacker, targetPosition, myKingPosition);
      } else {
        targetPosition -= delta;
      }
    }

    return false;
  }

  private boolean isAttacked(int targetPosition, int attackerColor) {
    assert (targetPosition & 0x88) == 0;
    assert attackerColor != IntColor.NOCOLOR;

    return getAttack(targetPosition, attackerColor).count > 0;
  }

  private Attack getAttack(int targetPosition, int attackerColor) {
    assert targetPosition != Position.NOPOSITION;
    assert attackerColor != IntColor.NOCOLOR;

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
    int pawnAttackerPosition = targetPosition + sign * 15;
    if ((pawnAttackerPosition & 0x88) == 0) {
      int pawn = board.board[pawnAttackerPosition];
      if (pawn != IntPiece.NOPIECE && pawn == pawnPiece) {
        assert Attack.deltas[targetPosition - pawnAttackerPosition + 127] == sign * -15;
        attack.position[attack.count] = pawnAttackerPosition;
        attack.delta[attack.count] = sign * -15;
        ++attack.count;
      }
    }
    pawnAttackerPosition = targetPosition + sign * 17;
    if ((pawnAttackerPosition & 0x88) == 0) {
      int pawn = board.board[pawnAttackerPosition];
      if (pawn != IntPiece.NOPIECE && pawn == pawnPiece) {
        assert Attack.deltas[targetPosition - pawnAttackerPosition + 127] == sign * -17;
        attack.position[attack.count] = pawnAttackerPosition;
        attack.delta[attack.count] = sign * -17;
        ++attack.count;
      }
    }
    for (long positions = board.knights[attackerColor].positions; positions != 0; positions &= positions - 1) {
      int attackerPosition = ChessmanList.next(positions);
      assert IntPiece.getChessman(board.board[attackerPosition]) == IntChessman.KNIGHT;
      assert attackerPosition != Position.NOPOSITION;
      assert board.board[attackerPosition] != IntPiece.NOPIECE;
      assert attackerColor == IntPiece.getColor(board.board[attackerPosition]);
      if (canAttack(IntChessman.KNIGHT, attackerColor, attackerPosition, targetPosition)) {
        int attackDelta = Attack.deltas[targetPosition - attackerPosition + 127];
        assert attackDelta != 0;
        attack.position[attack.count] = attackerPosition;
        attack.delta[attack.count] = attackDelta;
        ++attack.count;
      }
    }
    for (long positions = board.bishops[attackerColor].positions; positions != 0; positions &= positions - 1) {
      int attackerPosition = ChessmanList.next(positions);
      assert IntPiece.getChessman(board.board[attackerPosition]) == IntChessman.BISHOP;
      assert attackerPosition != Position.NOPOSITION;
      assert board.board[attackerPosition] != IntPiece.NOPIECE;
      assert attackerColor == IntPiece.getColor(board.board[attackerPosition]);
      if (canAttack(IntChessman.BISHOP, attackerColor, attackerPosition, targetPosition)) {
        int attackDelta = Attack.deltas[targetPosition - attackerPosition + 127];
        assert attackDelta != 0;
        attack.position[attack.count] = attackerPosition;
        attack.delta[attack.count] = attackDelta;
        ++attack.count;
      }
    }
    for (long positions = board.rooks[attackerColor].positions; positions != 0; positions &= positions - 1) {
      int attackerPosition = ChessmanList.next(positions);
      assert IntPiece.getChessman(board.board[attackerPosition]) == IntChessman.ROOK;
      assert attackerPosition != Position.NOPOSITION;
      assert board.board[attackerPosition] != IntPiece.NOPIECE;
      assert attackerColor == IntPiece.getColor(board.board[attackerPosition]);
      if (canAttack(IntChessman.ROOK, attackerColor, attackerPosition, targetPosition)) {
        int attackDelta = Attack.deltas[targetPosition - attackerPosition + 127];
        assert attackDelta != 0;
        attack.position[attack.count] = attackerPosition;
        attack.delta[attack.count] = attackDelta;
        ++attack.count;
      }
    }
    for (long positions = board.queens[attackerColor].positions; positions != 0; positions &= positions - 1) {
      int attackerPosition = ChessmanList.next(positions);
      assert IntPiece.getChessman(board.board[attackerPosition]) == IntChessman.QUEEN;
      assert attackerPosition != Position.NOPOSITION;
      assert board.board[attackerPosition] != IntPiece.NOPIECE;
      assert attackerColor == IntPiece.getColor(board.board[attackerPosition]);
      if (canAttack(IntChessman.QUEEN, attackerColor, attackerPosition, targetPosition)) {
        int attackDelta = Attack.deltas[targetPosition - attackerPosition + 127];
        assert attackDelta != 0;
        attack.position[attack.count] = attackerPosition;
        attack.delta[attack.count] = attackDelta;
        ++attack.count;
      }
    }
    assert board.kings[attackerColor].size() == 1;
    int attackerPosition = ChessmanList.next(board.kings[attackerColor].positions);
    assert IntPiece.getChessman(board.board[attackerPosition]) == IntChessman.KING;
    assert attackerPosition != Position.NOPOSITION;
    assert board.board[attackerPosition] != IntPiece.NOPIECE;
    assert attackerColor == IntPiece.getColor(board.board[attackerPosition]);
    if (canAttack(IntChessman.KING, attackerColor, attackerPosition, targetPosition)) {
      int attackDelta = Attack.deltas[targetPosition - attackerPosition + 127];
      assert attackDelta != 0;
      attack.position[attack.count] = attackerPosition;
      attack.delta[attack.count] = attackDelta;
      ++attack.count;
    }

    return attack;
  }

  private boolean canAttack(int attackerChessman, int attackerColor, int attackerPosition, int targetPosition) {
    assert attackerChessman != IntChessman.NOCHESSMAN;
    assert attackerColor != IntColor.NOCOLOR;
    assert (attackerPosition & 0x88) == 0;
    assert (targetPosition & 0x88) == 0;

    int attackVector = Attack.vector[targetPosition - attackerPosition + 127];

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
            if (canSliderAttack(attackerPosition, targetPosition)) {
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
            if (canSliderAttack(attackerPosition, targetPosition)) {
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
            if (canSliderAttack(attackerPosition, targetPosition)) {
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

  private boolean canSliderAttack(int attackerPosition, int targetPosition) {
    assert (attackerPosition & 0x88) == 0;
    assert (targetPosition & 0x88) == 0;

    int attackDelta = Attack.deltas[targetPosition - attackerPosition + 127];

    int position = attackerPosition + attackDelta;
    while ((position & 0x88) == 0 && position != targetPosition && board.board[position] == IntPiece.NOPIECE) {
      position += attackDelta;
    }

    return position == targetPosition;
  }

  private boolean canSliderPseudoAttack(int attacker, int attackerPosition, int targetPosition) {
    assert attacker != IntPiece.NOPIECE;
    assert (attackerPosition & 0x88) == 0;
    assert (targetPosition & 0x88) == 0;

    int attackVector;

    switch (IntPiece.getChessman(attacker)) {
      case IntChessman.PAWN:
        break;
      case IntChessman.KNIGHT:
        break;
      case IntChessman.BISHOP:
        attackVector = Attack.vector[targetPosition - attackerPosition + 127];
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
        attackVector = Attack.vector[targetPosition - attackerPosition + 127];
        switch (attackVector) {
          case Attack.s:
          case Attack.S:
            return true;
          default:
            break;
        }
        break;
      case IntChessman.QUEEN:
        attackVector = Attack.vector[targetPosition - attackerPosition + 127];
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
