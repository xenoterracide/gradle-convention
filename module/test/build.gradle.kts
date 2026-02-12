// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: MIT

buildscript { dependencyLocking { lockAllConfigurations() } }

plugins {
  our.bom
  our.javatest
  our.convention
}

gradlePlugin {
  plugins {
    create("test") {
      id = "${rootProject.group}.${project.name}"
      displayName = "Java testing conventions"
      tags = setOf("testing", "junit", "java", "convention")
      implementationClass = "$id.TestConventionPlugin"
      description = "Configures JUnit 5 testing with sensible defaults, test fixtures support, " +
        "parallel execution, and comprehensive logging"
    }
  }
}

coverage {
  minimum.set(0.3)
}
