/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package protocol

//go:generate mockgen -source=sender.go -destination=mock/sender.go -package=mock

type Sender interface {
	Id(name string, author string) error
	Ok() error
}
