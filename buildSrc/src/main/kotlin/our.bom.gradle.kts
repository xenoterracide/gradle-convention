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

        if (candidate.module == "hibernate-jpamodelgen") {
          if (candidate.version.startsWith("6.5")) {
            reject("https://hibernate.atlassian.net/browse/HHH-18203")
          }
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

dependencies {
  api(platform(libs.jakarta.bom))
  api(platform(libs.spring.bom))
  api(platform(libs.junit.bom))
  api(platform(libs.jmolecules.bom))
  api(platform(libs.spring.modulith.bom))

  compileOnly(platform(libs.jakarta.bom))
  compileOnly(platform(libs.spring.bom))
  compileOnly(platform(libs.junit.bom))
  compileOnly(platform(libs.jmolecules.bom))
  compileOnly(platform(libs.spring.modulith.bom))

  implementation(platform(libs.jakarta.bom))
  implementation(platform(libs.spring.bom))
  implementation(platform(libs.junit.bom))
  implementation(platform(libs.jmolecules.bom))
  implementation(platform(libs.spring.modulith.bom))

  runtimeOnly(platform(libs.jakarta.bom))
  runtimeOnly(platform(libs.spring.bom))
  runtimeOnly(platform(libs.junit.bom))
  runtimeOnly(platform(libs.jmolecules.bom))
  runtimeOnly(platform(libs.spring.modulith.bom))

  compileOnly(libs.jspecify)
  compileOnly(libs.jmolecules.architecture.layered)

  runtimeOnly(libs.starter.log4j2)

  modules {
    module("org.springframework.boot:spring-boot-starter-logging") {
      replacedBy(
        "org.springframework.boot:spring-boot-starter-log4j2",
        "Use Log4j2 instead of Logback",
      )
    }
  }
}
