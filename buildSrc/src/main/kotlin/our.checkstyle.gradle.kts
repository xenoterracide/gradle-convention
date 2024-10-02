// © Copyright 2023-2024 Caleb Cushing
// SPDX-License-Identifier: MIT

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
  `java-test-fixtures`
  checkstyle
}

val libs = the<LibrariesForLibs>()

tasks.withType<Checkstyle>().configureEach {
  isShowViolations = true
}

fun checkstyleConfig(filename: String): File {
  val path = ".config/checkstyle/$filename"
  val f = layout.projectDirectory.file(path).asFile
  if (f.exists()) return f
  if (rootProject.file(path).exists()) return rootProject.file(path)
  // test is weaker, and non main source sets are not production code
  return rootProject.file(".config/checkstyle/test.xml")
}

tasks.withType<Checkstyle>().configureEach {
  this.name.substringAfter("checkstyle").replaceFirstChar { it.lowercase() }.let { filename ->
    configFile = checkstyleConfig("$filename.xml")
  }
}
