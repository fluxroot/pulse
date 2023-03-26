/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse.java;

import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericCastling;
import com.fluxchess.jcpi.models.GenericChessman;
import com.fluxchess.jcpi.models.GenericColor;
import com.fluxchess.jcpi.models.GenericFile;
import com.fluxchess.jcpi.models.GenericPiece;
import com.fluxchess.jcpi.models.GenericPosition;
import com.fluxchess.jcpi.models.GenericRank;
import com.fluxchess.jcpi.models.IllegalNotationException;
import com.fluxchess.pulse.java.model.Castling;
import com.fluxchess.pulse.java.model.CastlingType;
import com.fluxchess.pulse.java.model.Color;
import com.fluxchess.pulse.java.model.File;
import com.fluxchess.pulse.java.model.Piece;
import com.fluxchess.pulse.java.model.PieceType;
import com.fluxchess.pulse.java.model.Square;

import static com.fluxchess.pulse.java.model.Castling.BLACK_KINGSIDE;
import static com.fluxchess.pulse.java.model.Castling.BLACK_QUEENSIDE;
import static com.fluxchess.pulse.java.model.Castling.NOCASTLING;
import static com.fluxchess.pulse.java.model.Castling.WHITE_KINGSIDE;
import static com.fluxchess.pulse.java.model.Castling.WHITE_QUEENSIDE;
import static com.fluxchess.pulse.java.model.Color.BLACK;
import static com.fluxchess.pulse.java.model.Color.NOCOLOR;
import static com.fluxchess.pulse.java.model.Color.WHITE;
import static com.fluxchess.pulse.java.model.Rank.NORANK;
import static com.fluxchess.pulse.java.model.Rank.r1;
import static com.fluxchess.pulse.java.model.Rank.r2;
import static com.fluxchess.pulse.java.model.Rank.r3;
import static com.fluxchess.pulse.java.model.Rank.r4;
import static com.fluxchess.pulse.java.model.Rank.r5;
import static com.fluxchess.pulse.java.model.Rank.r6;
import static com.fluxchess.pulse.java.model.Rank.r7;
import static com.fluxchess.pulse.java.model.Rank.r8;
import static com.fluxchess.pulse.java.model.Square.NOSQUARE;

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
		if ((position.castlingRights & WHITE_KINGSIDE) != NOCASTLING) {
			genericBoard.setCastling(
				fromColor(WHITE), fromCastlingType(CastlingType.KINGSIDE),
				fromFile(File.h)
			);
		}
		if ((position.castlingRights & WHITE_QUEENSIDE) != NOCASTLING) {
			genericBoard.setCastling(
				fromColor(WHITE), fromCastlingType(CastlingType.QUEENSIDE),
				fromFile(File.a)
			);
		}
		if ((position.castlingRights & BLACK_KINGSIDE) != NOCASTLING) {
			genericBoard.setCastling(
				fromColor(BLACK), fromCastlingType(CastlingType.KINGSIDE),
				fromFile(File.h)
			);
		}
		if ((position.castlingRights & BLACK_QUEENSIDE) != NOCASTLING) {
			genericBoard.setCastling(
				fromColor(BLACK), fromCastlingType(CastlingType.QUEENSIDE),
				fromFile(File.a)
			);
		}

		// Set en passant
		if (position.enPassantSquare != NOSQUARE) {
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
		switch (genericColor) {
			case WHITE:
				return WHITE;
			case BLACK:
				return BLACK;
			default:
				throw new IllegalArgumentException();
		}
	}

	static GenericColor fromColor(int color) {
		switch (color) {
			case WHITE:
				return GenericColor.WHITE;
			case BLACK:
				return GenericColor.BLACK;
			case NOCOLOR:
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
		switch (genericRank) {
			case R1:
				return r1;
			case R2:
				return r2;
			case R3:
				return r3;
			case R4:
				return r4;
			case R5:
				return r5;
			case R6:
				return r6;
			case R7:
				return r7;
			case R8:
				return r8;
			default:
				throw new IllegalArgumentException();
		}
	}

	static GenericRank fromRank(int rank) {
		switch (rank) {
			case r1:
				return GenericRank.R1;
			case r2:
				return GenericRank.R2;
			case r3:
				return GenericRank.R3;
			case r4:
				return GenericRank.R4;
			case r5:
				return GenericRank.R5;
			case r6:
				return GenericRank.R6;
			case r7:
				return GenericRank.R7;
			case r8:
				return GenericRank.R8;
			case NORANK:
			default:
				throw new IllegalArgumentException();
		}
	}

	static int toSquare(GenericPosition genericPosition) {
		return toRank(genericPosition.rank) * 16 + toFile(genericPosition.file);
	}

	static GenericPosition fromSquare(int square) {
		return GenericPosition.valueOf(fromFile(Square.getFile(square)), fromRank(Square.getRank(square)));
	}
}
