// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: MIT

buildscript { dependencyLocking { lockAllConfigurations() } }

plugins {
  our.bom
  our.javacompile
  our.javatest
  our.spotless
  our.convention
}

dependencies {
  testImplementation(libs.commons.io)
}

gradlePlugin {
  plugins {
    create("coverage") {
      id = "${rootProject.group}.${project.name}"
      displayName = "My conventions for coverage"
      tags = setOf("coverage", "jacoco", "convention", "jvm-test-suite")
      implementationClass = "$id.CoveragePlugin"
      description = "My conventions for coverage, supports unified coverage for multiple jvm-test-suites"
    }
  }
}

coverage {
  minimum.set(0.3)
}
