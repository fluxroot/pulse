/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package uci

import "github.com/fluxroot/pulse/internal/pulse/engine"

//go:generate mockgen -source=engine.go -destination=mock/engine.go -package=mock

type Engine interface {
	Initialize() error
	Ready() error
	SetNameOnlyOption(name string)
	SetNameValueOption(name string, value string)
	NewGame() error
	Position(p *engine.Position)
	Start() error
	Stop() error
	PonderHit() error
	Quit() error
}
