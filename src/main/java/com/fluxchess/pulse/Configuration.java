/*
 * Copyright 2013-2014 the original author or authors.
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

import com.fluxchess.jcpi.options.AbstractOption;
import com.fluxchess.jcpi.options.CheckboxOption;
import com.fluxchess.jcpi.options.Options;

public final class Configuration {

  public static boolean debug = false;

  public static boolean ponder = true;
  public static final CheckboxOption ponderOption = Options.newPonderOption(ponder);

  public static final AbstractOption[] options = new AbstractOption[]{
    ponderOption
  };

  private Configuration() {
  }

}
