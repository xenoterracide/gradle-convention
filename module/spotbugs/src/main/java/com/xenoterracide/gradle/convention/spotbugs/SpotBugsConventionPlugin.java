// SPDX-FileCopyrightText: Copyright © 2025, 2026 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.spotbugs;

import com.github.spotbugs.snom.Confidence;
import com.github.spotbugs.snom.Effort;
import com.github.spotbugs.snom.SpotBugsPlugin;
import com.github.spotbugs.snom.SpotBugsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * A plugin that applies the SpotBugs plugin and configures it with sensible defaults.
 *
 * <p>It sets the following defaults:
 *
 * <ul>
 *   <li>Disables all but the spotbugsMain task</li>
 *   <li>Sets the exclude filter to {@code .config/spotbugs/exclude.xml}</li>
 *   <li>Sets the effort to MAX</li>
 *   <li>Sets the report level to LOW</li>
 *   <li>Adds {@code -longBugCodes} to extra args</li>
 * </ul>
 */
public abstract class SpotBugsConventionPlugin implements Plugin<Project> {

  /**
   * Default constructor.
   */
  public SpotBugsConventionPlugin() {}

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(SpotBugsPlugin.class);
    project
      .getTasks()
      .withType(SpotBugsTask.class)
      .configureEach(task -> {
        if (!task.getName().equals("spotbugsMain")) {
          task.setEnabled(false);
        }
        var filter = project.file(".config/spotbugs/exclude.xml");
        if (filter.exists()) task.getExcludeFilter().set(filter);
        task.getEffort().set(Effort.MAX);
        task.getReportLevel().set(Confidence.LOW);
        task.getExtraArgs().add("-longBugCodes");

        // add runtime and compile in case dependencies are not available by default due to gradle or JPMS
        // e.g. jspecify doens't need to be in shipped artifacted and so is compileOnly but spotbugs needs it for null
        // analysis at runtime.
        task.getAuxClassPaths().from(project.getConfigurations().getByName("compileClasspath"));
        task.getAuxClassPaths().from(project.getConfigurations().getByName("runtimeClasspath"));
      });
  }
}
