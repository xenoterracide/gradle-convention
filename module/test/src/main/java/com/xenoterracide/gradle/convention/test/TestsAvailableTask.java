// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.test;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

/**
 * Task that verifies test source files exist.
 *
 * <p>This task checks that the test source set contains at least one Java file.
 * It is used to ensure that tests are not accidentally omitted from the build.
 */
public abstract class TestsAvailableTask extends DefaultTask {

  /**
   * The collection of test source files to check.
   *
   * <p>This should be set to the Java source directories of the test source set.
   *
   * @return the configurable file collection of test sources
   */
  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract ConfigurableFileCollection getTestSources();

  /**
   * Verifies that test source files exist.
   *
   * @throws GradleException if no test files are found
   */
  @TaskAction
  void checkTestsExist() {
    var sourceDirs = this.getTestSources().getFiles();
    if (sourceDirs.isEmpty()) {
      throw new GradleException("no tests found: test source directories are empty");
    }

    var visitor = new TestFileVisitor();
    this.getTestSources().getAsFileTree().visit(visitor);
    if (!visitor.isFoundTestFile()) {
      throw new GradleException("no tests found: no Java test files in " + sourceDirs);
    }
  }

  /**
   * File visitor that searches for Java test files.
   */
  static final class TestFileVisitor implements FileVisitor {

    private boolean foundTestFile;

    @Override
    public void visitDir(FileVisitDetails dirDetails) {
      // No action needed for directories
    }

    @Override
    public void visitFile(FileVisitDetails fileDetails) {
      if (!this.foundTestFile && fileDetails.getName().endsWith(".java")) {
        this.foundTestFile = true;
        fileDetails.stopVisiting();
      }
    }

    boolean isFoundTestFile() {
      return this.foundTestFile;
    }
  }
}
