/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package uci

import (
	"fmt"
	"testing"
)

func TestDefaultSender_ID(t *testing.T) {
	type args struct {
		name   string
		author string
	}
	tests := []struct {
		name    string
		args    args
		want    string
		wantErr bool
	}{
		{
			name: "When ID() is called with name and author, it should print a valid string",
			args: args{"some-name", "some-author"},
			want: `id name some-name
id author some-author
`,
			wantErr: false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			if err := s.ID(tt.args.name, tt.args.author); (err != nil) != tt.wantErr {
				t.Errorf("ID() error = %v, wantErr %v", err, tt.wantErr)
			}
			if w.result != tt.want {
				t.Errorf("ID() result = %v, want %v", w.result, tt.want)
			}
		})
	}
}

func TestDefaultSender_OK(t *testing.T) {
	tests := []struct {
		name    string
		want    string
		wantErr bool
	}{
		{
			name:    "When OK() is called, it should print 'uciok'",
			want:    "uciok\n",
			wantErr: false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			if err := s.OK(); (err != nil) != tt.wantErr {
				t.Errorf("OK() error = %v, wantErr %v", err, tt.wantErr)
			}
			if w.result != tt.want {
				t.Errorf("OK() result = %v, want %v", w.result, tt.want)
			}
		})
	}
}

func TestDefaultSender_ReadyOK(t *testing.T) {
	tests := []struct {
		name    string
		want    string
		wantErr bool
	}{
		{
			name:    "When ReadyOK() is called, it should print 'readyok'",
			want:    "readyok\n",
			wantErr: false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			if err := s.ReadyOK(); (err != nil) != tt.wantErr {
				t.Errorf("ReadyOK() error = %v, wantErr %v", err, tt.wantErr)
			}
			if w.result != tt.want {
				t.Errorf("ReadyOK() result = %v, want %v", w.result, tt.want)
			}
		})
	}
}

func TestDefaultSender_Debug(t *testing.T) {
	type args struct {
		message string
	}
	tests := []struct {
		name      string
		debugMode bool
		args      args
		want      string
		wantErr   bool
	}{
		{
			name:      "When Debug() is called and debugMode is false, it should print nothing",
			debugMode: false,
			args:      args{"some message"},
			want:      "",
			wantErr:   false,
		},
		{
			name:      "When Debug() is called and debugMode is true, it should print the message",
			debugMode: true,
			args:      args{"some message"},
			want:      "info string some message\n",
			wantErr:   false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			w := &testWriter{result: ""}
			s := NewDefaultSender(w)
			s.debugMode = tt.debugMode
			if err := s.Debug(tt.args.message); (err != nil) != tt.wantErr {
				t.Errorf("Debug() error = %v, wantErr %v", err, tt.wantErr)
			}
			if w.result != tt.want {
				t.Errorf("Debug() result = %v, want %v", w.result, tt.want)
			}
		})
	}
}

type testWriter struct {
	result string
}

func (w *testWriter) Writeln(s string) error {
	w.result += fmt.Sprintf("%s\n", s)
	return nil
}
