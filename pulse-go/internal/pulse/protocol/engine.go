/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package protocol

//go:generate mockgen -source=engine.go -destination=mock/engine.go -package=mock

type Engine interface {
	Initialize()
	Debug()
	Ready()
	NewGame()
	Position()
	Start()
	Stop()
	PonderHit()
	Quit()
}
