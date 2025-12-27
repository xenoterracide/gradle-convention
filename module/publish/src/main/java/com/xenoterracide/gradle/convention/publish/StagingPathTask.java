// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.publish;

import java.nio.file.Path;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * Task for printing the path to the primary publication location in the staging repository.
 */
public abstract class StagingPathTask extends DefaultTask {

  /**
   * default constructor.
   */
  @SuppressWarnings("this-escape")
  public StagingPathTask() {
    this.setGroup("Publishing");
    this.setDescription("Print path to the primary publication location in the staging repository");
  }

  /**
   * The directory where the staging repository is located.
   *
   * @return staging repository directory
   */
  @InputDirectory
  public abstract DirectoryProperty getStagingDirectory();

  /**
   * The group of the project.
   *
   * @return project group
   */
  @Input
  public abstract Property<String> getProjectGroup();

  /**
   * The name of the project.
   *
   * @return project name
   */
  @Input
  public abstract Property<String> getProjectName();

  /**
   * The version of the project.
   *
   * @return project version
   */
  @Input
  public abstract Property<String> getProjectVersion();

  /**
   * Task action.
   */
  @TaskAction
  public void run() {
    var groupPath = this.getProjectGroup().get().replace(".", "/");
    var artifactId = this.getProjectName().get();
    var version = this.getProjectVersion().get();

    var artifactPath = this.getStagingDirectory()
      .map(dir -> dir.getAsFile().toPath())
      .map(path -> path.resolve(groupPath))
      .map(path -> path.resolve(artifactId))
      .map(path -> path.resolve(version))
      .map(Path::toAbsolutePath)
      .map(path -> path.toAbsolutePath().toString())
      .get();

    System.out.println(artifactPath);
  }
}
