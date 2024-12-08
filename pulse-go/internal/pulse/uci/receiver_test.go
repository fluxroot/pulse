/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package uci

import (
	"io"
	"iter"
	"slices"
	"strings"
	"testing"

	"github.com/fluxroot/pulse/internal/pulse/uci/mock"
)

func TestDefaultReceiver_Run(t *testing.T) {
	tests := []struct {
		name       string
		input      string
		debugMode  bool
		want       bool
		assertions func(engine *mock.MockEngine)
	}{
		{
			name:  "When EOF is received, it should quit the engine",
			input: "",
			assertions: func(engine *mock.MockEngine) {
				engine.EXPECT().Quit()
			},
		},
		{
			name:      "When whitespaces are received, it should ignore them",
			input:     "   debug\ton\n",
			debugMode: false,
			want:      true,
			assertions: func(engine *mock.MockEngine) {
				engine.EXPECT().Quit()
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			e := mock.NewMockEngine(ctrl)
			tt.assertions(e)
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			s.debugMode = tt.debugMode
			r := &DefaultReceiver{
				reader: newTestReader(tt.input),
				sender: s,
				engine: e,
			}
			_ = r.Run()
			if r.sender.debugMode != tt.want {
				t.Errorf("wanted debug mode to be %v, got %v", tt.want, r.sender.debugMode)
			}
		})
	}
}

func TestDefaultReceiver_Initialize(t *testing.T) {
	tests := []struct {
		name       string
		input      string
		assertions func(engine *mock.MockEngine)
	}{
		{
			name:  "When 'uci' is received, it should initialize the engine",
			input: "uci\n",
			assertions: func(engine *mock.MockEngine) {
				engine.EXPECT().Initialize()
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			e := mock.NewMockEngine(ctrl)
			e.EXPECT().Quit()
			tt.assertions(e)
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			r := &DefaultReceiver{
				reader: newTestReader(tt.input),
				sender: s,
				engine: e,
			}
			_ = r.Run()
		})
	}
}

func TestDefaultReceiver_Debug(t *testing.T) {
	tests := []struct {
		name      string
		input     string
		debugMode bool
		want      bool
	}{
		{
			name:      "When 'debug' is received, it should toggle debug mode",
			input:     "debug\n",
			debugMode: false,
			want:      true,
		},
		{
			name:      "When 'debug on' is received, it should turn on debug mode",
			input:     "debug on\n",
			debugMode: false,
			want:      true,
		},
		{
			name:      "When 'debug off' is received, it should turn off debug mode",
			input:     "debug off\n",
			debugMode: true,
			want:      false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			e := mock.NewMockEngine(ctrl)
			e.EXPECT().Quit()
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			s.debugMode = tt.debugMode
			r := &DefaultReceiver{
				reader: newTestReader(tt.input),
				sender: s,
				engine: e,
			}
			_ = r.Run()
			if r.sender.debugMode != tt.want {
				t.Errorf("wanted debug mode to be %v, got %v", tt.want, r.sender.debugMode)
			}
		})
	}
}

func TestDefaultReceiver_Ready(t *testing.T) {
	tests := []struct {
		name       string
		input      string
		assertions func(engine *mock.MockEngine)
	}{
		{
			name:  "When 'isready' is received, it should check the engine for readiness",
			input: "isready\n",
			assertions: func(engine *mock.MockEngine) {
				engine.EXPECT().Ready()
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			e := mock.NewMockEngine(ctrl)
			e.EXPECT().Quit()
			tt.assertions(e)
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			r := &DefaultReceiver{
				reader: newTestReader(tt.input),
				sender: s,
				engine: e,
			}
			_ = r.Run()
		})
	}
}

func TestDefaultReceiver_SetOption(t *testing.T) {
	tests := []struct {
		name       string
		input      string
		assertions func(engine *mock.MockEngine)
	}{
		{
			name:  "When 'setoption' with name only is received, it should set the option on the engine",
			input: "setoption name some option\n",
			assertions: func(engine *mock.MockEngine) {
				engine.EXPECT().SetNameOnlyOption(gomock.Eq("some option"))
			},
		},
		{
			name:  "When 'setoption' with name and value is received, it should set the option on the engine",
			input: "setoption name some option value some value\n",
			assertions: func(engine *mock.MockEngine) {
				engine.EXPECT().SetNameValueOption(gomock.Eq("some option"), gomock.Eq("some value"))
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			e := mock.NewMockEngine(ctrl)
			e.EXPECT().Quit()
			tt.assertions(e)
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			r := &DefaultReceiver{
				reader: newTestReader(tt.input),
				sender: s,
				engine: e,
			}
			_ = r.Run()
		})
	}
}

