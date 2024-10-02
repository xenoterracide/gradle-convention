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

dependencies {
  testFixturesImplementation(platform(libs.jakarta.bom))
  testFixturesImplementation(platform(libs.spring.bom))
}

testing {
  suites {
    withType<JvmTestSuite>().configureEach {
      dependencies {
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

        compileOnly(libs.jmolecules.architecture.layered)
        compileOnly(libs.jspecify)

        implementation.bundle(libs.bundles.test.impl)
        runtimeOnly.bundle(libs.bundles.test.runtime)
      }
    }
  }
}

val available = tasks.register("tests available") {
  val java: Provider<FileCollection> = sourceSets.test.map { it.java }
  doLast {
    if (java.get().isEmpty) throw RuntimeException("no tests found")
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
  finalizedBy(available)

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
