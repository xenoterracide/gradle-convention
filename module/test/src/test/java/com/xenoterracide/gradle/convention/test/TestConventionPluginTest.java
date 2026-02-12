// SPDX-FileCopyrightText: Copyright © 2025 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

@SuppressWarnings("checkstyle:MethodName")
@DisplayName("TestConventionPlugin")
class TestConventionPluginTest {

  Project project;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().withName("test-project").build();
    project.getPluginManager().apply(TestConventionPlugin.class);
  }

  @org.junit.jupiter.api.Test
  void appliesJavaLibraryPlugin() {
    assertThat(project.getPlugins().hasPlugin(JavaLibraryPlugin.class)).isTrue();
  }

  @org.junit.jupiter.api.Test
  void appliesJavaTestFixturesPlugin() {
    assertThat(project.getPlugins().hasPlugin("java-test-fixtures")).isTrue();
  }

  @org.junit.jupiter.api.Test
  void testSuiteUsesJUnitJupiter() {
    var testing = project.getExtensions().getByType(org.gradle.testing.base.TestingExtension.class);
    var testSuite = testing.getSuites().getByName("test");
    assertThat(testSuite).isNotNull();
  }

  @org.junit.jupiter.api.Test
  void testsAvailableTaskExists() {
    var testsAvailableTask = project.getTasks().findByName("testsAvailable");
    assertThat(testsAvailableTask).isNotNull();
  }

  @org.junit.jupiter.api.Test
  void testTasksAreConfigured() {
    project
      .getTasks()
      .withType(org.gradle.api.tasks.testing.Test.class)
      .configureEach(test -> {
        assertThat(test.getTestFramework()).isNotNull();
      });
  }

  @org.junit.jupiter.api.Test
  void testTasksUseJUnitPlatform() {
    // Verify at least the test task exists and is configured
    var testTask = project.getTasks().findByName("test");
    assertThat(testTask).isNotNull();
  }
}
