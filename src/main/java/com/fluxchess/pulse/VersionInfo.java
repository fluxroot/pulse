/*
 * Copyright (C) 2013-2014 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

final class VersionInfo {

  private static final String versionInfoProperty = "/com/fluxchess/pulse/version-info.properties";
  private static final VersionInfo CURRENT;

  private final String version;
  private final String buildNumber;
  private final String revisionNumber;

  static {
    try (InputStream inputStream = VersionInfo.class.getResourceAsStream(versionInfoProperty)) {
      if (inputStream != null) {
        Properties properties = new Properties();
        properties.load(inputStream);

        CURRENT = new VersionInfo(
            properties.getProperty("version", "n/a"),
            properties.getProperty("buildNumber", "n/a"),
            properties.getProperty("revisionNumber", "n/a")
        );
      } else {
        throw new FileNotFoundException(String.format("Cannot find the properties file %s", versionInfoProperty));
      }
    } catch (IOException e) {
      throw new RuntimeException(String.format("Cannot load the properties file %s", versionInfoProperty), e);
    }
  }

  static VersionInfo current() {
    return CURRENT;
  }

  private VersionInfo(String version, String buildNumber, String revisionNumber) {
    Objects.requireNonNull(version);
    Objects.requireNonNull(buildNumber);
    Objects.requireNonNull(revisionNumber);

    this.version = version;
    this.buildNumber = buildNumber;
    this.revisionNumber = revisionNumber;
  }

  @Override
  public String toString() {
    return String.format("Pulse %s", version);
  }

  String getVersion() {
    return version;
  }

  String getBuildNumber() {
    return buildNumber;
  }

  String getRevisionNumber() {
    return revisionNumber;
  }

}
