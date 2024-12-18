/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

import "testing"

func TestMove(t *testing.T) {
	t.Run("Move should be correctly created", func(t *testing.T) {
		move := MoveOf(PawnPromotionMove, A7, B8, WhitePawn, BlackQueen, Knight)
		if MoveTypeOf(move) != PawnPromotionMove {
			t.Errorf("wanted move type of move to be %v, got %v", PawnPromotionMove, MoveTypeOf(move))
		}
		if OriginSquareOf(move) != A7 {
			t.Errorf("wanted origin square of move to be %v, got %v", A7, OriginSquareOf(move))
		}
		if TargetSquareOf(move) != B8 {
			t.Errorf("wanted target square of move to be %v, got %v", B8, TargetSquareOf(move))
		}
		if OriginPieceOf(move) != WhitePawn {
			t.Errorf("wanted origin piece of move to be %v, got %v", WhitePawn, OriginPieceOf(move))
		}
		if TargetPieceOf(move) != BlackQueen {
			t.Errorf("wanted target piece of move to be %v, got %v", BlackQueen, TargetPieceOf(move))
		}
		if PromotionOf(move) != Knight {
			t.Errorf("wanted promotion of move to be %v, got %v", Knight, PromotionOf(move))
		}
	})
}
