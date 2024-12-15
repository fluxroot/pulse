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

func TestRank(t *testing.T) {
	t.Run("Ranks array should be valid", func(t *testing.T) {
		for rank := range Ranks {
			if Ranks[rank] != rank {
				t.Errorf("wanted ranks at index %d to be %v, got %v", rank, rank, Ranks[rank])
			}
		}
	})
}
