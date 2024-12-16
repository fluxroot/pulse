/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

import (
	"testing"
)

func TestPiece(t *testing.T) {
	t.Run("Pieces array should be valid", func(t *testing.T) {
		for piece := range Pieces {
			if Pieces[piece] != piece {
				t.Errorf("wanted pieces at index %d to be %v, got %v", piece, piece, Pieces[piece])
			}
		}
	})
}
