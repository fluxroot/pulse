/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.*;

final class Notation {

  static final String STANDARDPOSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

  private Notation() {
  }

  static Position toPosition(String fen) {
    try {
      return toPosition(new GenericBoard(fen));
    } catch (IllegalNotationException e) {
      throw new IllegalArgumentException(e);
    }
  }

  static String fromPosition(Position position) {
    return toGenericBoard(position).toString();
  }

  static Position toPosition(GenericBoard genericBoard) {
    assert genericBoard != null;

    Position newPosition = new Position();

    // Initialize board
    for (int square : Square.values) {
      GenericPiece genericPiece = genericBoard.getPiece(fromSquare(square));
      if (genericPiece != null) {
        int piece = toPiece(genericPiece);
        newPosition.put(piece, square);
      }
    }

    // Initialize active color
    newPosition.setActiveColor(toColor(genericBoard.getActiveColor()));

    // Initialize castling
    for (int color : Color.values) {
      for (int castlingtype : CastlingType.values) {
        GenericFile genericFile = genericBoard.getCastling(
            fromColor(color), fromCastlingType(castlingtype)
        );
        if (genericFile != null) {
          newPosition.setCastlingRight(Castling.valueOf(color, castlingtype));
        }
      }
    }

    // Initialize en passant
    if (genericBoard.getEnPassant() != null) {
      newPosition.setEnPassantSquare(toSquare(genericBoard.getEnPassant()));
    }

    // Initialize half move clock
    newPosition.setHalfmoveClock(genericBoard.getHalfMoveClock());

    // Initialize the full move number
    newPosition.setFullmoveNumber(genericBoard.getFullMoveNumber());

    return newPosition;
  }

  static GenericBoard toGenericBoard(Position position) {
    GenericBoard genericBoard = new GenericBoard();

    // Set board
    for (int square : Square.values) {
      if (position.board[square] != Piece.NOPIECE) {
        genericBoard.setPiece(fromPiece(position.board[square]), fromSquare(square));
      }
    }

    // Set castling
    if ((position.castlingRights & Castling.WHITE_KINGSIDE) != Castling.NOCASTLING) {
      genericBoard.setCastling(
          fromColor(Color.WHITE), fromCastlingType(CastlingType.KINGSIDE),
          fromFile(File.h)
      );
    }
    if ((position.castlingRights & Castling.WHITE_QUEENSIDE) != Castling.NOCASTLING) {
      genericBoard.setCastling(
          fromColor(Color.WHITE), fromCastlingType(CastlingType.QUEENSIDE),
          fromFile(File.a)
      );
    }
    if ((position.castlingRights & Castling.BLACK_KINGSIDE) != Castling.NOCASTLING) {
      genericBoard.setCastling(
          fromColor(Color.BLACK), fromCastlingType(CastlingType.KINGSIDE),
          fromFile(File.h)
      );
    }
    if ((position.castlingRights & Castling.BLACK_QUEENSIDE) != Castling.NOCASTLING) {
      genericBoard.setCastling(
          fromColor(Color.BLACK), fromCastlingType(CastlingType.QUEENSIDE),
          fromFile(File.a)
      );
    }

    // Set en passant
    if (position.enPassantSquare != Square.NOSQUARE) {
      genericBoard.setEnPassant(fromSquare(position.enPassantSquare));
    }

    // Set active color
    genericBoard.setActiveColor(fromColor(position.activeColor));

    // Set half move clock
    genericBoard.setHalfMoveClock(position.halfmoveClock);

    // Set full move number
    genericBoard.setFullMoveNumber(position.getFullmoveNumber());

    return genericBoard;
  }

  static int toColor(GenericColor genericColor) {
    assert genericColor != null;

    switch (genericColor) {
      case WHITE:
        return Color.WHITE;
      case BLACK:
        return Color.BLACK;
      default:
        throw new IllegalArgumentException();
    }
  }

  static GenericColor fromColor(int color) {
    switch (color) {
      case Color.WHITE:
        return GenericColor.WHITE;
      case Color.BLACK:
        return GenericColor.BLACK;
      case Color.NOCOLOR:
      default:
        throw new IllegalArgumentException();
    }
  }

