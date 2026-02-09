// SPDX-FileCopyrightText: Copyright © 2025 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.compile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CompilePluginTest {

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