func TestDefaultReceiver_Register(t *testing.T) {
	tests := []struct {
		name      string
		input     string
		debugMode bool
		output    string
	}{
		{
			name:      "When 'register' is received and debug mode is true, it should send an error back",
			input:     "register\n",
			debugMode: true,
			output:    "info string Unsupported command: register\n",
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			e := mock.NewMockEngine(ctrl)
			e.EXPECT().Quit()
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			s.debugMode = tt.debugMode
			r := &DefaultReceiver{
				reader: newTestReader(tt.input),
				sender: s,
				engine: e,
			}
			_ = r.Run()
			if w.result != tt.output {
				t.Errorf("wanted output to be %v, got %v", tt.output, w.result)
			}
		})
	}
}

func TestDefaultReceiver_NewGame(t *testing.T) {
	tests := []struct {
		name       string
		input      string
		assertions func(engine *mock.MockEngine)
	}{
		{
			name:  "When 'ucinewgame' is received, it should start a new game on the engine",
			input: "ucinewgame\n",
			assertions: func(engine *mock.MockEngine) {
				engine.EXPECT().NewGame()
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			e := mock.NewMockEngine(ctrl)
			e.EXPECT().Quit()
			tt.assertions(e)
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			r := &DefaultReceiver{
				reader: newTestReader(tt.input),
				sender: s,
				engine: e,
			}
			_ = r.Run()
		})
	}
}

func TestDefaultReceiver_Stop(t *testing.T) {
	tests := []struct {
		name       string
		input      string
		assertions func(engine *mock.MockEngine)
	}{
		{
			name:  "When 'stop' is received, it should stop the engine",
			input: "stop\n",
			assertions: func(engine *mock.MockEngine) {
				engine.EXPECT().Stop()
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			e := mock.NewMockEngine(ctrl)
			e.EXPECT().Quit()
			tt.assertions(e)
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			r := &DefaultReceiver{
				reader: newTestReader(tt.input),
				sender: s,
				engine: e,
			}
			_ = r.Run()
		})
	}
}

func TestDefaultReceiver_PonderHit(t *testing.T) {
	tests := []struct {
		name       string
		input      string
		assertions func(engine *mock.MockEngine)
	}{
		{
			name:  "When 'ponderhit' is received, it should call ponderhit on the engine",
			input: "ponderhit\n",
			assertions: func(engine *mock.MockEngine) {
				engine.EXPECT().PonderHit()
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			e := mock.NewMockEngine(ctrl)
			e.EXPECT().Quit()
			tt.assertions(e)
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			r := &DefaultReceiver{
				reader: newTestReader(tt.input),
				sender: s,
				engine: e,
			}
			_ = r.Run()
		})
	}
}

func TestDefaultReceiver_Quit(t *testing.T) {
	tests := []struct {
		name       string
		input      string
		assertions func(engine *mock.MockEngine)
	}{
		{
			name:  "When 'quit' is received, it should quit the engine",
			input: "quit\n",
			assertions: func(engine *mock.MockEngine) {
				engine.EXPECT().Quit()
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			e := mock.NewMockEngine(ctrl)
			tt.assertions(e)
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			r := &DefaultReceiver{
				reader: newTestReader(tt.input),
				sender: s,
				engine: e,
			}
			_ = r.Run()
		})
	}
}

type testReader struct {
	iterator func() (string, bool)
	stop     func()
}

func newTestReader(input string) *testReader {
	lines := strings.Split(input, "\n")
	seq := slices.Values(lines)
	iterator, stop := iter.Pull(seq)
	return &testReader{
		iterator: iterator,
		stop:     stop,
	}
}

func (r *testReader) Readln() (string, error) {
	line, ok := r.iterator()
	if !ok {
		return "", io.EOF
	}
	return line, nil
}
