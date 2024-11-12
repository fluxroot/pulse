/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package uci

import (
	"bufio"
	"io"
	"os"
)

type Reader interface {
	Readln() (string, error)
}

func NewStdinReader() *StdinReader {
	return &StdinReader{
		scanner: bufio.NewScanner(os.Stdin),
	}
}

type StdinReader struct {
	scanner *bufio.Scanner
}

func (r *StdinReader) Readln() (string, error) {
	if r.scanner.Scan() {
		return r.scanner.Text(), nil
	} else if r.scanner.Err() != nil {
		return "", r.scanner.Err()
	} else {
		return "", io.EOF
	}
}
