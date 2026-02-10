// SPDX-FileCopyrightText: Copyright © 2025 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.compile;

import static org.assertj.core.api.Assertions.assertThat;

import net.ltgt.gradle.errorprone.ErrorProneOptions;
import net.ltgt.gradle.errorprone.ErrorPronePlugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompilePluginTest {

  Project project;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().withName("that").build();
    project.getPluginManager().apply(CompilePlugin.class);
  }

  @Test
  void appliesJavaPlugin() {
    assertThat(project.getPlugins().hasPlugin("java")).isTrue();
  }

  @Test
  void appliesErrorPronePlugin() {
    assertThat(project.getPlugins().hasPlugin(ErrorPronePlugin.class)).isTrue();
  }

  @Test
  void registersCompileTask() {
    assertThat(project.getTasks().findByName("compile")).isNotNull();
  }

  @Test
  void configuresCompilerArgs() {
    var compileJava = (JavaCompile) project.getTasks().getByName("compileJava");
    assertThat(compileJava.getOptions().getCompilerArgs())
      .contains("-parameters", "-Xlint:all", "-Xdiags:verbose");
  }

  @Test
  void setsEncoding() {
    var compileJava = (JavaCompile) project.getTasks().getByName("compileJava");
    assertThat(compileJava.getOptions().getEncoding()).isEqualTo("UTF-8");
  }

  @Test
  void configuresNullAwayAnnotatedPackages() {
    var compileJava = (JavaCompile) project.getTasks().getByName("compileJava");
    var optionsExtensions = ((org.gradle.api.plugins.ExtensionAware) compileJava.getOptions()).getExtensions();
    var epOptions = optionsExtensions.findByType(ErrorProneOptions.class);
    assertThat(epOptions).isNotNull();
    assertThat(epOptions.getCheckOptions().get()).containsKey("NullAway:AnnotatedPackages");
  }

  @Test
  void isProductionOrTestFixturesForCompileJava() {
    assertThat(CompilePlugin.isProductionOrTestFixtures("compileJava")).isTrue();
  }

  @Test
  void isProductionOrTestFixturesForTestFixtures() {
    assertThat(CompilePlugin.isProductionOrTestFixtures("compileTestFixturesJava")).isTrue();
  }

  @Test
  void isProductionOrTestFixturesForTestJava() {
    assertThat(CompilePlugin.isProductionOrTestFixtures("compileTestJava")).isFalse();
  }

  @Test
  void isTestCodeForCompileTestJava() {
    assertThat(CompilePlugin.isTestCode("compileTestJava")).isTrue();
  }

  @Test
  void isTestCodeForCompileJava() {
    assertThat(CompilePlugin.isTestCode("compileJava")).isFalse();
  }

  @Test
  void isTestCodeForTestFixtures() {
    assertThat(CompilePlugin.isTestCode("compileTestFixturesJava")).isFalse();
  }

  @Test
  void isTestCodeForIntegrationTests() {
    assertThat(CompilePlugin.isTestCode("compileTestIntegrationJava")).isTrue();
  }
}
