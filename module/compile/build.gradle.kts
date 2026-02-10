// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: MIT

buildscript { dependencyLocking { lockAllConfigurations() } }

plugins {
  our.bom
  our.javatest
  our.convention
}

dependencies {
  implementation(libs.plugin.errorprone)
  testImplementation(libs.commons.io)
}

gradlePlugin {
  plugins {
    create("compile") {
      id = "${rootProject.group}.${project.name}"
      displayName = "Java compile conventions with Error Prone"
      tags = setOf("java", "errorprone", "nullaway", "convention")
      implementationClass = "$id.CompilePlugin"
      description = "Configures Java compilation with Error Prone checks, NullAway, and sensible compiler defaults"
    }
  }
}

coverage {
  minimum.set(0.3)
}
