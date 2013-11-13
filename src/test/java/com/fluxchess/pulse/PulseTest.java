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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertFalse;

public class PulseTest {

  private BufferedReader testInput = null;
  private PrintStream testOutput = null;
  private BufferedReader engineInput = null;
  private PrintStream engineOutput = null;

  @Before
  public void setUp() throws IOException {
    PipedInputStream testInputPipe = new PipedInputStream();
    PipedOutputStream testOutputPipe = new PipedOutputStream();
    PipedInputStream engineInputPipe = new PipedInputStream(testOutputPipe);
    PipedOutputStream engineOutputPipe = new PipedOutputStream(testInputPipe);

    testInput = new BufferedReader(new InputStreamReader(testInputPipe));
    testOutput = new PrintStream(testOutputPipe);
    engineInput = new BufferedReader(new InputStreamReader(engineInputPipe));
    engineOutput = new PrintStream(engineOutputPipe);
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testStartStop() throws InterruptedException {
    Pulse engine = new Pulse(engineInput, engineOutput);
    Thread thread = new Thread(engine);
    thread.start();

    testOutput.println("uci");
    testOutput.println("quit");

    thread.join(1000);
    assertFalse(thread.isAlive());
  }

}
