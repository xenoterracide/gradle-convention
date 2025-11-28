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
  implementation(libs.plugin.spotbugs)
  testImplementation(libs.commons.io)
}

gradlePlugin {
  plugins {
    create("spotbugs") {
      id = "${rootProject.group}.${project.name}"
      displayName = "My conventions for spotbugs"
      tags = setOf("spotbugs", "convention")
      implementationClass = "$id.SpotBugsConventionPlugin"
      description = "configures checks for spotbugs "
    }
  }
}

coverage {
  minimum.set(0.3)
}
