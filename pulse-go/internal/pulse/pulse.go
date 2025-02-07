/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

import (
	"fmt"

	"github.com/fluxroot/pulse/internal/pulse/engine"
	"github.com/fluxroot/pulse/internal/pulse/uci"
)

func NewPulse(sender *uci.DefaultSender) *Pulse {
	return &Pulse{
		sender: sender,
	}
}

type Pulse struct {
	sender *uci.DefaultSender
}

func (p *Pulse) Initialize() error {
	if err := p.Stop(); err != nil {
		return fmt.Errorf("stop: %w", err)
	}
	if err := p.sender.ID("Pulse Go 2.0.0", "Phokham Nonava"); err != nil {
		return fmt.Errorf("id: %w", err)
	}
	if err := p.sender.OK(); err != nil {
		return fmt.Errorf("ok: %w", err)
	}
	return nil
}

func (p *Pulse) Ready() error {
	if err := p.sender.ReadyOK(); err != nil {
		return fmt.Errorf("ready ok: %w", err)
	}
	return nil
}

func (p *Pulse) SetNameOnlyOption(name string) {
	panic("implement me")
}

func (p *Pulse) SetNameValueOption(name string, value string) {
	panic("implement me")
}

func (p *Pulse) NewGame() error {
	panic("implement me")
}

func (p *Pulse) Position(pos *engine.Position) {
	panic("implement me")
}

func (p *Pulse) Start() error {
	panic("implement me")
}

func (p *Pulse) Stop() error {
	panic("implement me")
}

func (p *Pulse) PonderHit() error {
	panic("implement me")
}

func (p *Pulse) Quit() error {
	panic("implement me")
}
