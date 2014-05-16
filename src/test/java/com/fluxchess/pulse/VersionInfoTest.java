/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VersionInfoTest {

  @Test
  public void testVersionInfo() {
    VersionInfo versionInfo = VersionInfo.current();

    assertEquals("0.1.0-alpha.1", versionInfo.getVersion());
    assertEquals("dev", versionInfo.getBuildNumber());
    assertEquals("rev", versionInfo.getRevisionNumber());
  }

}
