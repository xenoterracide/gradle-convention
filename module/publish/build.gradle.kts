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
  api(libs.plugin.gradle.maven.publish)
  implementation(libs.commons.lang3)
}

gradlePlugin {
  plugins {
    register("publish") {
      id = "${rootProject.group}.${project.name}"
      displayName = "My conventions for publishing"
      tags = setOf("publish", "convention")
      implementationClass = "$id.PublishPlugin"
      description = "makes it easy to add repository wide spdx licenses"
    }
  }
}
