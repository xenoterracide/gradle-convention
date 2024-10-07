// © Copyright 2023-2024 Caleb Cushing
// SPDX-License-Identifier: MIT

rootProject.name = "buildSrc"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}

pluginManagement {
  repositories {
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

  repositories {
    maven("https://maven.pkg.github.com/xenoterracide/gradle-convention") {
      name = "gh"
      mavenContent {
        includeGroup("com.xenoterracide.gradle.convention")
      }
      credentials(PasswordCredentials::class)
    }
    gradlePluginPortal() // this should only be necessary in buildSrc/settings.gradle.kts
  }
}
