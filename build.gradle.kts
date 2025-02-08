// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: MIT

import org.semver4j.Semver

buildscript { dependencyLocking { lockAllConfigurations() } }

plugins {
  alias(libs.plugins.dependency.analysis)
  alias(libs.plugins.semver)
  `lifecycle-base`
}

dependencyLocking { lockAllConfigurations() }

group = "com.xenoterracide.gradle.convention"
version =
  providers
    .environmentVariable("IS_PUBLISHING")
    .map { semver.gitDescribed }
    .orElse(Semver("0.0.0"))
    .get()
    .toString()

tasks.dependencies {
  dependsOn(subprojects.map { "${it.path}:dependencies" })
}

tasks.check {
  dependsOn(tasks.buildHealth)
}

dependencyAnalysis {
  issues {
    all {
      onAny {
        severity("fail")
      }
      onUnusedDependencies {
        exclude(libs.junit.api)
        exclude(libs.junit.parameters)
        exclude(libs.assertj)
        exclude(libs.spring.test)
        exclude(libs.spring.boot.test.autoconfigure)
        exclude(libs.spring.boot.test.core)
        exclude(libs.jspecify)
      }
    }
  }
}
