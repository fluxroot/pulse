/*
 * Copyright 2013-2025 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.uci

import com.fluxchess.pulse.kotlin.engine.BISHOP
import com.fluxchess.pulse.kotlin.engine.BLACK
import com.fluxchess.pulse.kotlin.engine.BLACK_BISHOP
import com.fluxchess.pulse.kotlin.engine.BLACK_KING
import com.fluxchess.pulse.kotlin.engine.BLACK_KINGSIDE
import com.fluxchess.pulse.kotlin.engine.BLACK_KNIGHT
import com.fluxchess.pulse.kotlin.engine.BLACK_PAWN
import com.fluxchess.pulse.kotlin.engine.BLACK_QUEEN
import com.fluxchess.pulse.kotlin.engine.BLACK_QUEENSIDE
import com.fluxchess.pulse.kotlin.engine.BLACK_ROOK
import com.fluxchess.pulse.kotlin.engine.Castling
import com.fluxchess.pulse.kotlin.engine.CastlingType
import com.fluxchess.pulse.kotlin.engine.Color
import com.fluxchess.pulse.kotlin.engine.FILE_A
import com.fluxchess.pulse.kotlin.engine.FILE_B
import com.fluxchess.pulse.kotlin.engine.FILE_C
import com.fluxchess.pulse.kotlin.engine.FILE_D
import com.fluxchess.pulse.kotlin.engine.FILE_E
import com.fluxchess.pulse.kotlin.engine.FILE_F
import com.fluxchess.pulse.kotlin.engine.FILE_G
import com.fluxchess.pulse.kotlin.engine.FILE_H
import com.fluxchess.pulse.kotlin.engine.File
import com.fluxchess.pulse.kotlin.engine.KING
import com.fluxchess.pulse.kotlin.engine.KINGSIDE
import com.fluxchess.pulse.kotlin.engine.KNIGHT
import com.fluxchess.pulse.kotlin.engine.Move
import com.fluxchess.pulse.kotlin.engine.NO_FILE
import com.fluxchess.pulse.kotlin.engine.NO_PIECE
import com.fluxchess.pulse.kotlin.engine.NO_PIECE_TYPE
import com.fluxchess.pulse.kotlin.engine.NO_SQUARE
import com.fluxchess.pulse.kotlin.engine.PAWN
import com.fluxchess.pulse.kotlin.engine.Piece
import com.fluxchess.pulse.kotlin.engine.PieceType
import com.fluxchess.pulse.kotlin.engine.Position
import com.fluxchess.pulse.kotlin.engine.QUEEN
import com.fluxchess.pulse.kotlin.engine.QUEENSIDE
import com.fluxchess.pulse.kotlin.engine.RANK_1
import com.fluxchess.pulse.kotlin.engine.RANK_2
import com.fluxchess.pulse.kotlin.engine.RANK_3
import com.fluxchess.pulse.kotlin.engine.RANK_4
import com.fluxchess.pulse.kotlin.engine.RANK_5
import com.fluxchess.pulse.kotlin.engine.RANK_6
import com.fluxchess.pulse.kotlin.engine.RANK_7
import com.fluxchess.pulse.kotlin.engine.RANK_8
import com.fluxchess.pulse.kotlin.engine.ROOK
import com.fluxchess.pulse.kotlin.engine.Rank
import com.fluxchess.pulse.kotlin.engine.Square
import com.fluxchess.pulse.kotlin.engine.WHITE
import com.fluxchess.pulse.kotlin.engine.WHITE_BISHOP
import com.fluxchess.pulse.kotlin.engine.WHITE_KING
import com.fluxchess.pulse.kotlin.engine.WHITE_KINGSIDE
import com.fluxchess.pulse.kotlin.engine.WHITE_KNIGHT
import com.fluxchess.pulse.kotlin.engine.WHITE_PAWN
import com.fluxchess.pulse.kotlin.engine.WHITE_QUEEN
import com.fluxchess.pulse.kotlin.engine.WHITE_QUEENSIDE
import com.fluxchess.pulse.kotlin.engine.WHITE_ROOK
import com.fluxchess.pulse.kotlin.engine.castlingOf
import com.fluxchess.pulse.kotlin.engine.fileOf
import com.fluxchess.pulse.kotlin.engine.files
import com.fluxchess.pulse.kotlin.engine.isValidFile
import com.fluxchess.pulse.kotlin.engine.originSquareOf
import com.fluxchess.pulse.kotlin.engine.pieceOf
import com.fluxchess.pulse.kotlin.engine.promotionOf
import com.fluxchess.pulse.kotlin.engine.rankOf
import com.fluxchess.pulse.kotlin.engine.ranks
import com.fluxchess.pulse.kotlin.engine.squareOf
import com.fluxchess.pulse.kotlin.engine.targetSquareOf

private val fenTokenRegex = "\\s+".toRegex()
private val rankRegex = "/".toRegex()

fun String.toPositionOrNull(): Position? = try {
	toPosition()
} catch (e: FenException) {
	null
}

fun String.toPosition(): Position {
	// Clean and split into tokens
	val tokens = trim().split(fenTokenRegex)

	// halfmove clock and fullmove number are optional
	if (tokens.size < 4 || tokens.size > 6) {
		throw FenException("Invalid FEN: $this")
	}

	val position = Position()

	// Parse board
	val board = tokens[0].split(rankRegex)
	if (board.size != 8) {
		throw FenException("Invalid board: ${tokens[0]}")
	}
	var currentRank = RANK_8
	board.forEach { rank ->
		var currentFile = FILE_A
		rank.forEach { char ->
			if (!isValidFile(currentFile)) {
				throw FenException("Invalid rank: $rank")
			}
			val piece = char.toPieceOrNull()
			if (piece != null) {
				position.put(piece, squareOf(currentFile, currentRank))
				if (currentFile == FILE_H) {
					currentFile = NO_FILE
				} else {
					currentFile++
				}
				return@forEach
			}
			val emptySquares = char.toEmptySquaresOrNull()
			if (emptySquares != null) {
				currentFile += emptySquares
				return@forEach
			}
		}
		if (currentFile != NO_FILE) {
			throw FenException("Invalid rank: $rank")
		}
		currentRank--
	}

	// Parse active color
	val activeColor = tokens[1].toActiveColor()
	position.activeColor = activeColor

	// Parse castling rights
	if (tokens[2] != "-") {
		if (tokens[2].length > 4) {
			throw FenException("Invalid castling rights: ${tokens[2]}")
		}
		tokens[2].forEach { char ->
			val castling = char.toCastling()
			position.setCastlingRight(castling)
		}
	}

	// Parse en passant square
	if (tokens[3] != "-") {
		if (tokens[3].length != 2) {
			throw FenException("Invalid en passant square: ${tokens[3]}")
		}
		val file = tokens[3][0].toFile()
		val rank = tokens[3][1].toRank()
		if (!(activeColor == WHITE && rank == RANK_6) && !(activeColor == BLACK && rank == RANK_3)) {
			throw FenException("Invalid en passant square: ${tokens[3]}")
		}
		position.enPassantSquare = squareOf(file, rank)
	}

	// Parse halfmove clock
	if (tokens.size >= 5) {
		val halfmoveClock = tokens[4].toIntOrNull() ?: throw FenException("Invalid halfmove clock: ${tokens[4]}")
		position.halfmoveClock = halfmoveClock
	}

	// Parse fullmove number
	if (tokens.size == 6) {
		val fullmoveNumber = tokens[5].toIntOrNull() ?: throw FenException("Invalid fullmove number: ${tokens[5]}")
		position.halfmoveNumber = fullmoveNumber * 2
		if (activeColor == BLACK) {
			position.halfmoveNumber++
		}
	} else {
		position.halfmoveNumber = 2
	}

	return position
}

class FenException(message: String) : Exception(message)

private fun Char.toPieceOrNull(): Piece? = toPieceTypeOrNull()?.let { pieceOf(toColor(), it) }

private fun Char.toColor(): Color = if (isLowerCase()) BLACK else WHITE

private fun Char.toPieceTypeOrNull(): PieceType? = when (lowercaseChar()) {
	'p' -> PAWN
	'n' -> KNIGHT
	'b' -> BISHOP
	'r' -> ROOK
	'q' -> QUEEN
	'k' -> KING
	else -> null
}

private fun Char.toEmptySquaresOrNull(): Int? {
	val emptySquares = if (isDigit()) digitToInt() else return null
	return if (emptySquares in 1..8) emptySquares else null
}

private fun String.toActiveColor(): Color = when (lowercase()) {
	"w" -> WHITE
	"b" -> BLACK
	else -> throw FenException("Invalid active color: $this")
}

private fun Char.toCastling(): Castling = castlingOf(toColor(), toCastlingType())

private fun Char.toCastlingType(): CastlingType = when (lowercaseChar()) {
	'k' -> KINGSIDE
	'q' -> QUEENSIDE
	else -> throw FenException("Invalid castling type: $this")
}

private fun Char.toFile(): File = when (lowercaseChar()) {
	'a' -> FILE_A
	'b' -> FILE_B
	'c' -> FILE_C
	'd' -> FILE_D
	'e' -> FILE_E
	'f' -> FILE_F
	'g' -> FILE_G
	'h' -> FILE_H
	else -> throw FenException("Invalid file: $this")
}

private fun Char.toRank(): Rank = when (lowercaseChar()) {
	'1' -> RANK_1
	'2' -> RANK_2
	'3' -> RANK_3
	'4' -> RANK_4
	'5' -> RANK_5
	'6' -> RANK_6
	'7' -> RANK_7
	'8' -> RANK_8
	else -> throw FenException("Invalid rank: $this")
}

fun Position.toFEN(): String {
	var fen = ""

	// board
	for (rank in ranks.reversed()) {
		var emptySquares = 0
		for (file in files) {
			val piece = get(squareOf(file, rank))
			if (piece == NO_PIECE) {
				emptySquares++
			} else {
				if (emptySquares > 0) {
					fen += emptySquares
					emptySquares = 0
				}
				fen += pieceToNotation(piece)
			}
		}
		if (emptySquares > 0) {
			fen += emptySquares
		}
		if (rank > RANK_1) {
			fen += '/'
		}
	}

	// active color
	fen += " " + colorToNotation(activeColor)

	// castling rights
	var rights = ""
	if (castlingRights and WHITE_KINGSIDE == WHITE_KINGSIDE) {
		rights += "K"
	}
	if (castlingRights and WHITE_QUEENSIDE == WHITE_QUEENSIDE) {
		rights += "Q"
	}
	if (castlingRights and BLACK_KINGSIDE == BLACK_KINGSIDE) {
		rights += "k"
	}
	if (castlingRights and BLACK_QUEENSIDE == BLACK_QUEENSIDE) {
		rights += "q"
	}
	if (rights == "") {
		rights = "-"
	}
	fen += " $rights"

	// en passant square
	if (enPassantSquare != NO_SQUARE) {
		fen += " " + squareToNotation(enPassantSquare)
	} else {
		fen += " -"
	}

	// halfmove clock
	fen += " $halfmoveClock"

	// fullmove number
	fen += " " + (halfmoveNumber / 2)

	return fen
}

private fun pieceToNotation(piece: Piece): String = when (piece) {
	WHITE_PAWN -> "P"
	WHITE_KNIGHT -> "N"
	WHITE_BISHOP -> "B"
	WHITE_ROOK -> "R"
	WHITE_QUEEN -> "Q"
	WHITE_KING -> "K"
	BLACK_PAWN -> "p"
	BLACK_KNIGHT -> "n"
	BLACK_BISHOP -> "b"
	BLACK_ROOK -> "r"
	BLACK_QUEEN -> "q"
	BLACK_KING -> "k"
	else -> error("Invalid piece: $piece")
}

private fun colorToNotation(color: Color): String = when (color) {
	WHITE -> "w"
	BLACK -> "b"
	else -> error("Invalid color: $color")
}

private fun squareToNotation(square: Square): String {
	return fileToNotation(fileOf(square)) + rankToNotation(rankOf(square))
}

private fun fileToNotation(file: File): String = when (file) {
	FILE_A -> "a"
	FILE_B -> "b"
	FILE_C -> "c"
	FILE_D -> "d"
	FILE_E -> "e"
	FILE_F -> "f"
	FILE_G -> "g"
	FILE_H -> "h"
	else -> error("Invalid file: $file")
}

private fun rankToNotation(rank: Rank): String = when (rank) {
	RANK_1 -> "1"
	RANK_2 -> "2"
	RANK_3 -> "3"
	RANK_4 -> "4"
	RANK_5 -> "5"
	RANK_6 -> "6"
	RANK_7 -> "7"
	RANK_8 -> "8"
	else -> error("Invalid rank: $rank")
}

fun Move.toNotation(): String {
	var notation = ""
	notation += squareToNotation(originSquareOf(this))
	notation += squareToNotation(targetSquareOf(this))
	val promotion = promotionOf(this)
	if (promotion != NO_PIECE_TYPE) {
		notation += pieceTypeToNotation(promotion)
	}
	return notation
}

private fun pieceTypeToNotation(pieceType: PieceType): String = when (pieceType) {
	KNIGHT -> "n"
	BISHOP -> "b"
	ROOK -> "r"
	QUEEN -> "q"
	else -> error("Invalid piece type: $pieceType")
}