  static GenericChessman fromPieceType(int piecetype) {
    switch (piecetype) {
      case PieceType.PAWN:
        return GenericChessman.PAWN;
      case PieceType.KNIGHT:
        return GenericChessman.KNIGHT;
      case PieceType.BISHOP:
        return GenericChessman.BISHOP;
      case PieceType.ROOK:
        return GenericChessman.ROOK;
      case PieceType.QUEEN:
        return GenericChessman.QUEEN;
      case PieceType.KING:
        return GenericChessman.KING;
      case PieceType.NOPIECETYPE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static int toPiece(GenericPiece genericPiece) {
    assert genericPiece != null;

    switch (genericPiece) {
      case WHITEPAWN:
        return Piece.WHITE_PAWN;
      case WHITEKNIGHT:
        return Piece.WHITE_KNIGHT;
      case WHITEBISHOP:
        return Piece.WHITE_BISHOP;
      case WHITEROOK:
        return Piece.WHITE_ROOK;
      case WHITEQUEEN:
        return Piece.WHITE_QUEEN;
      case WHITEKING:
        return Piece.WHITE_KING;
      case BLACKPAWN:
        return Piece.BLACK_PAWN;
      case BLACKKNIGHT:
        return Piece.BLACK_KNIGHT;
      case BLACKBISHOP:
        return Piece.BLACK_BISHOP;
      case BLACKROOK:
        return Piece.BLACK_ROOK;
      case BLACKQUEEN:
        return Piece.BLACK_QUEEN;
      case BLACKKING:
        return Piece.BLACK_KING;
      default:
        throw new IllegalArgumentException();
    }
  }

  static GenericPiece fromPiece(int piece) {
    switch (piece) {
      case Piece.WHITE_PAWN:
        return GenericPiece.WHITEPAWN;
      case Piece.WHITE_KNIGHT:
        return GenericPiece.WHITEKNIGHT;
      case Piece.WHITE_BISHOP:
        return GenericPiece.WHITEBISHOP;
      case Piece.WHITE_ROOK:
        return GenericPiece.WHITEROOK;
      case Piece.WHITE_QUEEN:
        return GenericPiece.WHITEQUEEN;
      case Piece.WHITE_KING:
        return GenericPiece.WHITEKING;
      case Piece.BLACK_PAWN:
        return GenericPiece.BLACKPAWN;
      case Piece.BLACK_KNIGHT:
        return GenericPiece.BLACKKNIGHT;
      case Piece.BLACK_BISHOP:
        return GenericPiece.BLACKBISHOP;
      case Piece.BLACK_ROOK:
        return GenericPiece.BLACKROOK;
      case Piece.BLACK_QUEEN:
        return GenericPiece.BLACKQUEEN;
      case Piece.BLACK_KING:
        return GenericPiece.BLACKKING;
      case Piece.NOPIECE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static GenericCastling fromCastlingType(int castlingtype) {
    switch (castlingtype) {
      case CastlingType.KINGSIDE:
        return GenericCastling.KINGSIDE;
      case CastlingType.QUEENSIDE:
        return GenericCastling.QUEENSIDE;
      case CastlingType.NOCASTLINGTYPE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static int toFile(GenericFile genericFile) {
    assert genericFile != null;

    switch (genericFile) {
      case Fa:
        return File.a;
      case Fb:
        return File.b;
      case Fc:
        return File.c;
      case Fd:
        return File.d;
      case Fe:
        return File.e;
      case Ff:
        return File.f;
      case Fg:
        return File.g;
      case Fh:
        return File.h;
      default:
        throw new IllegalArgumentException();
    }
  }

  static GenericFile fromFile(int file) {
    switch (file) {
      case File.a:
        return GenericFile.Fa;
      case File.b:
        return GenericFile.Fb;
      case File.c:
        return GenericFile.Fc;
      case File.d:
        return GenericFile.Fd;
      case File.e:
        return GenericFile.Fe;
      case File.f:
        return GenericFile.Ff;
      case File.g:
        return GenericFile.Fg;
      case File.h:
        return GenericFile.Fh;
      case File.NOFILE:
      default:
        throw new IllegalArgumentException();
    }
  }

  static int toRank(GenericRank genericRank) {
    assert genericRank != null;

    switch (genericRank) {
      case R1:
        return Rank.r1;
      case R2:
        return Rank.r2;
      case R3:
        return Rank.r3;
      case R4:
        return Rank.r4;
      case R5:
        return Rank.r5;
      case R6:
        return Rank.r6;
      case R7:
        return Rank.r7;
      case R8:
        return Rank.r8;
      default:
        throw new IllegalArgumentException();
    }
  }

  static GenericRank fromRank(int rank) {
    switch (rank) {
      case Rank.r1:
        return GenericRank.R1;
      case Rank.r2:
        return GenericRank.R2;
      case Rank.r3:
        return GenericRank.R3;
      case Rank.r4:
        return GenericRank.R4;
      case Rank.r5:
        return GenericRank.R5;
      case Rank.r6:
        return GenericRank.R6;
      case Rank.r7:
        return GenericRank.R7;
      case Rank.r8:
        return GenericRank.R8;
      case Rank.NORANK:
      default:
        throw new IllegalArgumentException();
    }
  }

  static int toSquare(GenericPosition genericPosition) {
    assert genericPosition != null;

    int square = toRank(genericPosition.rank) * 16 + toFile(genericPosition.file);
    assert Square.isValid(square);

    return square;
  }

  static GenericPosition fromSquare(int square) {
    assert Square.isValid(square);

    return GenericPosition.valueOf(fromFile(Square.getFile(square)), fromRank(Square.getRank(square)));
  }
}
