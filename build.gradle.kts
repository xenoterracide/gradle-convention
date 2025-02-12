import org.semver4j.Semver

// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: MIT

buildscript { dependencyLocking { lockAllConfigurations() } }

plugins {
  alias(libs.plugins.semver)
  `lifecycle-base`
}

dependencyLocking { lockAllConfigurations() }

group = "com.xenoterracide.gradle.convention"
version =
  providers
    .environmentVariable("IS_PUBLISHING")
    .flatMap { semver.provider }
    .orElse(Semver.ZERO)
    .get()

tasks.dependencies {
  dependsOn(subprojects.map { it.tasks.dependencies })
}
