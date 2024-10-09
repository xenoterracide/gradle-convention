// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.coverage;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.file.PathUtils;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoveragePluginTest {

  static final String GROOVY_SCRIPT =
    """
    plugins {
      id("com.xenoterracide.gradle.convention.coverage")
    }
    """;

  Logger log = LoggerFactory.getLogger(this.getClass());

  @TempDir
  Path testProjectDir;

  @BeforeEach
  @SuppressWarnings("NullAway")
  public void setupRunner() throws IOException {
    Files.writeString(testProjectDir.resolve("settings.gradle"), "rootProject.name = 'hello-world'");
    var pathToCoveredProject = PathUtils.current()
      .toAbsolutePath()
      .getParent()
      .getParent()
      .resolve("integration-test-coverered-project")
      .toAbsolutePath();
    log.info("directory {}", pathToCoveredProject);
    PathUtils.copyDirectory(pathToCoveredProject, testProjectDir);
  }

  @Test
  void debug() throws IOException {
    var build = GradleRunner.create()
      // .withDebug(true)
      .withProjectDir(testProjectDir.toFile())
      .withArguments("check", "--stacktrace")
      .withPluginClasspath()
      .build();

    assertThat(build.getOutput())
      .contains("BUILD SUCCESSFUL")
      .doesNotContain("Task :check UP-TO-DATE")
      .doesNotContain("BUILD FAILED");
  }
}
