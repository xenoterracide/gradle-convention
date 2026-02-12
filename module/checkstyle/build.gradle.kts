// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: MIT

buildscript { dependencyLocking { lockAllConfigurations() } }

plugins {
  our.bom
  our.convention
}

dependencies {
  testImplementation(libs.commons.io)
}

gradlePlugin {
  plugins {
    create("checkstyle") {
      id = "${rootProject.group}.${project.name}"
      displayName = "My conventions for checkstyle"
      tags = setOf("checkstyle", "convention")
      implementationClass = "$id.CheckstyleConventionPlugin"
      description = "My conventions for checkstyle"
    }
  }
}

coverage {
  minimum.set(0.3)
}
