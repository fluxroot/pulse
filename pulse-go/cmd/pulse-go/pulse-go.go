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
	"github.com/fluxroot/pulse/internal/pulse"
	"github.com/fluxroot/pulse/internal/pulse/uci"
	"log"
	"os"
	"strings"
)

func main() {
	args := os.Args[1:]
	if len(args) == 0 {
		sender := uci.NewSender(bufio.NewWriter(os.Stdout))
		engine := pulse.NewPulse(sender)
		receiver := uci.NewReceiver(bufio.NewScanner(os.Stdin), engine)
		if err := receiver.Run(); err != nil {
			log.Fatalf("Error: %v", err)
		}
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
