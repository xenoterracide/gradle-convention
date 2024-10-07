// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: MIT

buildscript { dependencyLocking { lockAllConfigurations() } }

plugins {
  our.spotless
  alias(libs.plugins.dependency.analysis)
}

dependencyLocking { lockAllConfigurations() }

group = "com.xenoterracide.gradle.convention"

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
