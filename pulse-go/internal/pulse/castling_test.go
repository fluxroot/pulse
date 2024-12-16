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

func TestCastling(t *testing.T) {
	t.Run("Castlings array should be valid", func(t *testing.T) {
		for castling := range Castlings {
			if Castlings[castling] != castling {
				t.Errorf("wanted castlings at index %d to be %v, got %v", castling, castling, Castlings[castling])
			}
		}
	})
}
