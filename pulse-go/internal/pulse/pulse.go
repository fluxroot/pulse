/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

import (
	"bufio"
	"fmt"
	"os"
)

func NewPulse() *Pulse {
	return &Pulse{}
}

type Pulse struct {
}

func (p *Pulse) Run() error {
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		line := scanner.Text()
		switch line {
		case "uci":
			// TODO
		case "debug":
			// TODO
		case "isready":
			// TODO
		case "ucinewgame":
			// TODO
		case "position":
			// TODO
		case "go":
			// TODO
		case "stop":
			// TODO
		case "ponderhit":
			// TODO
		case "quit":
			// TODO
			return nil
		}
	}
	if err := scanner.Err(); err != nil {
		return fmt.Errorf("scan: %w", err)
	}
	return nil
}
