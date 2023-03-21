/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

import (
	"fmt"
	"github.com/fluxroot/pulse/internal/pulse/uci"
)

func NewPulse(sender *uci.Sender) *Pulse {
	return &Pulse{
		sender: sender,
	}
}

type Pulse struct {
	sender *uci.Sender
}

func (p *Pulse) Initialize() error {
	if err := p.Stop(); err != nil {
		return fmt.Errorf("stop: %w", err)
	}
	if err := p.sender.Id("Pulse Go 2.0.0", "Phokham Nonava"); err != nil {
		return fmt.Errorf("id: %w", err)
	}
	if err := p.sender.Ok(); err != nil {
		return fmt.Errorf("ok: %w", err)
	}
	return nil
}

func (p *Pulse) Debug() error {
	// TODO
	return nil
}

func (p *Pulse) Ready() error {
	// TODO
	return nil
}

func (p *Pulse) NewGame() error {
	// TODO
	return nil
}

func (p *Pulse) Position() error {
	// TODO
	return nil
}

func (p *Pulse) Start() error {
	// TODO
	return nil
}

func (p *Pulse) Stop() error {
	// TODO
	return nil
}

func (p *Pulse) PonderHit() error {
	// TODO
	return nil
}

func (p *Pulse) Quit() error {
	// TODO
	return nil
}
