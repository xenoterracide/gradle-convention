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
      displayName = "My conventions for publishing"
      tags = setOf("publish", "convention")
      implementationClass = "$id.PublishPlugin"
    }
  }
}

tasks.withType<Test>().configureEach {
  enabled = false
}
