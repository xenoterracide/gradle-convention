// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.coverage;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoveragePluginTest {

  Project project;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().withName("that").build();
    project.getPluginManager().apply(CoveragePlugin.class);
  }

  @Test
  void apply() {}
}
