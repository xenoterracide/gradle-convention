// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.coverage;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.testing.Test;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification;
import org.gradle.testing.jacoco.tasks.JacocoReport;

/**
 * Coverage Plugin. Applies {@link JavaBasePlugin}, {@link JacocoPlugin} and configures coverage tasks.
 * It is designed to work if multiple {@link org.gradle.api.plugins.jvm.JvmTestSuite} tasks are applied.
 * It forces 90% coverage to each project by default, but can be configured with the {@link CoveragePluginExtension},
 * e.g. {@code coverage { minimum.set(0.3) }}.
 */
public class CoveragePlugin implements Plugin<Project> {

  // CHECKSTYLE:OFF MethodLength
  @Override
  public void apply(Project project) {
    project.getPlugins().apply(JavaBasePlugin.class);
    project.getPlugins().apply(JacocoPlugin.class);

    var log = project.getLogger();
    var coverage = project.getExtensions().create("coverage", CoveragePluginExtension.class);

    var tasks = project.getTasks();
    tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME, task -> {
      task.dependsOn(tasks.withType(JacocoCoverageVerification.class));
    });

    tasks
      .withType(JacocoCoverageVerification.class)
      .configureEach(verification -> {
        var jacocoReports = tasks.withType(JacocoReport.class);
        verification.dependsOn(jacocoReports);
        // execution data needs to be aggregated from all exec files in the project for multi jvm test suite testing
        log.debug("{}: aggregate execution data {}", project.getName(), jacocoReports.getNames());
        verification.executionData(
          tasks.withType(JacocoReport.class).stream().map(JacocoReport::getExecutionData).collect(Collectors.toList())
        );
        verification.violationRules(rules -> {
          rules.rule(r -> {
            r.limit(l -> l.setMinimum(coverage.getMinimum().orElse(0.9).map(BigDecimal::valueOf).get()));
          });
        });
      });

    tasks
      .withType(JacocoReport.class)
      .configureEach(jacocoReport -> {
        var tests = tasks.withType(Test.class);
        jacocoReport.dependsOn(tests);
        log.debug("{}: execution data for {}", project.getName(), tests.getNames());
        tests.forEach(jacocoReport::executionData);
      });
  }
  // CHECKSTYLE:ON MethodLength
}
