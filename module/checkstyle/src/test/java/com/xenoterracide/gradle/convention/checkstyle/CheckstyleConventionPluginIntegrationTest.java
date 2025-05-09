// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.checkstyle;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.file.PathUtils;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckstyleConventionPluginIntegrationTest {

  Logger log = LoggerFactory.getLogger(this.getClass());

  @TempDir(cleanup = CleanupMode.ON_SUCCESS)
  Path testProjectDir;

  @BeforeEach
  @SuppressWarnings("NullAway")
  public void setupRunner() throws IOException {
    var project = "checkstyle-integration-test";
    var pathToCoveredProject = PathUtils.current()
      .toAbsolutePath()
      .getParent()
      .getParent()
      .resolve("integration-test")
      .resolve(project)
      .toAbsolutePath();
    log.info("directory {}", pathToCoveredProject);
    PathUtils.copyDirectory(pathToCoveredProject, testProjectDir);
  }

  @Test
  void debug() {
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
