/*
 * Copyright 2013-2023 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package uci

import (
	"fmt"
	"io"
	"regexp"
	"strings"
)

var tokenRegex = regexp.MustCompile(`\s+`)
var nameValueOptionRegex = regexp.MustCompile(`^name\s+(?P<name>.+?)\s+value\s+(?P<value>.+)`)
var nameOnlyOptionRegex = regexp.MustCompile(`^name\s+(?P<name>.+)`)

type Receiver interface {
	Run() error
}

func NewDefaultReceiver(reader Reader, sender *DefaultSender, engine Engine) *DefaultReceiver {
	return &DefaultReceiver{
		reader: reader,
		sender: sender,
		engine: engine,
	}
}

type DefaultReceiver struct {
	reader Reader
	sender *DefaultSender
	engine Engine
}

func (r *DefaultReceiver) Run() error {
	for {
		line, err := r.reader.Readln()
		if err == io.EOF {
			_ = r.engine.Quit()
			return nil
		}
		if err != nil {
			_ = r.engine.Quit()
			return err
		}
		tokens := tokenRegex.Split(strings.TrimSpace(line), 2)
		if len(tokens) == 0 {
			continue
		}
		switch tokens[0] {
		case "uci":
			if err := r.engine.Initialize(); err != nil {
				return fmt.Errorf("initialize: %w", err)
			}
		case "debug":
			r.parseDebug(tokens)
		case "isready":
			if err := r.engine.Ready(); err != nil {
				return fmt.Errorf("ready: %w", err)
			}
		case "setoption":
			r.parseSetOption(tokens)
		case "register":
			if err := r.sender.Debug("Unsupported command: register"); err != nil {
				return fmt.Errorf("debug: %w", err)
			}
		case "ucinewgame":
			if err := r.engine.NewGame(); err != nil {
				return fmt.Errorf("new game: %w", err)
			}
		case "position":
			parsePosition()
		case "go":
			parseGo()
		case "stop":
			if err := r.engine.Stop(); err != nil {
				return fmt.Errorf("stop: %w", err)
			}
		case "ponderhit":
			if err := r.engine.PonderHit(); err != nil {
				return fmt.Errorf("ponder hit: %w", err)
			}
		case "quit":
			if err := r.engine.Quit(); err != nil {
				return fmt.Errorf("quit: %w", err)
			}
			return nil
		default:
			if err := r.sender.Debug(fmt.Sprintf("Unknown command: %s", tokens[0])); err != nil {
				return fmt.Errorf("debug: %w", err)
			}
		}
	}
}

func (r *DefaultReceiver) parseDebug(tokens []string) {
	switch len(tokens) {
	case 1:
		r.sender.debugMode = !r.sender.debugMode // Toggle debug
	case 2:
		switch tokens[1] {
		case "on":
			r.sender.debugMode = true
		case "off":
			r.sender.debugMode = false
		default:
			_ = r.sender.Debug(fmt.Sprintf("Unknown argument: %s", tokens[1]))
		}
	}
}

func (r *DefaultReceiver) parseSetOption(tokens []string) {
	if len(tokens) != 2 {
		_ = r.sender.Debug("Argument required")
		return
	}
	match := nameValueOptionRegex.FindStringSubmatch(tokens[1])
	if match != nil {
		result := map[string]string{}
		for i, name := range nameValueOptionRegex.SubexpNames() {
			if i != 0 && name != "" {
				result[name] = match[i]
			}
		}
		r.engine.SetNameValueOption(result["name"], result["value"])
		return
	}
	match = nameOnlyOptionRegex.FindStringSubmatch(tokens[1])
	if match != nil {
		result := map[string]string{}
		for i, name := range nameOnlyOptionRegex.SubexpNames() {
			if i != 0 && name != "" {
				result[name] = match[i]
			}
		}
		r.engine.SetNameOnlyOption(result["name"])
		return
	}
	_ = r.sender.Debug(fmt.Sprintf("Error parsing argument: %s", tokens[1]))
}

func parsePosition() {
}

func parseGo() {
}
