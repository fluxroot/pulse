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

func TestFile(t *testing.T) {
	t.Run("Files array should be valid", func(t *testing.T) {
		for file := range Files {
			if Files[file] != file {
				t.Errorf("wanted files at index %d to be %v, got %v", file, file, Files[file])
			}
		}
	})
}
