/*
 * Copyright 2013-2025 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package uci

import (
	"errors"
	"reflect"
	"testing"

	"github.com/fluxroot/pulse/internal/pulse/engine"
)

var noPosition = func() *engine.Position { return nil }

func TestNotation_FENToPosition(t *testing.T) {
	tests := []struct {
		name    string
		fen     string
		want    func() *engine.Position
		wantErr error
	}{
		{
			name:    "Valid FEN should return a valid position",
			fen:     "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
			want:    startingPosition,
			wantErr: nil,
		},
		{
			name:    "Valid FEN without halfmove clock and fullmove number should return a valid position",
			fen:     "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -",
			want:    startingPosition,
			wantErr: nil,
		},
		{
			name:    "Invalid FEN should return an error",
			fen:     "invalid FEN",
			want:    noPosition,
			wantErr: errors.New("invalid FEN: invalid FEN"),
		},
		{
			name:    "Invalid ranks should return an error",
			fen:     "invalid-ranks w KQkq - 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid board: invalid-ranks"),
		},
		{
			name:    "Invalid rank with too many squares should return an error",
			fen:     "rnbqkbnr1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid rank: rnbqkbnr1"),
		},
		{
			name:    "Invalid rank with too few squares should return an error",
			fen:     "rnbqkbn/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid rank: rnbqkbn"),
		},
		{
			name:    "Invalid piece type should return an error",
			fen:     "xnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid rank: xnbqkbnr"),
		},
		{
			name:    "Invalid number of empty squares should return an error",
			fen:     "9/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid rank: 9"),
		},
		{
			name:    "Invalid active color should return an error",
			fen:     "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR x KQkq - 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid active color: x"),
		},
		{
			name: "No castling rights should return a valid position",
			fen:  "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1",
			want: func() *engine.Position {
				p := startingPosition()
				p.CastlingRights = engine.NoCastling
				return p
			},
			wantErr: nil,
		},
		{
			name:    "Invalid castling rights with too many castlings should return an error",
			fen:     "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkqK - 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid castling rights: KQkqK"),
		},
		{
			name:    "Invalid castling rights should return an error",
			fen:     "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w XQkq - 0 1",
			want:    noPosition,
			wantErr: errors.New("could not map to castling: invalid castling type: X"),
		},
		{
			name: "Valid en passant square should return a valid position",
			fen:  "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e6 0 1",
			want: func() *engine.Position {
				p := startingPosition()
				p.EnPassantSquare = engine.E6
				return p
			},
			wantErr: nil,
		},
		{
			name:    "Invalid en passant square length should return an error",
			fen:     "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e31 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid en passant square: e31"),
		},
		{
			name:    "Invalid en passant square with non-existing file should return an error",
			fen:     "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq x3 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid file: x"),
		},
		{
			name:    "Invalid en passant square with non-existing rank should return an error",
			fen:     "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e9 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid rank: 9"),
		},
		{
			name:    "Invalid en passant square should return an error",
			fen:     "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e2 0 1",
			want:    noPosition,
			wantErr: errors.New("invalid en passant square: e2"),
		},
		{
			name:    "Invalid halfmove clock should return an error",
			fen:     "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - x 1",
			want:    noPosition,
			wantErr: errors.New("strconv.Atoi: parsing \"x\": invalid syntax"),
		},
		{
			name: "Valid fullmove number should return a valid position",
			fen:  "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1",
			want: func() *engine.Position {
				p := startingPosition()
				p.ActiveColor = engine.Black
				p.HalfmoveNumber = 3
				return p
			},
			wantErr: nil,
		},
		{
			name:    "Invalid fullmove number should return an error",
			fen:     "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 x",
			want:    noPosition,
			wantErr: errors.New("strconv.Atoi: parsing \"x\": invalid syntax"),
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := FENToPosition(tt.fen)
			if tt.wantErr != nil && (err == nil || err.Error() != tt.wantErr.Error()) {
				t.Errorf("FENToPosition() error = %v, wantErr %v", err, tt.wantErr)
			}
			if !reflect.DeepEqual(got, tt.want()) {
				t.Errorf("FENToPosition() got = %v, want %v", got, tt.want())
			}
		})
	}
}

func TestNotation_PositionToFEN(t *testing.T) {
	tests := []struct {
		name     string
		position func() *engine.Position
		want     string
	}{
		{
			name:     "Starting position should return a valid FEN",
			position: startingPosition,
			want:     "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
		},
		{
			name: "No castling rights should return a valid FEN",
			position: func() *engine.Position {
				p := startingPosition()
				p.CastlingRights = engine.NoCastling
				return p
			},
			want: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1",
		},
		{
			name: "Valid en passant square should return a valid FEN",
			position: func() *engine.Position {
				p := startingPosition()
				p.EnPassantSquare = engine.E6
				return p
			},
			want: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e6 0 1",
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := PositionToFEN(tt.position())
			if got != tt.want {
				t.Errorf("PositionToFEN() got = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestNotation_MoveToNotation(t *testing.T) {
	tests := []struct {
		name string
		move engine.Move
		want string
	}{
		{
			name: "Valid move should return a valid notation",
			move: engine.MoveOf(engine.NormalMove, engine.A1, engine.B2, engine.WhiteQueen, engine.BlackRook, engine.NoPieceType),
			want: "a1b2",
		},
		{
			name: "Valid move with promotion should return a valid notation",
			move: engine.MoveOf(engine.PawnPromotionMove, engine.A7, engine.A8, engine.WhitePawn, engine.NoPiece, engine.Queen),
			want: "a7a8q",
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := MoveToNotation(tt.move)
			if got != tt.want {
				t.Errorf("MoveToNotation() got = %v, want %v", got, tt.want)
			}
		})
	}
}

func startingPosition() *engine.Position {
	p := engine.NewPosition()
	p.ActiveColor = engine.White
	p.CastlingRights = engine.WhiteKingside | engine.WhiteQueenside | engine.BlackKingside | engine.BlackQueenside
	p.HalfmoveNumber = 2
	p.Put(engine.WhiteRook, engine.A1)
	p.Put(engine.WhiteKnight, engine.B1)
	p.Put(engine.WhiteBishop, engine.C1)
	p.Put(engine.WhiteQueen, engine.D1)
	p.Put(engine.WhiteKing, engine.E1)
	p.Put(engine.WhiteBishop, engine.F1)
	p.Put(engine.WhiteKnight, engine.G1)
	p.Put(engine.WhiteRook, engine.H1)
	p.Put(engine.BlackRook, engine.A8)
	p.Put(engine.BlackKnight, engine.B8)
	p.Put(engine.BlackBishop, engine.C8)
	p.Put(engine.BlackQueen, engine.D8)
	p.Put(engine.BlackKing, engine.E8)
	p.Put(engine.BlackBishop, engine.F8)
	p.Put(engine.BlackKnight, engine.G8)
	p.Put(engine.BlackRook, engine.H8)
	for _, f := range engine.Files {
		p.Put(engine.WhitePawn, engine.SquareOf(f, engine.Rank2))
		p.Put(engine.BlackPawn, engine.SquareOf(f, engine.Rank7))
	}
	return p
}
