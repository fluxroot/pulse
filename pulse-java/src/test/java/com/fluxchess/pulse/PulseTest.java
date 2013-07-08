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
import com.fluxchess.jcpi.IGui;
import com.fluxchess.jcpi.commands.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PulseTest extends AbstractCommunication implements IGui {

    private static String READY_REQUEST_TOKEN = "1234";

    private Queue<IEngineCommand> commandQueue = new ConcurrentLinkedQueue<IEngineCommand>();
    private IEngineCommand currentCommand = null;

    /**
     * Let's test whether our engine can execute the following commands
     * in sequence:
     *
     * <ul>
     *   <li>EngineInitializeRequestCommand</li>
     *   <li>EngineReadyRequestCommand</li>
     *   <li>EngineQuitCommand</li>
     * </ul>
     * <p>
     * After each step currentCommand will be set to null. This allows
     * us to check whether the previous command has been executed
     * successfully.
     */
    @Test
    public void testStartStop() {
        commandQueue.clear();

        EngineInitializeRequestCommand engineInitializeRequestCommand = new EngineInitializeRequestCommand();
        commandQueue.add(engineInitializeRequestCommand);

        EngineReadyRequestCommand engineReadyRequestCommand = new EngineReadyRequestCommand(READY_REQUEST_TOKEN);
        commandQueue.add(engineReadyRequestCommand);

        EngineQuitCommand engineQuitCommand = new EngineQuitCommand();
        commandQueue.add(engineQuitCommand);

        currentCommand = null;

        // Run our engine single threaded
        AbstractEngine engine = new Pulse(this);
        engine.run();

        if (!(currentCommand instanceof EngineQuitCommand)) {
            Assert.fail();
        }
    }

    public void send(IGuiCommand iGuiCommand) {
        iGuiCommand.accept(this);
    }

    protected IEngineCommand receive() {
        if (commandQueue.isEmpty()) {
            Assert.fail();
        } else if (currentCommand != null) {
            // The currentCommand will be null if the engine recognized our previous command
            Assert.fail();
        } else {
            currentCommand = commandQueue.remove();
        }

        return currentCommand;
    }

    public void visit(GuiInitializeAnswerCommand guiInitializeAnswerCommand) {
        if (!(currentCommand instanceof EngineInitializeRequestCommand)) {
            Assert.fail();
        } else {
            currentCommand = null;
        }
    }

    public void visit(GuiReadyAnswerCommand guiReadyAnswerCommand) {
        if (!(currentCommand instanceof EngineReadyRequestCommand) || !guiReadyAnswerCommand.token.equals(READY_REQUEST_TOKEN)) {
            Assert.fail();
        } else {
            currentCommand = null;
        }
    }

    public void visit(GuiBestMoveCommand guiBestMoveCommand) {
        // Ignore GuiBestMoveCommand
    }

    public void visit(GuiInformationCommand guiInformationCommand) {
        // Ignore GuiInformationCommand
    }

    public void visit(GuiQuitCommand guiQuitCommand) {
        // Ignore GuiQuitCommand
    }

}
