// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.test;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.JavaTestFixturesPlugin;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.api.provider.Provider;
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

  private static final String TEST = "test";
  private static final String TESTS_AVAILABLE = "testsAvailable";

  private static void configureTestSuites(Project project) {
    var testing = project.getExtensions().getByType(TestingExtension.class);

    testing.getSuites().withType(JvmTestSuite.class).configureEach(JvmTestSuite::useJUnitJupiter);
  }

  // CHECKSTYLE:OFF: LambdaBodyLength
  private static void configureTestTasks(Project project) {
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

    // Register a task to verify tests exist
    registerTestsAvailableTask(project);
  }

  private static void registerTestsAvailableTask(Project project) {
    var testSourceSet = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().named(TEST);

    project
      .getTasks()
      .register(TESTS_AVAILABLE, task -> {
        task.doLast(t -> checkTestsExist(testSourceSet));
      });

    // Finalize test tasks with the availability check
    project
      .getTasks()
      .withType(Test.class)
      .configureEach(test -> {
        test.finalizedBy(TESTS_AVAILABLE);
      });
  }

  private static void checkTestsExist(Provider<SourceSet> testSourceSet) {
    var javaFiles = testSourceSet.get().getJava().getFiles();
    if (javaFiles.isEmpty()) {
      throw new GradleException("no tests found");
    }
  }

  @Override
  public void apply(Project project) {
    project.getPlugins().apply(JavaLibraryPlugin.class);
    project.getPlugins().apply(JavaTestFixturesPlugin.class);

    configureTestSuites(project);
    configureTestTasks(project);
  }
}
