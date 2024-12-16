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

func TestPieceType(t *testing.T) {
	t.Run("PieceTypes array should be valid", func(t *testing.T) {
		for pieceType := range PieceTypes {
			if PieceTypes[pieceType] != pieceType {
				t.Errorf("wanted piece types at index %d to be %v, got %v", pieceType, pieceType, PieceTypes[pieceType])
			}
		}
	})
}
