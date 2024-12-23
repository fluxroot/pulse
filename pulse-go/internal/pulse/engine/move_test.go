/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import "testing"

func TestMove_moveOf(t *testing.T) {
	t.Run("moveOf should return the move", func(t *testing.T) {
		m := moveOf(pawnPromotionMove, A7, B8, WhitePawn, BlackQueen, Knight)

		mt := moveTypeOf(m)
		if mt != pawnPromotionMove {
			t.Errorf("wanted move type of move to be %v, but got %v", pawnPromotionMove, mt)
		}
		originSq := OriginSquareOf(m)
		if originSq != A7 {
			t.Errorf("wanted origin square of move to be %v, but got %v", A7, originSq)
		}
		targetSq := TargetSquareOf(m)
		if targetSq != B8 {
			t.Errorf("wanted target square of move to be %v, but got %v", B8, targetSq)
		}
		originPc := OriginPieceOf(m)
		if originPc != WhitePawn {
			t.Errorf("wanted origin piece of move to be %v, but got %v", WhitePawn, originPc)
		}
		targetPc := TargetPieceOf(m)
		if targetPc != BlackQueen {
			t.Errorf("wanted target piece of move to be %v, but got %v", BlackQueen, targetPc)
		}
		promotion := PromotionOf(m)
		if promotion != Knight {
			t.Errorf("wanted promotion of move to be %v, but got %v", Knight, promotion)
		}
	})
}
