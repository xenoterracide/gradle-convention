// SPDX-FileCopyrightText: Copyright © 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.javadoc;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.api.Project;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JavadocConventionPluginTest {

  Project project;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().withName("test").build();
    project.getPluginManager().apply("java");
    project.getPluginManager().apply(JavadocConventionPlugin.class);
  }

  @Test
  void javadocTaskExists() {
    assertThat(project.getTasks().withType(Javadoc.class)).isNotEmpty();
  }
}
