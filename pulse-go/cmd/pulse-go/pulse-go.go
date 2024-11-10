/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strings"

	"github.com/fluxroot/pulse/internal/pulse"
	"github.com/fluxroot/pulse/internal/pulse/uci"
)

func main() {
	args := os.Args[1:]
	switch {
	case len(args) == 0:
		sender := uci.NewSender(bufio.NewWriter(os.Stdout))
		engine := pulse.NewPulse(sender)
		receiver := uci.NewReceiver(bufio.NewScanner(os.Stdin), engine)
		if err := receiver.Run(); err != nil {
			log.Fatalf("Error: %v", err)
		}
	case (len(args)) == 1 && strings.EqualFold(args[0], "perft"):
		pulse.NewPerft().Run()
	default:
		printUsage()
		os.Exit(1)
	}
}

func printUsage() {
	_, _ = fmt.Fprintf(os.Stderr, "Usage: pulse-go [perft]")
}
