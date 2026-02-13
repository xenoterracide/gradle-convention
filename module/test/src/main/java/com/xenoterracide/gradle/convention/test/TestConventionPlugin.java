// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.test;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.JavaTestFixturesPlugin;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.logging.TestExceptionFormat;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.testing.base.TestingExtension;

/**
 * Convention plugin for configuring testing with JUnit 5.
 *
 * <p>This plugin configures standard Java testing with:
 * <ul>
 *   <li>JUnit 5 (JUnit Platform) for all test suites</li>
 *   <li>Test fixtures support via java-test-fixtures plugin</li>
 *   <li>Parallel test execution</li>
 *   <li>Comprehensive test logging</li>
 *   <li>Dynamic agent loading for Mockito, ByteBuddy, etc.</li>
 *   <li>Verification that tests exist</li>
 * </ul>
 */
public class TestConventionPlugin implements Plugin<Project> {

  // CHECKSTYLE:OFF: MultipleStringLiterals - "test" is a standard Gradle source set name
  private static final String TEST = "test";
  // CHECKSTYLE:ON: MultipleStringLiterals
  private static final String TESTS_AVAILABLE = "testsAvailable";

  static void configureTestSuites(Project project) {
    var testing = project.getExtensions().getByType(TestingExtension.class);

    testing.getSuites().withType(JvmTestSuite.class).configureEach(JvmTestSuite::useJUnitJupiter);
  }

  // CHECKSTYLE:OFF: LambdaBodyLength
  static void configureTestTasks(Project project) {
    var tests = project.getTasks().withType(Test.class);

    tests.configureEach(test -> {
      // Enable dynamic agent loading for Mockito, ByteBuddy, etc.
      test.jvmArgs("-XX:+EnableDynamicAgentLoading");
      test.useJUnitPlatform();

      // keep low because gradle is already running in parallel, and we might want to parallel using junit itself
      test.setMaxParallelForks(2);

      test
        .getTestLogging()
        .lifecycle(lifecycle -> {
          lifecycle.setShowStandardStreams(true);
          lifecycle.setDisplayGranularity(2);
          lifecycle.setExceptionFormat(TestExceptionFormat.FULL);
          lifecycle.events(TestLogEvent.SKIPPED, TestLogEvent.FAILED);
        });
    });
    // CHECKSTYLE:ON: LambdaBodyLength

    // Register the testsAvailable task
    registerTestsAvailableTask(project);
  }

  static void registerTestsAvailableTask(Project project) {
    var javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
    var testSourceSet = javaExtension.getSourceSets().named(TEST);

    // Register the testsAvailable task with proper inputs for configuration cache compatibility
    var testsAvailable = project
      .getTasks()
      .register(TESTS_AVAILABLE, TestsAvailableTask.class, task -> {
        task.setDescription("Verifies there is at least one test");
        task.setGroup("Verification");
        // Configure the input files using a provider that extracts just the source directories
        // This avoids capturing the SourceSet itself which is not serializable
        var sourceDirsProvider = testSourceSet
          .map(SourceSet::getJava)
          .map(javaSourceSet -> javaSourceSet.getSourceDirectories());
        task.getTestSources().from(sourceDirsProvider);
      });

    // Only finalize the 'test' task (not testIntegration or other suites) since we only check the test source set
    project
      .getTasks()
      .named(TEST, Test.class)
      .configure(test -> test.finalizedBy(testsAvailable));
  }

  @Override
  public void apply(Project project) {
    project.getPlugins().apply(JavaLibraryPlugin.class);
    project.getPlugins().apply(JavaTestFixturesPlugin.class);

    configureTestSuites(project);
    configureTestTasks(project);
  }
}
