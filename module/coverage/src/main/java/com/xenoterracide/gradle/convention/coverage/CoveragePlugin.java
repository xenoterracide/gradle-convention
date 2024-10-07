// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.coverage;

import java.math.BigDecimal;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.testing.Test;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.jspecify.annotations.NonNull;

/**
 * Coverage Plugin. Applies {@link JavaBasePlugin}, {@link JacocoPlugin} and configures coverage tasks.
 * It is designed to work if multiple {@link org.gradle.api.plugins.jvm.JvmTestSuite} tasks are applied.
 * It forces 90% coverage to each project by default, but can be configured with the {@link CoveragePluginExtension},
 * e.g. {@code coverage { minimum.set(0.3) }}.
 */
public class CoveragePlugin implements Plugin<Project> {

  @Override
  public void apply(@NonNull Project project) {
    project.getPlugins().apply(JacocoPlugin.class);
    var coverage = project.getExtensions().create("coverage", CoveragePluginExtension.class);

    var tasks = project.getTasks();
    tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME, task -> {
      task.dependsOn(tasks.withType(JacocoCoverageVerification.class));
    });

    tasks
      .withType(JacocoReport.class)
      .configureEach(jacocoReport -> {
        var tests = tasks.withType(Test.class);
        jacocoReport.dependsOn(tests);
        tests.forEach(jacocoReport::executionData);
      });

    tasks
      .withType(JacocoCoverageVerification.class)
      .configureEach(verification -> {
        verification.dependsOn(tasks.withType(JacocoReport.class));
        // execution data needs to be aggregated from all exec files in the project for multi jvm test suite testing
        verification.executionData(tasks.withType(JacocoReport.class).stream().map(JacocoReport::getExecutionData));
        verification.violationRules(rules -> {
          rules.rule(r -> {
            r.limit(l -> l.setMinimum(coverage.getMinimum().orElse(0.9).map(BigDecimal::valueOf).get()));
          });
        });
      });
  }
}