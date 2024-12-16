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

func TestCastlingType(t *testing.T) {
	t.Run("CastlingTypes array should be valid", func(t *testing.T) {
		for castlingType := range CastlingTypes {
			if CastlingTypes[castlingType] != castlingType {
				t.Errorf("wanted castling types at index %d to be %v, got %v", castlingType, castlingType, CastlingTypes[castlingType])
			}
		}
	})
}
