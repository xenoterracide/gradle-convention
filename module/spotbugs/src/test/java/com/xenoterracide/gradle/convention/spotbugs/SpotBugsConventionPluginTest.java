// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.spotbugs.snom.SpotBugsExtension;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpotBugsConventionPluginTest {

  Project project;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().withName("that").build();
    project.getPluginManager().apply(SpotBugsConventionPlugin.class);
  }

  @Test
  void extension() {
    assertThat(project.getExtensions().getByType(SpotBugsExtension.class)).isNotNull();
  }
}
