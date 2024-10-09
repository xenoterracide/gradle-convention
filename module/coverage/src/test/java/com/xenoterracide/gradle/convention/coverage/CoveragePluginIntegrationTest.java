// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.coverage;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CoveragePluginIntegrationTest {

  static final String GROOVY_SCRIPT =
    """
    plugins {
      id("com.xenoterracide.gradle.convention.coverage")
    }
    """;

  @TempDir
  File testProjectDir;

  @BeforeEach
  public void setupRunner() throws IOException {
    Files.writeString(testProjectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'hello-world'");
  }

  @Test
  void debug() throws IOException {
    Files.writeString(testProjectDir.toPath().resolve("build.gradle"), GROOVY_SCRIPT);
    var build = GradleRunner.create()
      .withDebug(true)
      .withProjectDir(testProjectDir)
      .withArguments("check", "--stacktrace")
      .withPluginClasspath()
      .build();

    assertThat(build.getOutput())
      .contains("BUILD SUCCESSFUL")
      .doesNotContain("Task :check UP-TO-DATE")
      .doesNotContain("BUILD FAILED");
  }
}
