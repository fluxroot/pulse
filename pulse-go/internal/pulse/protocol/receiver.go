/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package protocol

//go:generate mockgen -source=receiver.go -destination=mock/receiver.go -package=mock

type Receiver interface {
	Receive() error
}
