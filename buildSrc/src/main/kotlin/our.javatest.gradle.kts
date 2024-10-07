// © Copyright 2023-2024 Caleb Cushing
// SPDX-License-Identifier: MIT

import gradle.kotlin.dsl.accessors._e054d9723d982fdb55b1e388b8ab0cbf.testing
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  `java-library`
  `java-test-fixtures`
}

val libs = the<LibrariesForLibs>()

testing {
  suites {
    withType<JvmTestSuite>().configureEach {
      dependencies {
        implementation(platform(libs.junit.bom))
        implementation.bundle(libs.bundles.test.impl)
        runtimeOnly(platform(libs.junit.bom))
        runtimeOnly.bundle(libs.bundles.test.runtime)
      }
    }
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
  reports {
    junitXml.required.set(false)
    html.required.set(false)
  }
  testLogging {
    lifecycle {
      showStandardStreams = true
      displayGranularity = 2
      exceptionFormat = TestExceptionFormat.FULL
      events.addAll(
        listOf(
          TestLogEvent.SKIPPED,
          TestLogEvent.FAILED,
        ),
      )
    }
  }
  inputs.dir(rootProject.file("buildSrc/src/main"))

  afterSuite(
    KotlinClosure2<TestDescriptor, TestResult, Unit>(
      { descriptor, result ->
        if (descriptor.parent == null) {
          logger.lifecycle("Tests run: ${result.testCount}, Failures: ${result.failedTestCount}, Skipped: ${result.skippedTestCount}")
          if (result.testCount == 0L) throw IllegalStateException("You cannot have 0 tests")
        }
        Unit
      },
    ),
  )
}
