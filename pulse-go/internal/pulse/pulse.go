/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package pulse

import "github.com/fluxroot/pulse/internal/pulse/protocol"

func NewPulse() protocol.Engine {
	return &Pulse{}
}

type Pulse struct {
}

func (p *Pulse) Initialize() {
	// TODO
}

func (p *Pulse) Debug() {
	// TODO
}

func (p *Pulse) Ready() {
	// TODO
}

func (p *Pulse) NewGame() {
	// TODO
}

func (p *Pulse) Position() {
	// TODO
}

func (p *Pulse) Start() {
	// TODO
}

func (p *Pulse) Stop() {
	// TODO
}

func (p *Pulse) PonderHit() {
	// TODO
}

func (p *Pulse) Quit() {
	// TODO
}
