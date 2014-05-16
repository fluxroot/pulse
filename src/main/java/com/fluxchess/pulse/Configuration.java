/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.options.AbstractOption;
import com.fluxchess.jcpi.options.CheckboxOption;
import com.fluxchess.jcpi.options.Options;

final class Configuration {

  static boolean debug = false;

  static boolean ponder = true;
  static final CheckboxOption ponderOption = Options.newPonderOption(ponder);

  static final AbstractOption[] options = new AbstractOption[]{
      ponderOption
  };

  private Configuration() {
  }

}
