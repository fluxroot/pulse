/*
 * Copyright 2013-2025 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package fen

import (
	"errors"
	"fmt"
	"regexp"
	"strconv"
	"strings"
	"unicode"

	"github.com/fluxroot/pulse/internal/pulse/engine"
)

var tokenRegex = regexp.MustCompile(`\s+`)
var rankRegex = regexp.MustCompile(`/`)

func ToPosition(fen string) (*engine.Position, error) {
	// Clean and split into tokens
	tokens := tokenRegex.Split(strings.TrimSpace(fen), -1)

	// halfmove clock and fullmove number are optional
	if len(tokens) < 4 || len(tokens) > 6 {
		return nil, errors.New("invalid FEN: " + fen)
	}

	p := engine.NewPosition()

	// Parse board
	board := rankRegex.Split(tokens[0], -1)
	if len(board) != 8 {
		return nil, errors.New("invalid board: " + tokens[0])
	}
	currentRank := engine.Rank8
	for _, rank := range board {
		currentFile := engine.FileA
		for _, char := range rank {
			if !engine.IsValidFile(currentFile) {
				return nil, errors.New("invalid rank: " + rank)
			}
			if pc, err := toPiece(char); err == nil {
				p.Put(pc, engine.SquareOf(currentFile, currentRank))
				if currentFile == engine.FileH {
					currentFile = engine.NoFile
				} else {
					currentFile++
				}
				continue
			}
			if emptySquares, err := toEmptySquares(char); err == nil {
				currentFile += emptySquares
				continue
			}
		}
		if currentFile != engine.NoFile {
			return nil, errors.New("invalid rank: " + rank)
		}
		currentRank--
	}

	// Parse active color
	activeCol, err := toActiveColor(tokens[1])
	if err != nil {
		return nil, err
	}
	p.ActiveColor = activeCol

	// Parse castling rights
	if tokens[2] != "-" {
		if len(tokens[2]) > 4 {
			return nil, errors.New("invalid castling rights: " + tokens[2])
		}
		for _, char := range tokens[2] {
			cast, err := toCastling(char)
			if err != nil {
				return nil, err
			}
			p.SetCastlingRight(cast)
		}
	}

	// Parse en passant square
	if tokens[3] != "-" {
		if len(tokens[3]) != 2 {
			return nil, errors.New("invalid en passant square: " + tokens[3])
		}
		f, err := toFile([]rune(tokens[3])[0])
		if err != nil {
			return nil, err
		}
		r, err := toRank([]rune(tokens[3])[1])
		if err != nil {
			return nil, err
		}
		if !(activeCol == engine.White && r == engine.Rank6) && !(activeCol == engine.Black && r == engine.Rank3) {
			return nil, errors.New("invalid en passant square: " + tokens[3])
		}
		p.EnPassantSquare = engine.SquareOf(f, r)
	}

	// Parse halfmove clock
	if len(tokens) >= 5 {
		halfmoveClock, err := strconv.Atoi(tokens[4])
		if err != nil {
			return nil, err
		}
		p.HalfmoveClock = halfmoveClock
	}

	// Parse fullmove number
	if len(tokens) == 6 {
		fullmoveNumber, err := strconv.Atoi(tokens[5])
		if err != nil {
			return nil, err
		}
		p.HalfmoveNumber = fullmoveNumber * 2
		if activeCol == engine.Black {
			p.HalfmoveNumber++
		}
	}

	return p, nil
}

func toPiece(r rune) (engine.Piece, error) {
	col := toColor(r)
	pt, err := toPieceType(r)
	if err != nil {
		return engine.NoPiece, fmt.Errorf("could not map to piece type: %w", err)
	}
	return engine.PieceOf(col, pt), nil
}

func toColor(r rune) engine.Color {
	if unicode.IsLower(r) {
		return engine.Black
	} else {
		return engine.White
	}
}

func toPieceType(r rune) (engine.PieceType, error) {
	switch unicode.ToLower(r) {
	case 'p':
		return engine.Pawn, nil
	case 'n':
		return engine.Knight, nil
	case 'b':
		return engine.Bishop, nil
	case 'r':
		return engine.Rook, nil
	case 'q':
		return engine.Queen, nil
	case 'k':
		return engine.King, nil
	default:
		return engine.NoPieceType, errors.New("invalid piece type: " + string(r))
	}
}

func toEmptySquares(r rune) (int, error) {
	if '1' <= r && r <= '8' {
		return int(r - '0'), nil
	} else {
		return 0, errors.New("invalid number of empty squares: " + string(r))
	}
}

func toActiveColor(s string) (engine.Color, error) {
	switch strings.ToLower(s) {
	case "w":
		return engine.White, nil
	case "b":
		return engine.Black, nil
	default:
		return engine.NoColor, errors.New("invalid active color: " + s)
	}
}

func toCastling(r rune) (engine.Castling, error) {
	col := toColor(r)
	ct, err := toCastlingType(r)
	if err != nil {
		return engine.NoCastling, fmt.Errorf("could not map to castling: %w", err)
	}
	return engine.CastlingOf(col, ct), nil
}

func toCastlingType(r rune) (engine.CastlingType, error) {
	switch unicode.ToLower(r) {
	case 'k':
		return engine.Kingside, nil
	case 'q':
		return engine.Queenside, nil
	default:
		return engine.NoCastlingType, errors.New("invalid castling type: " + string(r))
	}
}

func toFile(r rune) (engine.File, error) {
	switch unicode.ToLower(r) {
	case 'a':
		return engine.FileA, nil
	case 'b':
		return engine.FileB, nil
	case 'c':
		return engine.FileC, nil
	case 'd':
		return engine.FileD, nil
	case 'e':
		return engine.FileE, nil
	case 'f':
		return engine.FileF, nil
	case 'g':
		return engine.FileG, nil
	case 'h':
		return engine.FileH, nil
	default:
		return engine.NoFile, errors.New("invalid file: " + string(r))
	}
}

func toRank(r rune) (engine.Rank, error) {
	switch unicode.ToLower(r) {
	case '1':
		return engine.Rank1, nil
	case '2':
		return engine.Rank2, nil
	case '3':
		return engine.Rank3, nil
	case '4':
		return engine.Rank4, nil
	case '5':
		return engine.Rank5, nil
	case '6':
		return engine.Rank6, nil
	case '7':
		return engine.Rank7, nil
	case '8':
		return engine.Rank8, nil
	default:
		return engine.NoRank, errors.New("invalid rank: " + string(r))
	}
}

func ToFEN(p engine.Position) string {
	fen := ""

	// board
	for i := len(engine.Ranks) - 1; i >= 0; i-- {
		r := engine.Ranks[i]
		emptySquares := 0
		for _, f := range engine.Files {
			pc := p.Get(engine.SquareOf(f, r))
			if pc == engine.NoPiece {
				emptySquares++
			} else {
				if emptySquares > 0 {
					fen += strconv.Itoa(emptySquares)
					emptySquares = 0
				}
				fen += pieceToString(pc)
			}
		}
		if emptySquares > 0 {
			fen += strconv.Itoa(emptySquares)
		}
		if i > 0 {
			fen += "/"
		}
	}

	// active color
	fen += " " + colorToString(p.ActiveColor)

	// castling rights
	castlingRights := ""
	if p.CastlingRights&engine.WhiteKingside == engine.WhiteKingside {
		castlingRights += "K"
	}
	if p.CastlingRights&engine.WhiteQueenside == engine.WhiteQueenside {
		castlingRights += "Q"
	}
	if p.CastlingRights&engine.BlackKingside == engine.BlackKingside {
		castlingRights += "k"
	}
	if p.CastlingRights&engine.BlackQueenside == engine.BlackQueenside {
		castlingRights += "q"
	}
	if castlingRights == "" {
		castlingRights = "-"
	}
	fen += " " + castlingRights

	// en passant square
	if p.EnPassantSquare != engine.NoSquare {
		fen += " " + squareToString(p.EnPassantSquare)
	} else {
		fen += " -"
	}

	// halfmove clock
	fen += " " + strconv.Itoa(p.HalfmoveClock)

	// fullmove number
	fen += " " + strconv.Itoa(p.HalfmoveNumber/2)

	return fen
}

func pieceToString(pc engine.Piece) string {
	switch pc {
	case engine.WhitePawn:
		return "P"
	case engine.WhiteKnight:
		return "N"
	case engine.WhiteBishop:
		return "B"
	case engine.WhiteRook:
		return "R"
	case engine.WhiteQueen:
		return "Q"
	case engine.WhiteKing:
		return "K"
	case engine.BlackPawn:
		return "p"
	case engine.BlackKnight:
		return "n"
	case engine.BlackBishop:
		return "b"
	case engine.BlackRook:
		return "r"
	case engine.BlackQueen:
		return "q"
	case engine.BlackKing:
		return "k"
	default:
		panic(fmt.Sprintf("Invalid piece: %v", pc))
	}
}

func colorToString(col engine.Color) string {
	switch col {
	case engine.White:
		return "w"
	case engine.Black:
		return "b"
	default:
		panic(fmt.Sprintf("Invalid color: %v", col))
	}
}

func squareToString(sq engine.Square) string {
	return fileToString(engine.FileOf(sq)) + rankToString(engine.RankOf(sq))
}

func fileToString(f engine.File) string {
	switch f {
	case engine.FileA:
		return "a"
	case engine.FileB:
		return "b"
	case engine.FileC:
		return "c"
	case engine.FileD:
		return "d"
	case engine.FileE:
		return "e"
	case engine.FileF:
		return "f"
	case engine.FileG:
		return "g"
	case engine.FileH:
		return "h"
	default:
		panic(fmt.Sprintf("Invalid file: %v", f))
	}
}

func rankToString(r engine.Rank) string {
	switch r {
	case engine.Rank1:
		return "1"
	case engine.Rank2:
		return "2"
	case engine.Rank3:
		return "3"
	case engine.Rank4:
		return "4"
	case engine.Rank5:
		return "5"
	case engine.Rank6:
		return "6"
	case engine.Rank7:
		return "7"
	case engine.Rank8:
		return "8"
	default:
		panic(fmt.Sprintf("Invalid rank: %v", r))
	}
}
