/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package uci

import (
	"fmt"
)

type Sender interface {
	ID(name string, author string) error
	OK() error
	ReadyOK() error
	Debug(message string) error
}

func NewDefaultSender(writer Writer) *DefaultSender {
	return &DefaultSender{
		writer:    writer,
		debugMode: false,
	}
}

type DefaultSender struct {
	writer    Writer
	debugMode bool
}

func (s *DefaultSender) ID(name string, author string) error {
	if err := s.writer.Writeln(fmt.Sprintf("id name %s", name)); err != nil {
		return err
	}
	if err := s.writer.Writeln(fmt.Sprintf("id author %s", author)); err != nil {
		return err
	}
	return nil
}

func (s *DefaultSender) OK() error {
	if err := s.writer.Writeln("uciok"); err != nil {
		return err
	}
	return nil
}

func (s *DefaultSender) ReadyOK() error {
	if err := s.writer.Writeln("readyok"); err != nil {
		return err
	}
	return nil
}

func (s *DefaultSender) Debug(message string) error {
	if s.debugMode {
		if err := s.writer.Writeln(fmt.Sprintf("info string %s", message)); err != nil {
			return err
		}
	}
	return nil
}
