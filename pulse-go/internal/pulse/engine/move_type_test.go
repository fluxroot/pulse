/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package engine

import (
	"testing"
)

func TestMoveType(t *testing.T) {
	t.Run("moveTypes array should be valid", func(t *testing.T) {
		for mt := range moveTypes {
			if moveTypes[mt] != mt {
				t.Errorf("wanted move types at index %d to be %v, but got %v", mt, mt, moveTypes[mt])
			}
		}
	})
}
