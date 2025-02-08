// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: MIT

buildscript { dependencyLocking { lockAllConfigurations() } }

plugins {
  our.bom
  our.javacompile
  our.javatest
  our.convention
}

dependencies {
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
