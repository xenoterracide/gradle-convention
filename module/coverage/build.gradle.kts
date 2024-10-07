// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: MIT

buildscript { dependencyLocking { lockAllConfigurations() } }

plugins {
  our.bom
  our.javacompile
  our.javatest
  our.spotless
  our.publish
}

dependencies {
}

gradlePlugin {
  plugins {
    create("pub") {
      id = "${project.group}.${project.name}"
      displayName = "My conventions for coverage"
      tags = setOf("coverage", "jacoco", "convention")
      implementationClass = "$id.CoveragePlugin"
    }
  }
}

tasks.withType<Test>().configureEach {
  enabled = false
}
