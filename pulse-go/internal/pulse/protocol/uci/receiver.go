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
	"github.com/fluxroot/pulse/internal/pulse/protocol"
)

func NewReceiver(scanner *bufio.Scanner, engine protocol.Engine) *Receiver {
	return &Receiver{
		scanner: scanner,
		engine:  engine,
	}
}

type Receiver struct {
	scanner *bufio.Scanner
	engine  protocol.Engine
}

func (r *Receiver) Run() error {
	for r.scanner.Scan() {
		line := r.scanner.Text()
		switch line {
		case "uci":
			if err := r.engine.Initialize(); err != nil {
				return fmt.Errorf("initialize: %w", err)
			}
		case "debug":
			if err := r.engine.Debug(); err != nil {
				return fmt.Errorf("debug: %w", err)
			}
		case "isready":
			if err := r.engine.Ready(); err != nil {
				return fmt.Errorf("ready: %w", err)
			}
		case "ucinewgame":
			if err := r.engine.NewGame(); err != nil {
				return fmt.Errorf("new game: %w", err)
			}
		case "position":
			if err := r.engine.Position(); err != nil {
				return fmt.Errorf("position: %w", err)
			}
		case "go":
			if err := r.engine.Start(); err != nil {
				return fmt.Errorf("start: %w", err)
			}
		case "stop":
			if err := r.engine.Stop(); err != nil {
				return fmt.Errorf("stop: %w", err)
			}
		case "ponderhit":
			if err := r.engine.PonderHit(); err != nil {
				return fmt.Errorf("ponder hit: %w", err)
			}
		case "quit":
			if err := r.engine.Quit(); err != nil {
				return fmt.Errorf("quit: %w", err)
			}
			return nil
		}
	}
	if err := r.scanner.Err(); err != nil {
		return fmt.Errorf("scan: %w", err)
	}
	return nil
}
