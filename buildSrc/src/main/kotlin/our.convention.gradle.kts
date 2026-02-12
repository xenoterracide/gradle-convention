// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: MIT

import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.GradlePublishPlugin
import com.xenoterracide.gradle.convention.publish.GithubPublicRepositoryConfiguration
import org.gradle.accessors.dm.LibrariesForLibs


plugins {
  id("com.autonomousapps.dependency-analysis")
  id("com.gradle.plugin-publish")
  id("com.xenoterracide.gradle.convention.checkstyle")
  id("com.xenoterracide.gradle.convention.compile")
  id("com.xenoterracide.gradle.convention.coverage")
  id("com.xenoterracide.gradle.convention.javadoc")
  id("com.xenoterracide.gradle.convention.publish")
  id("com.xenoterracide.gradle.convention.spotbugs")
  id("com.xenoterracide.gradle.convention.test")
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

mavenPublishing {
  configure(GradlePublishPlugin())
}

testing {
  suites {
    withType<JvmTestSuite>().configureEach {
      dependencies {
        implementation(platform(libs.junit.bom))
        implementation.bundle(libs.bundles.test.impl)
        runtimeOnly.bundle(libs.bundles.test.runtime)
      }
    }
    val testIntegration by registering(JvmTestSuite::class) {
      gradlePlugin.testSourceSet(sources)
      dependencies {
        runtimeOnly(project())
      }
    }
  }
}
