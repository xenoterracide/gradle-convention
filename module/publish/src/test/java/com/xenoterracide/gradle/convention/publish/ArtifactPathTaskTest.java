// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.publish;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArtifactPathTaskTest {

  private Project project;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().build();
  }

  @Test
  void testRun() {
    var task = project.getTasks().register("stagingPath", ArtifactPathTask.class).get();
    task.getProjectGroup().set("com.example");
    task.getProjectName().set("test-artifact");
    task.getProjectVersion().set("1.0.0");
    task.getDirectory().set(project.getLayout().getBuildDirectory().dir("repo"));

    var outContent = new ByteArrayOutputStream();
    var originalOut = System.out;
    System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
    try {
      task.run();
    } finally {
      System.setOut(originalOut);
    }

    assertThat(outContent.toString(StandardCharsets.UTF_8).trim()).endsWith(
      "build/repo/com/example/test-artifact/1.0.0"
    );
  }
}
