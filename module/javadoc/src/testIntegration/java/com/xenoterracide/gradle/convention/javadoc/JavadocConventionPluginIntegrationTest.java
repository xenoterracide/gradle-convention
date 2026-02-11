// SPDX-FileCopyrightText: Copyright © 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.javadoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class JavadocConventionPluginIntegrationTest {

  @TempDir
  Path testProjectDir;

  @Test
  void canApplyPlugin() throws IOException {
    var buildFile = testProjectDir.resolve("build.gradle");
    Files.writeString(
      buildFile,
      """
      plugins {
        id 'java'
        id 'com.xenoterracide.gradle.convention.javadoc'
      }
      """
    );

    var settingsFile = testProjectDir.resolve("settings.gradle");
    Files.writeString(settingsFile, "rootProject.name = 'test'");

    var result = GradleRunner.create()
      .withProjectDir(testProjectDir.toFile())
      .withArguments("tasks", "--all")
      .withPluginClasspath()
      .build();

    assertThat(result.getOutput()).contains("javadoc");
  }
}
