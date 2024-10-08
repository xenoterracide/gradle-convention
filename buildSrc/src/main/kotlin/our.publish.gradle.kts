// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: MIT

plugins {
  id("com.gradle.plugin-publish")
  id("com.xenoterracide.gradle.convention.publish")
}

val repo = rootProject.name
val username = "xenoterracide"
val githubUrl = "https://github.com"
val repoShort = "$username/$repo"

gradlePlugin {
  website.set("$githubUrl/$repoShort")
  vcsUrl.set("${website.get()}.git")
}

publicationLegal {
  inceptionYear.set(2024)
  spdxLicenseIdentifiers.add("Apache-2.0")
}
