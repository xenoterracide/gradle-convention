// SPDX-FileCopyrightText: Copyright © 2025 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.checkstyle;

import java.util.Locale;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.CheckstylePlugin;

/**
 * Checkstyle convention plugin.
 *
 * <p>Applies the checkstyle plugin and configures it to use the {@code .config/checkstyle/<sourceSet>.xml} either in
 * the local project, or falling back to the root project.
 */
public class CheckstyleConventionPlugin implements Plugin<Project> {

  static String getPath(String taskName) {
    var sourceSetName = taskName.substring("checkstyle".length());
    var filename = sourceSetName.substring(0, 1).toLowerCase(Locale.ENGLISH) + sourceSetName.substring(1);
    return ".config/checkstyle/" + filename + ".xml";
  }

  @Override
  public void apply(Project project) {
    project.getPlugins().apply(CheckstylePlugin.class);
    project
      .getTasks()
      .withType(Checkstyle.class)
      .configureEach(checkstyle -> {
        checkstyle.setShowViolations(true);

        var path = checkstyle.getName().transform(CheckstyleConventionPlugin::getPath);

        var file = project.file(path);
        var config = file.exists() ? file : project.getRootProject().file(path);
        checkstyle.setConfigFile(config);
      });
  }
}
