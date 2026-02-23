// SPDX-FileCopyrightText: Copyright © 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.javadoc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

/**
 * Javadoc convention plugin.
 *
 * <p>Configures javadoc with custom tags for API specifications and implementation notes.</p>
 */
public class JavadocConventionPlugin implements Plugin<Project> {

  private static void configureJavadoc(Javadoc javadoc, TaskContainer tasks, SourceSetContainer sourceSets) {
    javadoc.dependsOn(tasks.named("classes"));
    // if cache invalidation is important enable this and see if it works
    // javadoc.doNotTrackState("depends on classes makes cache not work anyways, I guess");

    var mainSourceSet = sourceSets.named("main");
    javadoc.source(mainSourceSet.map(ss -> ss.getOutput().getGeneratedSourcesDirs()));

    var options = javadoc.getOptions();
    if (options instanceof StandardJavadocDocletOptions standardOptions) {
      standardOptions.tags(
        "apiSpec:a:API Spec:",
        "apiNote:a:API Note:",
        "implSpec:a:Implementation Spec:",
        "implNote:a:Implementation Note:"
      );
    }
  }

  @Override
  public void apply(Project project) {
    var extensions = project.getExtensions();

    // kind of sketch SRP wise, but maybe more accurately documentation plugin
    var java = extensions.getByType(JavaPluginExtension.class);
    java.withSourcesJar();

    var tasks = project.getTasks();
    var sourceSets = extensions.getByType(SourceSetContainer.class);

    tasks.withType(Javadoc.class).configureEach(javadoc -> configureJavadoc(javadoc, tasks, sourceSets));
  }
}
