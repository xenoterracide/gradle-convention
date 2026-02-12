// SPDX-FileCopyrightText: Copyright © 2025 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("checkstyle:MethodName")
@DisplayName("TestConventionPlugin Integration")
class TestConventionPluginIntegrationTest {

  Logger log = LoggerFactory.getLogger(this.getClass());

  @TempDir(cleanup = CleanupMode.ON_SUCCESS)
  Path testProjectDir;

  Path buildFile;
  Path settingsFile;
  Path srcDir;
  Path testDir;

  @BeforeEach
  void setup() throws IOException {
    settingsFile = testProjectDir.resolve("settings.gradle");
    buildFile = testProjectDir.resolve("build.gradle");
    srcDir = testProjectDir.resolve("src/main/java/com/example");
    testDir = testProjectDir.resolve("src/test/java/com/example");

    Files.createDirectories(srcDir);
    Files.createDirectories(testDir);

    Files.writeString(
      settingsFile,
      """
      rootProject.name = 'test-project'
      """
    );
  }

  @Test
  void runsWithConfigurationCache() throws IOException {
    // Given a basic Java project with tests
    Files.writeString(
      buildFile,
      """
      plugins {
        id 'com.xenoterracide.gradle.convention.test'
      }

      repositories {
        mavenCentral()
      }

      dependencies {
        testImplementation 'org.junit.jupiter:junit-jupiter:5.11.0'
      }
      """
    );

    // Create a main source file
    Files.writeString(
      srcDir.resolve("Library.java"),
      """
      package com.example;

      public class Library {
        public String greet() {
          return "Hello World!";
        }
      }
      """
    );

    // Create a test file
    Files.writeString(
      testDir.resolve("LibraryTest.java"),
      """
      package com.example;

      import org.junit.jupiter.api.Test;
      import static org.junit.jupiter.api.Assertions.assertEquals;

      class LibraryTest {
        @Test
        void greetReturnsHello() {
          var library = new Library();
          assertEquals("Hello World!", library.greet());
        }
      }
      """
    );

    // When running with configuration cache enabled
    var result = GradleRunner.create()
      .withProjectDir(testProjectDir.toFile())
      .withArguments("check", "--configuration-cache")
      .withPluginClasspath()
      .forwardOutput()
      .build();

    // Then the build should succeed
    assertThat(result.getOutput()).contains("BUILD SUCCESSFUL").doesNotContain("BUILD FAILED");

    // And the test task should run
    assertThat(result.task(":test")).isNotNull();
    assertThat(result.task(":test").getOutcome()).isIn(TaskOutcome.SUCCESS, TaskOutcome.FROM_CACHE);

    // And configuration cache should be stored (no errors)
    assertThat(result.getOutput()).contains("Configuration cache entry stored");

    log.info("Configuration cache stored successfully");
  }

  @Test
  void configurationCacheIsReused() throws IOException {
    // Given a basic Java project with tests
    Files.writeString(
      buildFile,
      """
      plugins {
        id 'com.xenoterracide.gradle.convention.test'
      }

      repositories {
        mavenCentral()
      }

      dependencies {
        testImplementation 'org.junit.jupiter:junit-jupiter:5.11.0'
      }
      """
    );

    // Create a main source file
    Files.writeString(
      srcDir.resolve("Library.java"),
      """
      package com.example;

      public class Library {
        public String greet() {
          return "Hello World!";
        }
      }
      """
    );

    // Create a test file
    Files.writeString(
      testDir.resolve("LibraryTest.java"),
      """
      package com.example;

      import org.junit.jupiter.api.Test;
      import static org.junit.jupiter.api.Assertions.assertEquals;

      class LibraryTest {
        @Test
        void greetReturnsHello() {
          var library = new Library();
          assertEquals("Hello World!", library.greet());
        }
      }
      """
    );

    // First run - store configuration cache
    var firstRun = GradleRunner.create()
      .withProjectDir(testProjectDir.toFile())
      .withArguments("check", "--configuration-cache")
      .withPluginClasspath()
      .build();

    assertThat(firstRun.getOutput()).contains("Configuration cache entry stored");

    // Second run - reuse configuration cache
    var secondRun = GradleRunner.create()
      .withProjectDir(testProjectDir.toFile())
      .withArguments("check", "--configuration-cache")
      .withPluginClasspath()
      .build();

    assertThat(secondRun.getOutput()).contains("Configuration cache entry reused");

    log.info("Configuration cache successfully reused");
  }
}
