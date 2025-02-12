// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: MIT

import org.semver4j.Semver

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
    .map { semver.gitDescribed }
    .orElse(Semver("0.0.0"))
    .get()
    .toString()

tasks.dependencies {
  dependsOn(subprojects.map { it.tasks.dependencies })
}
