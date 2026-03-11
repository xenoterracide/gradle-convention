<!--
SPDX-FileCopyrightText: Copyright © 2025, 2026 Caleb Cushing

SPDX-License-Identifier: CC-BY-NC-4.0
-->

# Gradle Convention Plugins

Opinionated Gradle convention plugins for Java/Gradle projects. These plugins are published to both the **Gradle Plugin Portal** and **GitHub Packages**.

> **Note:** These are my personal conventions. If you find them useful, feel free to let me know and I can work toward a stable API.

## Table of Contents

- [Available Plugins](#available-plugins)
- [Usage](#usage)
- [Plugin Details](#plugin-details)
  - [Checkstyle](#checkstyle)
  - [Compile](#compile)
  - [Coverage](#coverage)
  - [Javadoc](#javadoc)
  - [Publish](#publish)
  - [SpotBugs](#spotbugs)
  - [Test](#test)
- [Compatibility](#compatibility)
- [Contributing](#contributing)
- [License](#license)

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

### Checkstyle

**Plugin ID:** `com.xenoterracide.gradle.convention.checkstyle`

Configures Checkstyle for code style enforcement. The plugin:

- Applies the standard Gradle Checkstyle plugin
- Automatically configures Checkstyle to use config files from `.config/checkstyle/<sourceSet>.xml`
- Falls back to root project config if local project config doesn't exist
- Creates a `checkstyle` task that runs Checkstyle on all source sets

**Configuration:**

Create configuration files for each source set:

```
.config/
  checkstyle/
    main.xml          # Used for main source set
    test.xml          # Used for test source set
    testFixtures.xml  # Used for test fixtures source set
```

**Tasks:**

| Task         | Description                                      |
| ------------ | ------------------------------------------------ |
| `checkstyle` | Runs Checkstyle on all Java source files         |
| `check`      | Includes Checkstyle verification (standard task) |

---

### Compile

**Plugin ID:** `com.xenoterracide.gradle.convention.compile`

Configures Java compilation with Error Prone and NullAway for enhanced static analysis. The plugin:

- Applies Error Prone for compile-time bug detection
- Configures NullAway for null safety checking in production code
- Sets compiler arguments for better diagnostics (`-parameters`, `-Xlint:all`, etc.)
- Disables certain checks when running inside IntelliJ IDEA
- Treats test fixtures as production code (enables null checking)
- Disables null checking for test code

**Features:**

- **Error Prone**: Enables 150+ checks as errors, including:
  - `BadImport`, `BadInstanceof`, `ClassCanBeStatic`
  - `MissingOverride`, `EqualsGetClass`, `ReferenceEquality`
  - `UnusedVariable`, `UnusedMethod` (disabled in IDEA)
- **NullAway**: Null safety analysis for production code
  - Annotated packages: `com`, `org`, `net`, `io`, `dev`, `graphql`
  - Handles common test assertion libraries
  - Excludes fields annotated with `@TempDir`

**Tasks:**

| Task      | Description                    |
| --------- | ------------------------------ |
| `compile` | Compiles all Java source files |

**Note:** This plugin does not configure the Java toolchain version, allowing consuming projects to set it according to their needs.

---

### Coverage

**Plugin ID:** `com.xenoterracide.gradle.convention.coverage`

Configures JaCoCo coverage with unified multi-test-suite support. The plugin:

- Enforces a minimum coverage percentage per module (default: 90%)
- Combines all `JvmTestSuite`s so coverage is calculated across all tests
- Automatically wires coverage verification to the `check` task

**Configuration:**

```kotlin
coverage {
  minimum.set(0.9)  // Set minimum coverage (0.0 - 1.0), default: 0.9 (90%)
}
```

**Tasks:**

| Task                             | Description                               |
| -------------------------------- | ----------------------------------------- |
| `jacocoTestReport`               | Generates JaCoCo coverage report          |
| `jacocoTestCoverageVerification` | Verifies coverage meets minimum threshold |
| `check`                          | Includes coverage verification            |

---

### Javadoc

**Plugin ID:** `com.xenoterracide.gradle.convention.javadoc`

Configures Javadoc generation with custom tags for API documentation. The plugin:

- Enables `withSourcesJar()` for publishing source JARs
- Configures custom Javadoc tags:
  - `@apiSpec` - API Specification
  - `@apiNote` - API Note
  - `@implSpec` - Implementation Specification
  - `@implNote` - Implementation Note
- Filters to only include `**/*.java` files (excludes generated sources like JPA modelgen)

**Configuration:**

No additional configuration required. The plugin automatically applies to all Javadoc tasks.

**Tasks:**

| Task         | Description                               |
| ------------ | ----------------------------------------- |
| `javadoc`    | Generates Javadoc with custom tags        |
| `sourcesJar` | Creates a JAR with source files (enabled) |

---

### Publish

**Plugin ID:** `com.xenoterracide.gradle.convention.publish`

**WARNING:** This plugin sets up defaults for my projects that will _not_ be suitable for yours. It configures Maven publishing with SPDX license support.

**Configuration:**

```kotlin
// Configure repository host information
repositoryHost(GithubPublicRepositoryConfiguration())
repositoryHost {
  namespace.set("your-org")  // GitHub user/organization
}

// Configure legal information for POM
publicationLegal {
  inceptionYear.set(2024)
  spdxLicenseIdentifiers.addAll("GPL-3.0-or-later", "Classpath-exception-2.0")
}
```

**Extensions:**

| Extension          | Type                        | Description                   |
| ------------------ | --------------------------- | ----------------------------- |
| `repositoryHost`   | `RepositoryHostExtension`   | Repository host configuration |
| `publicationLegal` | `PublicationLegalExtension` | License and legal information |

**Repository Host Properties:**

| Property                 | Description                        | Default (GitHub)               |
| ------------------------ | ---------------------------------- | ------------------------------ |
| `host`                   | Base URI of the repository host    | `https://github.com`           |
| `namespace`              | User or organization name          | Required                       |
| `name`                   | Repository name                    | Project name                   |
| `extension`              | Repository extension (e.g., "git") | `git`                          |
| `developmentPackageHost` | Package registry for snapshots     | `https://maven.pkg.github.com` |

**Publishing:**

```bash
# Publish to GitHub Packages (requires credentials)
./gradlew publishAllPublicationsToGhRepository

# Environment variables for authentication:
# ORG_GRADLE_PROJECT_ghUsername=<github-username>
# ORG_GRADLE_PROJECT_ghPassword=<github-token>

# Enable signing (required for releases)
IS_PUBLISHING=1 ./gradlew publish
```

**Tasks:**

| Task                                   | Description                         |
| -------------------------------------- | ----------------------------------- |
| `publish`                              | Publishes all publications          |
| `publishAllPublicationsToGhRepository` | Publishes to GitHub Packages        |
| `publishToMavenLocal`                  | Publishes to local Maven repository |

---

### SpotBugs

**Plugin ID:** `com.xenoterracide.gradle.convention.spotbugs`

Configures SpotBugs static analysis with sensible defaults. The plugin:

- Disables all SpotBugs tasks except `spotbugsMain`
- Sets effort to MAX for thorough analysis
- Sets report level to LOW to catch more issues
- Adds `-longBugCodes` to extra args for better error descriptions

**Configuration:**

Create an exclude filter file:

```
.config/
  spotbugs/
    exclude.xml    # Exclude filter for false positives
```

Example `exclude.xml`:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<FindBugsFilter>
  <Match>
    <Class name="~.*\$.*" />
    <Bug pattern="EI_EXPOSE_REP" />
  </Match>
</FindBugsFilter>
```

**Tasks:**

| Task           | Description                                 |
| -------------- | ------------------------------------------- |
| `spotbugsMain` | Runs SpotBugs analysis on main source set   |
| `check`        | Includes SpotBugs verification (if enabled) |

---

### Test

**Plugin ID:** `com.xenoterracide.gradle.convention.test`

Configures JUnit 5 testing with test fixtures support. The plugin:

- Applies `java-library` and `java-test-fixtures` plugins
- Configures all `JvmTestSuite`s to use JUnit Jupiter
- Enables dynamic agent loading for Mockito, ByteBuddy, etc.
- Sets max parallel forks to 2
- Configures comprehensive test logging
- Registers a `testsAvailable` task to verify tests exist

**Features:**

- **Test Fixtures**: Automatically enables test fixtures for sharing test code
- **Parallel Execution**: Runs tests with max 2 parallel forks
- **Test Logging**: Shows standard streams, full exception format, skipped and failed events
- **Dynamic Agents**: Enables `-XX:+EnableDynamicAgentLoading` for mocking frameworks

**Tasks:**

| Task             | Description                            |
| ---------------- | -------------------------------------- |
| `test`           | Runs the standard test suite           |
| `testsAvailable` | Verifies that at least one test exists |
| `check`          | Includes test execution                |

**Custom Test Suites:**

The plugin automatically configures any custom `JvmTestSuite` to use JUnit Jupiter:

```kotlin
testing {
  suites {
    val testIntegration by registering(JvmTestSuite::class) {
      // Automatically configured to use JUnit Jupiter
    }
  }
}
```

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
