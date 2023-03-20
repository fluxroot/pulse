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
	"io"
)

func NewReceiver(reader io.Reader, engine protocol.Engine) protocol.Receiver {
	return &receiver{
		reader: reader,
		engine: engine,
	}
}

type receiver struct {
	reader io.Reader
	engine protocol.Engine
}

func (r *receiver) Receive() error {
	scanner := bufio.NewScanner(r.reader)
	for scanner.Scan() {
		line := scanner.Text()
		switch line {
		case "uci":
			r.engine.Initialize()
		case "debug":
			r.engine.Debug()
		case "isready":
			r.engine.Ready()
		case "ucinewgame":
			r.engine.NewGame()
		case "position":
			r.engine.Position()
		case "go":
			r.engine.Start()
		case "stop":
			r.engine.Stop()
		case "ponderhit":
			r.engine.PonderHit()
		case "quit":
			r.engine.Quit()
			return nil
		}
	}
	if err := scanner.Err(); err != nil {
		return fmt.Errorf("scan: %w", err)
	}
	return nil
}
