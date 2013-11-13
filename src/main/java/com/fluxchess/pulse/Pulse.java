/*
 * Copyright 2013 the original author or authors.
 *
 * This file is part of Pulse Chess.
 *
 * Pulse Chess is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pulse Chess is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pulse Chess.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.AbstractEngine;
import com.fluxchess.jcpi.commands.*;

import java.io.BufferedReader;
import java.io.PrintStream;

/**
 * Pulse uses the Java Chess Protocol Interface (JCPI) to handle the
 * UCI protocol.
 * <p/>
 * We simply extend AbstractEngine and implement the required methods.
 */
public final class Pulse extends AbstractEngine {

  public static void main(String[] args) {
    // Don't do any fancy stuff here. Just create our engine and
    // run it. JCPI takes care of the rest. It waits for the GUI
    // to issue commands which will call our methods using the
    // visitor pattern.
    AbstractEngine engine = new Pulse();
    engine.run();
  }

  public Pulse() {
  }

  public Pulse(BufferedReader input, PrintStream output) {
    super(input, output);
  }

  @Override
  protected void quit() {
    // We received a quit command. Stop calculating now and
    // cleanup!
    new EngineStopCalculatingCommand().accept(this);
  }

  @Override
  public void receive(EngineInitializeRequestCommand command) {
    // We received an initialization request. Stop calculating now!
    new EngineStopCalculatingCommand().accept(this);

    // We must send an initialization answer back!
    getProtocol().send(new ProtocolInitializeAnswerCommand(
      VersionInfo.current().toString(),
      "Flux Chess Project"
    ));
  }

  @Override
  public void receive(EngineSetOptionCommand command) {
  }

  @Override
  public void receive(EngineDebugCommand command) {
  }

  @Override
  public void receive(EngineReadyRequestCommand command) {
    // We received a ready request. We must send the token back!
    getProtocol().send(new ProtocolReadyAnswerCommand(command.token));
  }

  @Override
  public void receive(EngineNewGameCommand command) {
  }

  @Override
  public void receive(EngineAnalyzeCommand command) {
  }

  @Override
  public void receive(EngineStartCalculatingCommand command) {
  }

  @Override
  public void receive(EngineStopCalculatingCommand command) {
  }

  @Override
  public void receive(EnginePonderHitCommand command) {
  }

}
