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
}

gradlePlugin {
  plugins {
    register("plugin") {
      id = "${rootProject.group}.${project.name}"
      displayName = "My conventions for publishing"
      tags = setOf("publish", "convention")
      implementationClass = "$id.PublishPlugin"
      logger.quiet("publication {} {}:{}", name, id, version)
    }
  }
}
