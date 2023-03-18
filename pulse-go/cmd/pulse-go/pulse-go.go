/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package main

import (
	"fmt"
	"github.com/fluxroot/pulse/internal/pulse"
	"os"
	"strings"
)

func main() {
	args := os.Args[1:]
	if len(args) == 0 {
		pulse.NewPulse().Run()
	} else if (len(args)) == 1 && strings.EqualFold(args[0], "perft") {
		pulse.NewPerft().Run()
	} else {
		printUsage()
	}
}

func printUsage() {
	fmt.Println("Usage: pulse [perft]")
	os.Exit(1)
}
