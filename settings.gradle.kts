// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: MIT

rootProject.name = "gradle-convention"

pluginManagement {
  repositories {
    gradlePluginPortal()
  }
}

plugins {
  id("com.gradle.develocity") version "3.18.1"
}

develocity {
  val ci = providers.environmentVariable("CI")
  buildScan {
    publishing.onlyIf { ci.isPresent }
    termsOfUseUrl.set("https://gradle.com/terms-of-service")
    termsOfUseAgree.set("yes")
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
  rulesMode = RulesMode.FAIL_ON_PROJECT_RULES

  repositories {
    gradlePluginPortal()
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootDir.resolve("module").listFiles()?.forEach { file ->
  if (file.isDirectory &&
    file
      ?.list { _, name -> name.startsWith("build.gradle") }
      ?.isNotEmpty() == true
  ) {
    val name = file.name
    include(":$name")
    project(":$name").projectDir = file("module/$name")
  }
}
