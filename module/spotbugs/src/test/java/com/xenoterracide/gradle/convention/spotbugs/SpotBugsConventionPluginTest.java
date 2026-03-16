// SPDX-FileCopyrightText: Copyright © 2025, 2026 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.spotbugs.snom.Confidence;
import com.github.spotbugs.snom.Effort;
import com.github.spotbugs.snom.SpotBugsExtension;
import com.github.spotbugs.snom.SpotBugsTask;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SpotBugsConventionPluginTest {

  Project project;

  @TempDir
  File tempDir;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().withName("that").withProjectDir(tempDir).build();
    project.getPluginManager().apply("java");
    project.getPluginManager().apply(SpotBugsConventionPlugin.class);
  }

  @Test
  void extension() {
    assertThat(project.getExtensions().getByType(SpotBugsExtension.class)).isNotNull();
  }

  @Test
  void spotbugsMainTaskIsEnabled() {
    var spotbugsMain = project.getTasks().withType(SpotBugsTask.class).named("spotbugsMain");
    assertThat(spotbugsMain.get().getEnabled()).isTrue();
  }

  @Test
  void otherSpotbugsTasksAreDisabled() {
    var spotbugsTest = project.getTasks().withType(SpotBugsTask.class).named("spotbugsTest");
    assertThat(spotbugsTest.get().getEnabled()).isFalse();
  }

  @Test
  void effortIsSetToMax() {
    var spotbugsMain = project.getTasks().withType(SpotBugsTask.class).named("spotbugsMain");
    assertThat(spotbugsMain.get().getEffort().get()).isEqualTo(Effort.MAX);
  }

  @Test
  void reportLevelIsSetToLow() {
    var spotbugsMain = project.getTasks().withType(SpotBugsTask.class).named("spotbugsMain");
    assertThat(spotbugsMain.get().getReportLevel().get()).isEqualTo(Confidence.LOW);
  }

  @Test
  void extraArgsContainsLongBugCodes() {
    var spotbugsMain = project.getTasks().withType(SpotBugsTask.class).named("spotbugsMain");
    assertThat(spotbugsMain.get().getExtraArgs().get()).contains("-longBugCodes");
  }

  @Test
  void excludeFilterIsSetWhenFileExists() throws IOException {
    var configDir = new File(tempDir, ".config/spotbugs");
    configDir.mkdirs();
    var excludeFile = new File(configDir, "exclude.xml");
    Files.writeString(excludeFile.toPath(), "<FindBugsFilter></FindBugsFilter>");

    var spotbugsMain = project.getTasks().withType(SpotBugsTask.class).named("spotbugsMain");
    assertThat(spotbugsMain.get().getExcludeFilter().get().getAsFile()).exists();
  }

  @Test
  void excludeFilterIsNotSetWhenFileDoesNotExist() {
    var spotbugsMain = project.getTasks().withType(SpotBugsTask.class).named("spotbugsMain");
    assertThat(spotbugsMain.get().getExcludeFilter().isPresent()).isFalse();
  }

  @Test
  void auxClassPathsFromCompileClasspath() {
    var spotbugsMain = project.getTasks().withType(SpotBugsTask.class).named("spotbugsMain");
    var auxClasspath = spotbugsMain.get().getAuxClassPaths();
    assertThat(auxClasspath).isNotNull();
  }
}
