// Code generated by MockGen. DO NOT EDIT.
// Source: engine.go

// Package mock is a generated GoMock package.
package mock

import (
	reflect "reflect"

	gomock "github.com/golang/mock/gomock"
)

// MockEngine is a mock of Engine interface.
type MockEngine struct {
	ctrl     *gomock.Controller
	recorder *MockEngineMockRecorder
}

// MockEngineMockRecorder is the mock recorder for MockEngine.
type MockEngineMockRecorder struct {
	mock *MockEngine
}

// NewMockEngine creates a new mock instance.
func NewMockEngine(ctrl *gomock.Controller) *MockEngine {
	mock := &MockEngine{ctrl: ctrl}
	mock.recorder = &MockEngineMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use.
func (m *MockEngine) EXPECT() *MockEngineMockRecorder {
	return m.recorder
}

// Debug mocks base method.
func (m *MockEngine) Debug() error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Debug")
	ret0, _ := ret[0].(error)
	return ret0
}

// Debug indicates an expected call of Debug.
func (mr *MockEngineMockRecorder) Debug() *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Debug", reflect.TypeOf((*MockEngine)(nil).Debug))
}

// Initialize mocks base method.
func (m *MockEngine) Initialize() error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Initialize")
	ret0, _ := ret[0].(error)
	return ret0
}

// Initialize indicates an expected call of Initialize.
func (mr *MockEngineMockRecorder) Initialize() *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Initialize", reflect.TypeOf((*MockEngine)(nil).Initialize))
}

// NewGame mocks base method.
func (m *MockEngine) NewGame() error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "NewGame")
	ret0, _ := ret[0].(error)
	return ret0
}

// NewGame indicates an expected call of NewGame.
func (mr *MockEngineMockRecorder) NewGame() *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "NewGame", reflect.TypeOf((*MockEngine)(nil).NewGame))
}

// PonderHit mocks base method.
func (m *MockEngine) PonderHit() error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "PonderHit")
	ret0, _ := ret[0].(error)
	return ret0
}

// PonderHit indicates an expected call of PonderHit.
func (mr *MockEngineMockRecorder) PonderHit() *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "PonderHit", reflect.TypeOf((*MockEngine)(nil).PonderHit))
}

// Position mocks base method.
func (m *MockEngine) Position() error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Position")
	ret0, _ := ret[0].(error)
	return ret0
}

// Position indicates an expected call of Position.
func (mr *MockEngineMockRecorder) Position() *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Position", reflect.TypeOf((*MockEngine)(nil).Position))
}

// Quit mocks base method.
func (m *MockEngine) Quit() error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Quit")
	ret0, _ := ret[0].(error)
	return ret0
}

// Quit indicates an expected call of Quit.
func (mr *MockEngineMockRecorder) Quit() *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Quit", reflect.TypeOf((*MockEngine)(nil).Quit))
}

// Ready mocks base method.
func (m *MockEngine) Ready() error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Ready")
	ret0, _ := ret[0].(error)
	return ret0
}

// Ready indicates an expected call of Ready.
func (mr *MockEngineMockRecorder) Ready() *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Ready", reflect.TypeOf((*MockEngine)(nil).Ready))
}

// Start mocks base method.
func (m *MockEngine) Start() error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Start")
	ret0, _ := ret[0].(error)
	return ret0
}

// Start indicates an expected call of Start.
func (mr *MockEngineMockRecorder) Start() *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Start", reflect.TypeOf((*MockEngine)(nil).Start))
}

// Stop mocks base method.
func (m *MockEngine) Stop() error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Stop")
	ret0, _ := ret[0].(error)
	return ret0
}

// Stop indicates an expected call of Stop.
func (mr *MockEngineMockRecorder) Stop() *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Stop", reflect.TypeOf((*MockEngine)(nil).Stop))
}
