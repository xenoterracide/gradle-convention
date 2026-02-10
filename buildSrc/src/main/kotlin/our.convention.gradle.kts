// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: MIT

import com.xenoterracide.gradle.convention.publish.GithubPublicRepositoryConfiguration
import org.gradle.accessors.dm.LibrariesForLibs


plugins {
  id("com.autonomousapps.dependency-analysis")
  id("com.gradle.plugin-publish")
  id("com.xenoterracide.gradle.convention.checkstyle")
  id("com.xenoterracide.gradle.convention.compile")
  id("com.xenoterracide.gradle.convention.coverage")
  id("com.xenoterracide.gradle.convention.publish")
  id("com.xenoterracide.gradle.convention.spotbugs")
}

repositoryHost(GithubPublicRepositoryConfiguration())
repositoryHost.namespace.set("xenoterracide")

val libs = the<LibrariesForLibs>()

dependencies {
  errorprone(libs.errorprone.core)
  errorprone(libs.errorprone.nullaway)
  spotbugs(libs.spotbugs)
}

gradlePlugin {
  website.set(repositoryHost.repository.websiteUrl.map { it.toString() })
  vcsUrl.set(repositoryHost.repository.cloneUrl.map { it.toString() })
}

publicationLegal {
  inceptionYear.set(2024)
  spdxLicenseIdentifiers.addAll("GPL-3.0-or-later WITH Classpath-exception-2.0")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(25))
  }
}

tasks.compileJava {
  options.release.set(17)
}

java {
  withJavadocJar()
  withSourcesJar()
}
tasks.withType<Javadoc>().configureEach {
  dependsOn(tasks.classes)
  source(sourceSets.main.map { it.output.generatedSourcesDirs })
  (options as StandardJavadocDocletOptions).apply {
    addMultilineStringsOption("tag").value =
      listOf(
        "apiSpec:a:API Spec:",
        "apiNote:a:API Note:",
        "implSpec:a:Implementation Spec:",
        "implNote:a:Implementation Note:",
      )
  }
}

tasks.withType<Jar>().configureEach {
  archiveBaseName.set(project.path.substring(1).replace(":", "-"))
}
