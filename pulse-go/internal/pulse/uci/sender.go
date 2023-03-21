/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package uci

import (
	"bufio"
	"fmt"
)

func NewSender(writer *bufio.Writer) *Sender {
	return &Sender{
		writer: writer,
	}
}

type Sender struct {
	writer *bufio.Writer
}

func (s *Sender) Id(name string, author string) error {
	if _, err := fmt.Fprintln(s.writer, "id name", name); err != nil {
		return fmt.Errorf("fprintln: %w", err)
	}
	if _, err := fmt.Fprintln(s.writer, "id author", author); err != nil {
		return fmt.Errorf("fprintln: %w", err)
	}
	if err := s.writer.Flush(); err != nil {
		return fmt.Errorf("flush: %w", err)
	}
	return nil
}

func (s *Sender) Ok() error {
	if _, err := fmt.Fprintln(s.writer, "uciok"); err != nil {
		return fmt.Errorf("fprintln: %w", err)
	}
	if err := s.writer.Flush(); err != nil {
		return fmt.Errorf("flush: %w", err)
	}
	return nil
}
