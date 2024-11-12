/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package uci

import (
	"bufio"
	"fmt"
	"os"
)

type Writer interface {
	Writeln(s string) error
}

func NewStdoutWriter() *StdoutWriter {
	return &StdoutWriter{
		writer: bufio.NewWriter(os.Stdout),
	}
}

type StdoutWriter struct {
	writer *bufio.Writer
}

func (w *StdoutWriter) Writeln(s string) error {
	if _, err := fmt.Fprintln(w.writer, s); err != nil {
		return fmt.Errorf("fprintln: %w", err)
	}
	if err := w.writer.Flush(); err != nil {
		return fmt.Errorf("flush: %w", err)
	}
	return nil
}
