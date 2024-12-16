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

func TestColor(t *testing.T) {
	t.Run("Colors array should be valid", func(t *testing.T) {
		for color := range Colors {
			if Colors[color] != color {
				t.Errorf("wanted colors at index %d to be %v, got %v", color, color, Colors[color])
			}
		}
	})
}
