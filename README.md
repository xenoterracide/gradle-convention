<!--
SPDX-FileCopyrightText: Copyright © 2025 - 2026 Caleb Cushing

SPDX-License-Identifier: CC-BY-NC-4.0
-->

# Gradle Convention Plugins

Opinionated Gradle convention plugins for Java/Gradle projects. These plugins are published to both the **Gradle Plugin Portal** and **GitHub Packages**.

> **Note:** These are my personal conventions. If you find them useful, feel free to let me know and I can work toward a stable API.

## Available Plugins

| Plugin ID                                        | Module                          | Description                                                      |
| ------------------------------------------------ | ------------------------------- | ---------------------------------------------------------------- |
| `com.xenoterracide.gradle.convention.checkstyle` | [checkstyle](module/checkstyle) | Configures Checkstyle for code style enforcement                 |
| `com.xenoterracide.gradle.convention.compile`    | [compile](module/compile)       | Configures Java compilation with Error Prone and NullAway        |
| `com.xenoterracide.gradle.convention.coverage`   | [coverage](module/coverage)     | Configures JaCoCo coverage with unified multi-test-suite support |
| `com.xenoterracide.gradle.convention.javadoc`    | [javadoc](module/javadoc)       | Configures Javadoc generation conventions                        |
| `com.xenoterracide.gradle.convention.publish`    | [publish](module/publish)       | Configures Maven publishing with SPDX license support            |
| `com.xenoterracide.gradle.convention.spotbugs`   | [spotbugs](module/spotbugs)     | Configures SpotBugs static analysis                              |
| `com.xenoterracide.gradle.convention.test`       | [test](module/test)             | Configures JUnit 5 testing with test fixtures support            |

## Usage

Add the plugin to your `build.gradle.kts`:

```kotlin
plugins {
  id("com.xenoterracide.gradle.convention.coverage") version "0.3.0"
}
```

## Plugin Details

### Coverage

Enforces a minimum coverage percentage per module and combines all `JvmTestSuite`s so coverage is calculated across all tests.

```kotlin
coverage {
  minimum.set(0.9) // default: 0.9 (90%)
}
```

### Publish

**WARNING:** This plugin sets up defaults for my projects that will _not_ be suitable for yours. See the [package-info.java](module/publish/src/main/java/com/xenoterracide/gradle/convention/publish/package-info.java) for details.

## Compatibility

| Version | Gradle | Java |
| ------- | ------ | ---- |
| v0.2.x  | 8.x    | 11.x |
| v0.3.x  | 9.x    | 17.x |

Older versions may work but are unsupported.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup and guidelines.

## License

- **Java and resulting Jars:** [GPL-3.0-or-later](https://choosealicense.com/licenses/gpl-3.0/) WITH [Classpath-exception-2.0](https://spdx.org/licenses/Classpath-exception-2.0.html)
- **Gradle Kotlin and Config Files:** [MIT](https://choosealicense.com/licenses/mit/)
- **Documentation:** [CC-BY-NC-4.0](https://creativecommons.org/licenses/by-nc/4.0/)

© Copyright 2024–2026 Caleb Cushing.
