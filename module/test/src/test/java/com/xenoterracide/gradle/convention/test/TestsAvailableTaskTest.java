// SPDX-FileCopyrightText: Copyright © 2025 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings("checkstyle:MethodName")
@DisplayName("TestsAvailableTask")
class TestsAvailableTaskTest {

  @TempDir
  File tempDir;

  Project project;
  TestsAvailableTask task;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().withName("test-project").withProjectDir(tempDir).build();
    task = project.getTasks().register("testsAvailable", TestsAvailableTask.class).get();
  }

  @Test
  void passesWhenTestFilesExist() throws IOException {
    // Given a directory with Java test files
    var testDir = new File(tempDir, "src/test/java/com/example");
    Files.createDirectories(testDir.toPath());
    var testFile = new File(testDir, "ExampleTest.java");
    Files.writeString(testFile.toPath(), "class ExampleTest {}");

    task.getTestSources().setFrom(testDir);

    // When/Then task should complete without exception
    task.checkTestsExist();
  }

  @Test
  void failsWhenNoTestFiles() throws IOException {
    // Given an empty test directory
    var testDir = new File(tempDir, "src/test/java/com/example");
    Files.createDirectories(testDir.toPath());

    task.getTestSources().setFrom(testDir);

    // When/Then task should throw GradleException
    assertThatThrownBy(() -> task.checkTestsExist())
      .isInstanceOf(GradleException.class)
      .hasMessageContaining("no tests found");
  }

  @Test
  void failsWhenNoSourceDirectories() {
    // Given no source directories configured (empty file collection)
    task.getTestSources().setFrom();

    // When/Then task should throw GradleException
    assertThatThrownBy(() -> task.checkTestsExist())
      .isInstanceOf(GradleException.class)
      .hasMessageContaining("no tests found");
  }

  @Test
  void passesWithNestedJavaFiles() throws IOException {
    // Given a directory with Java files in subdirectories
    var testDir = new File(tempDir, "src/test/java/com/example");
    var nestedDir = new File(testDir, "nested");
    Files.createDirectories(nestedDir.toPath());
    var testFile = new File(nestedDir, "NestedTest.java");
    Files.writeString(testFile.toPath(), "package com.example.nested; class NestedTest {}");

    task.getTestSources().setFrom(testDir);

    // When/Then task should complete without exception
    task.checkTestsExist();
  }

  @Test
  void ignoresNonJavaFiles() throws IOException {
    // Given a directory with only non-Java files
    var testDir = new File(tempDir, "src/test/resources");
    Files.createDirectories(testDir.toPath());
    var resourceFile = new File(testDir, "test-data.txt");
    Files.writeString(resourceFile.toPath(), "test data");

    task.getTestSources().setFrom(testDir);

    // When/Then task should throw GradleException (no Java files)
    assertThatThrownBy(() -> task.checkTestsExist())
      .isInstanceOf(GradleException.class)
      .hasMessageContaining("no tests found");
  }
}
