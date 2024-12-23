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

func TestFile(t *testing.T) {
	t.Run("Files array should be valid", func(t *testing.T) {
		for f := range Files {
			if Files[f] != f {
				t.Errorf("wanted Files at index %d to be %v, but got %v", f, f, Files[f])
			}
		}
	})
}

func TestFile_IsValidFile(t *testing.T) {
	tests := []struct {
		file File
		want bool
	}{
		{file: FileA, want: true},
		{file: FileB, want: true},
		{file: FileC, want: true},
		{file: FileD, want: true},
		{file: FileE, want: true},
		{file: FileF, want: true},
		{file: FileG, want: true},
		{file: FileH, want: true},
		{file: NoFile, want: false},
	}
	for _, tt := range tests {
		t.Run("IsValidFile should return true if file is valid, false otherwise", func(t *testing.T) {
			valid := IsValidFile(tt.file)
			if valid != tt.want {
				t.Errorf("IsValidFile(%d) = %v, want %v", tt.file, valid, tt.want)
			}
		})
	}
}
