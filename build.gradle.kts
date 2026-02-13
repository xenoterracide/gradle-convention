import org.semver4j.Semver

// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: MIT

buildscript { dependencyLocking { lockAllConfigurations() } }

plugins {
  alias(libs.plugins.semver)
  alias(libs.plugins.dependency.analysis)
  `lifecycle-base`
}

dependencyLocking { lockAllConfigurations() }

group = "com.xenoterracide.gradle.convention"
version =
  providers
    .environmentVariable("IS_PUBLISHING")
    .flatMap { semver.provider }
    .orElse(Semver.ZERO)
    .get()

tasks.dependencies {
  dependsOn(subprojects.map { it.tasks.dependencies })
}

tasks.check {
  // Ensure dependency analysis build health runs as part of check (root project)
  dependsOn(tasks.buildHealth)
}

dependencyAnalysis {
  issues {
    all {
      onAny { severity("fail") }
      // Convention modules don't have integration tests; ignore that source set for analysis
      ignoreSourceSet("testIntegration")
      onUnusedDependencies {
        // convention wires common test libs that may be unused by these modules
        exclude(libs.junit.parameters)
        exclude(libs.assertj)
        exclude(libs.junit.api)
        exclude("org.junit.jupiter:junit-jupiter")
      }
    }
  }
}
