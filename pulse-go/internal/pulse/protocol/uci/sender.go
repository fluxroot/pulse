/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package uci

import (
	"github.com/fluxroot/pulse/internal/pulse/protocol"
)

func NewSender() protocol.Sender {
	return &sender{}
}

type sender struct {
}
