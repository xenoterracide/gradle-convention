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
    create("javadoc") {
      id = "${rootProject.group}.${project.name}"
      displayName = "My conventions for javadoc"
      tags = setOf("javadoc", "convention")
      implementationClass = "$id.JavadocConventionPlugin"
      description = "My conventions for javadoc"
    }
  }
}
