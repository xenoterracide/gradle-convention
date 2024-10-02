// © Copyright 2024 Caleb Cushing
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
    maven("https://maven.pkg.github.com/xenoterracide/java-commons") {
      name = "gh"
      mavenContent {
        includeModule("com.xenoterracide", "tools")
      }
      credentials(PasswordCredentials::class)
    }
    mavenCentral()
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("coverage")
