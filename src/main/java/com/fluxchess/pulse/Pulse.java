/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.AbstractEngine;
import com.fluxchess.jcpi.commands.*;

/**
 * Pulse uses the Java Chess Protocol Interface (JCPI) to handle the
 * UCI protocol. We simply extend AbstractEngine and implement the required
 * methods.
 */
public final class Pulse extends AbstractEngine {

  protected void quit() {
  }

  public void receive(EngineInitializeRequestCommand command) {
  }

  public void receive(EngineSetOptionCommand command) {
  }

  public void receive(EngineDebugCommand command) {
  }

  public void receive(EngineReadyRequestCommand command) {
  }

  public void receive(EngineNewGameCommand command) {
  }

  public void receive(EngineAnalyzeCommand command) {
  }

  public void receive(EnginePonderHitCommand command) {
  }

  public void receive(EngineStartCalculatingCommand command) {
  }

  public void receive(EngineStopCalculatingCommand command) {
  }

}
