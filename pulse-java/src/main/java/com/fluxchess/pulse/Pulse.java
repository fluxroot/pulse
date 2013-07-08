/*
 * Copyright 2013 Phokham Nonava
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

import com.fluxchess.jcpi.AbstractCommunication;
import com.fluxchess.jcpi.AbstractEngine;
import com.fluxchess.jcpi.commands.*;
import com.fluxchess.jcpi.standardio.StandardIoCommunication;

public final class Pulse extends AbstractEngine {

    public static void main(String[] args) {
        AbstractEngine engine = new Pulse(new StandardIoCommunication());
        engine.run();
    }

    public Pulse(AbstractCommunication communication) {
        super(communication);
    }

    protected void quit() {
    }

    public void visit(EngineInitializeRequestCommand engineInitializeRequestCommand) {
    }

    public void visit(EngineSetOptionCommand engineSetOptionCommand) {
    }

    public void visit(EngineDebugCommand engineDebugCommand) {
    }

    public void visit(EngineReadyRequestCommand engineReadyRequestCommand) {
    }

    public void visit(EngineNewGameCommand engineNewGameCommand) {
    }

    public void visit(EngineAnalyzeCommand engineAnalyzeCommand) {
    }

    public void visit(EngineStartCalculatingCommand engineStartCalculatingCommand) {
    }

    public void visit(EngineStopCalculatingCommand engineStopCalculatingCommand) {
    }

    public void visit(EnginePonderHitCommand enginePonderHitCommand) {
    }

}
