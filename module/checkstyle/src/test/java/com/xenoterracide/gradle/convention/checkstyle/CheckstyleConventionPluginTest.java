// SPDX-FileCopyrightText: Copyright © 2025 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.checkstyle;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.api.Project;
import org.gradle.api.plugins.quality.CheckstyleExtension;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CheckstyleConventionPluginTest {

  Project project;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().withName("that").build();
    project.getPluginManager().apply(CheckstyleConventionPlugin.class);
  }

  @Test
  void extension() {
    assertThat(project.getExtensions().getByType(CheckstyleExtension.class)).isNotNull();
  }

  @Test
  void getPath() {
    assertThat(CheckstyleConventionPlugin.getPath("checkstyleMain")).isEqualTo(".config/checkstyle/main.xml");
    assertThat(CheckstyleConventionPlugin.getPath("checkstyleTest")).isEqualTo(".config/checkstyle/test.xml");
    assertThat(CheckstyleConventionPlugin.getPath("checkstyleAndroidTest")).isEqualTo(
      ".config/checkstyle/androidTest.xml"
    );
  }
}
