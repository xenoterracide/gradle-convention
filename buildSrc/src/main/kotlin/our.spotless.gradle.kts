// © Copyright 2023-2024 Caleb Cushing
// SPDX-License-Identifier: MIT

plugins {
  `java-base`
  id("com.diffplug.spotless")
}

val copyright = "// © Copyright \$YEAR Caleb Cushing\n"
val javaLicense =
  "// SPDX-License-Identifier: AGPL-3.0-or-later WITH Universal-FOSS-exception-1.0 AND CC-BY-4.0" +
    " OR CC-BY-NC-4.0\n\n"
val gradleLicense = "// SPDX-License-Identifier: MIT\n\n"

spotless {
  if (!providers.environmentVariable("CI").isPresent) {
    ratchetFrom("origin/main")
  }

  java {
    licenseHeader(copyright + javaLicense)
  }

  kotlinGradle {
    target("**/*.gradle.kts")
    targetExclude("**/build/**")
    ktlint().editorConfigOverride(mapOf("ktlint_standard_value-argument-comment" to "disabled"))
    licenseHeader(copyright + gradleLicense, "(import|buildscript|plugins|root)")
  }
}
