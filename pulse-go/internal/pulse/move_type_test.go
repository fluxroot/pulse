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

func TestMoveType(t *testing.T) {
	t.Run("MoveTypes array should be valid", func(t *testing.T) {
		for moveType := range MoveTypes {
			if MoveTypes[moveType] != moveType {
				t.Errorf("wanted move types at index %d to be %v, got %v", moveType, moveType, MoveTypes[moveType])
			}
		}
	})
}
