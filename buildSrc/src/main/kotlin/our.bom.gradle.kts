// © Copyright 2023-2024 Caleb Cushing
// SPDX-License-Identifier: MIT

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
  `java-library`
}

dependencyLocking {
  lockAllConfigurations()
}

val libs = the<LibrariesForLibs>()

configurations.configureEach {
  exclude(group = "org.slf4j", module = "slf4j-nop")
  exclude(group = "junit", module = "junit")

  resolutionStrategy {
    componentSelection {
      all {
        val nonRelease = Regex("^[\\d.]+-(RC|M|ea|beta|alpha).*$")
        val module = Regex("^spotbugs.*")
        val group = Regex("^com.xenoterracide$")
        if (!candidate.group.matches(group) && !name.matches(module) && !candidate.module.matches(module)) {
          if (candidate.version.matches(nonRelease)) reject("no pre-release")
          if (candidate.version.endsWith("-SNAPSHOT")) reject("no snapshots")
        } else if (candidate.version.matches(nonRelease)) {
          logger.info("allowing: {}", candidate)
        }
      }
    }
  }
}

configurations.matching { it.name == "runtimeClasspath" || it.name == "testRuntimeClasspath" }.configureEach {
  exclude(group = "com.google.code.findbugs", module = "jsr305")
  exclude(group = "com.google.errorprone", module = "error_prone_annotations")
  exclude(group = "org.checkerframework", module = "checker-qual")
  exclude(group = "ch.qos.logback", module = "logback-classic")
}
