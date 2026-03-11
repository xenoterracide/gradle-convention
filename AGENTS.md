<!--
SPDX-FileCopyrightText: Copyright © 2026 Caleb Cushing

SPDX-License-Identifier: CC-BY-NC-4.0
-->

# Agent Guidelines for gradle-convention

This document provides essential information for AI coding agents working on this project.

## Agent Workflow

**When doing ANY work that modifies files in this repository, follow the [pull-request skill](.agents/skills/pull-request/SKILL.md) workflow.**

This applies to all code changes including fixing bugs, adding features, refactoring, debugging, or cleaning up. The workflow ensures changes are properly committed, pushed, and submitted as pull requests with appropriate conventional commit messages.

See the "Pull Request Workflow" section below for the `make merge` command.

## Project Overview

This is a **Gradle plugin project** that provides opinionated convention plugins for Java/Gradle projects. The plugins are published to both the **Gradle Plugin Portal** and **GitHub Packages**.

The project follows a **multi-module structure** where each module in `module/` is a standalone Gradle plugin with its own ID: `com.xenoterracide.gradle.convention.<module-name>`. The project uses Java 25 for building but targets Java 17 runtime compatibility.

### Available Convention Plugins

| Module       | Plugin ID                                        | Purpose                                                                 |
| ------------ | ------------------------------------------------ | ----------------------------------------------------------------------- |
| `checkstyle` | `com.xenoterracide.gradle.convention.checkstyle` | Configures Checkstyle for code style enforcement                        |
| `compile`    | `com.xenoterracide.gradle.convention.compile`    | Configures Java compilation with Error Prone, NullAway, strict warnings |
| `coverage`   | `com.xenoterracide.gradle.convention.coverage`   | Configures JaCoCo coverage with unified multi-test-suite support        |
| `javadoc`    | `com.xenoterracide.gradle.convention.javadoc`    | Configures Javadoc generation conventions                               |
| `publish`    | `com.xenoterracide.gradle.convention.publish`    | Configures Maven publishing with SPDX license support                   |
| `spotbugs`   | `com.xenoterracide.gradle.convention.spotbugs`   | Configures SpotBugs static analysis                                     |
| `test`       | `com.xenoterracide.gradle.convention.test`       | Configures JUnit 5 testing with test fixtures and parallel execution    |

## Technology Stack

- **Language**: Java 17+ (runtime), Java 25 (toolchain for building)
- **Build Tool**: Gradle 9+ with Kotlin DSL
- **Module System**: Gradle multi-module project with `module/` directory
- **Testing**: JUnit 5, AssertJ, Gradle TestKit for integration tests
- **Static Analysis**: Error Prone, NullAway, SpotBugs, Checkstyle
- **Coverage**: JaCoCo with 90% minimum coverage default (30% for convention modules)
- **Node.js/Yarn**: For formatting tools (Prettier) and git hooks
- **Python**: For REUSE license compliance

## Project Structure

```
gradle-convention/
├── module/                    # Individual plugin modules
│   ├── checkstyle/           # Checkstyle convention plugin
│   ├── compile/              # Compile convention plugin (Error Prone, NullAway)
│   ├── coverage/             # JaCoCo coverage plugin
│   ├── javadoc/              # Javadoc convention plugin
│   ├── publish/              # Maven publishing plugin
│   ├── spotbugs/             # SpotBugs convention plugin
│   ├── test/                 # JUnit 5 testing convention plugin
│   └── integration-test/     # Test fixtures for integration tests
├── buildSrc/                 # Shared build logic
│   └── src/main/kotlin/      # Convention scripts for building this project
│       ├── our.bom.gradle.kts        # Bill of Materials exclusions and resolution
│       └── our.convention.gradle.kts # Conventions for this project's modules
├── gradle/
│   └── libs.versions.toml    # Version catalog for dependencies
├── .share/                   # Shared configuration files (symlinked from root)
│   ├── git/hooks/            # Git hooks (pre-commit, commit-msg)
│   └── bin/                  # Utility scripts
├── .config/git/hooks/        # Git hooks location (via core.hooksPath)
└── scripts/                  # Utility scripts
```

Each module follows this structure:

```
module/<name>/
├── build.gradle.kts          # Module build configuration
├── src/
│   ├── main/java/            # Plugin source code
│   ├── test/java/            # Unit tests using Gradle Test Fixtures
│   └── testIntegration/java/ # Integration tests using Gradle TestKit
```

## Build Commands

```bash
# Build the entire project
./gradlew build

# Run all checks (tests, static analysis, coverage verification)
./gradlew check

# Clean build directories
./gradlew clean
# OR (more thorough, removes caches)
yarn cleaner

# Update Gradle dependencies (write lock files)
yarn ug
# OR with build scan
yarn ug:scan

# View project version (from semver)
./gradlew version

# Dependency analysis
./gradlew buildHealth

# Run tests only
./gradlew test

# Run integration tests
./gradlew testIntegration
```

> **Note**: Append `--console=plain` to Gradle commands for cleaner output suitable for parsing or redirection.

## Code Style Guidelines

### File Formatting

- **Prettier** is used for formatting Java, XML, YAML, JSON, TOML, and properties files
- **ktlint** is used for Kotlin DSL files (`*.gradle.kts`)
- EditorConfig enforces: 2-space indentation, LF line endings, UTF-8, trailing whitespace trimmed
- Print width: 120 characters

### License Headers

Every file MUST have an SPDX license header. Use `reuse` tool to annotate:

```bash
# Java files - GPL-3.0-or-later WITH Classpath-exception-2.0
uv run reuse annotate --license 'GPL-3.0-or-later WITH Classpath-exception-2.0' \
  --copyright 'Caleb Cushing' --copyright-prefix spdx-string-symbol \
  --merge-copyrights <file>

# Gradle/Kotlin files - MIT
uv run reuse annotate --license 'MIT' --copyright 'Caleb Cushing' \
  --copyright-prefix spdx-string-symbol --merge-copyrights <file>

# Config/data files - CC0-1.0
uv run reuse annotate --license 'CC0-1.0' --copyright 'Caleb Cushing' \
  --copyright-prefix spdx-string-symbol --merge-copyrights --fallback-dot-license <file>

# Documentation - CC-BY-NC-4.0
uv run reuse annotate --license 'CC-BY-NC-4.0' --copyright 'Caleb Cushing' \
  --copyright-prefix spdx-string-symbol --merge-copyrights <file>
```

### Pre-commit Hooks

The project uses lint-staged and conventional commits:

- Commits must follow [Conventional Commits](https://www.conventionalcommits.org/) format
- Valid types: `ci`, `feat`, `fix`, `perf`, `refactor`, `style`, `test`, `build`, `ops`, `docs`, `chore`, `merge`, `revert`
- Pre-commit hooks auto-format files with Prettier/ktlint and validate commit messages
- Setup hooks: `yarn run contribute`

### Java Code Style

- Error Prone and NullAway are enabled for static analysis
- SpotBugs detects potential bugs
- Checkstyle enforces style rules
- All warnings are treated as errors
- Use `var` for local variables where type is obvious
- Use `abstract class` for plugins extending `Plugin<Project>`

## Testing Instructions

### Test Structure

Each module has:

- **Unit tests**: `src/test/java/` - Fast tests using Gradle Test Fixtures
- **Integration tests**: `src/testIntegration/java/` - Slower tests using Gradle TestKit

### Running Tests

```bash
# All tests
./gradlew check

# Specific module tests
./gradlew :coverage:test

# Integration tests
./gradlew :coverage:testIntegration

# With detailed output
./gradlew test --info
```

### Coverage Requirements

- Default minimum coverage: **90%**
- Convention modules use **30%** minimum (configured via `coverage { minimum.set(0.3) }`)
- Coverage is calculated across ALL test suites (unit + integration)
- JaCoCo verification runs as part of `check` task

### Test Configuration

- JUnit 5 (Jupiter) platform
- Parallel test execution enabled (`maxParallelForks = 2`)
- Dynamic agent loading enabled for Mockito/ByteBuddy
- Comprehensive test logging (full stack traces, standard streams)
- AssertJ for fluent assertions

### Integration Test Pattern

Integration tests use Gradle TestKit with test fixtures from `module/integration-test/`:

```java
@TempDir
Path testProjectDir;

@BeforeEach
void setup() throws IOException {
  var pathToProject = PathUtils.current()
    .toAbsolutePath()
    .getParent()
    .getParent()
    .resolve("integration-test")
    .resolve("<test-project-name>")
    .toAbsolutePath();
  PathUtils.copyDirectory(pathToProject, testProjectDir);
}

@Test
void test() {
  var build = GradleRunner.create()
    .withProjectDir(testProjectDir.toFile())
    .withArguments("check", "--stacktrace")
    .withPluginClasspath()
    .build();
  assertThat(build.getOutput()).contains("BUILD SUCCESSFUL");
}
```

## Security Considerations

### Dependency Management

- **Dependency locking** is enabled on all configurations
- Lock files must be updated with `yarn ug` when changing dependencies
- Pre-release and snapshot dependencies are rejected (except for project-internal dependencies)
- Exclusions configured in `buildSrc/src/main/kotlin/our.bom.gradle.kts`:
  - `slf4j-nop`, `junit:junit` globally
  - `jsr305`, `error_prone_annotations`, `checker-qual` from runtime

### Publishing Security

- Artifacts are signed with GPG during publishing
- Credentials come from environment variables, never hardcoded
- GitHub Packages and Gradle Plugin Portal require authentication

### Required Secrets for Publishing

- `GPG_SECRET_KEY` / `GPG_PASSPHRASE`: For artifact signing
- `GRADLE_PUBLISH_KEY` / `GRADLE_PUBLISH_SECRET`: For Gradle Plugin Portal
- `GRADLE_ENCRYPTION_KEY`: For Gradle cache encryption in CI

## Development Workflow

### Setup

```bash
# Enable Corepack for Yarn
corepack enable

# Install dependencies and setup git hooks
yarn install --immutable --inline-builds --check-resolutions
yarn run contributor
```

### Making Changes

1. Write code with proper SPDX headers
2. Add/update tests (both unit and integration)
3. Run `./gradlew build` to verify
4. Commit using conventional commits format

### Pull Request Workflow

```bash
# Create/update PR with automated message generation
make merge
```

This will:

1. Build the project
2. Generate PR title/body using AI
3. Create or update the PR
4. Trigger CI build
5. Auto-merge on success

## Key Configuration Files

| File                                                 | Purpose                                                |
| ---------------------------------------------------- | ------------------------------------------------------ |
| `settings.gradle.kts`                                | Project structure, plugin management, Develocity scans |
| `build.gradle.kts`                                   | Root build configuration, dependency analysis          |
| `gradle/libs.versions.toml`                          | Version catalog for all dependencies                   |
| `gradle.properties`                                  | Gradle configuration (caching, JVM args)               |
| `buildSrc/src/main/kotlin/our.convention.gradle.kts` | Conventions for building this project                  |
| `buildSrc/src/main/kotlin/our.bom.gradle.kts`        | Bill of Materials exclusions and resolution strategy   |
| `git-conventional-commits.yaml`                      | Conventional commit configuration                      |
| `.lintstagedrc.yml`                                  | Pre-commit formatting rules                            |
| `REUSE.toml`                                         | License compliance configuration                       |
| `.tool-versions`                                     | Tool versions (Java 25, Node.js 24, ktlint 1.8.0)      |

## Version Management

- Versions are managed by the **semver plugin** based on git tags
- Format: `v0.3.0`, `v1.0.0`
- Use `./gradlew version` to check current version
- Publishing only happens when `IS_PUBLISHING=1` environment variable is set

## Gradle Configuration Cache

All plugins in this project **MUST be configuration cache safe**. When writing or modifying plugins:

- Use `Provider` APIs instead of direct property access
- Avoid capturing non-serializable objects in task actions
- Test with `./gradlew build --configuration-cache`

Gradle configuration is enabled in `gradle.properties`:

```properties
org.gradle.configuration-cache=true
org.gradle.configuration-cache.parallel=true
```

## External Dependencies

### GitHub Packages Authentication

To fetch snapshot dependencies locally, create `~/.gradle/gradle.properties`:

```properties
ghUsername=<your-github-username>
ghPassword=<your-github-token-with-read-packages>
```

### Tool Versions

- Java: 25 (Temurin distribution recommended)
- Node.js: 24 (via Corepack/Yarn)
- ktlint: 1.8.0

See `.tool-versions` for specific versions.

## CI/CD

GitHub Actions workflows:

- **build.yml**: Build, test, and publish on push
  - `build` job: Standard build with caching
  - `full` job: Build without cache (verification)
  - `publish` job: Publish to GitHub Packages and Gradle Plugin Portal
- **pre-commit.yml**: License compliance, formatting checks
  - `license` job: REUSE lint
  - `format` job: Prettier check
  - `format-kotlin` job: ktlint check
- **update-java.yml**: Automated Java dependency updates (daily cron)

All workflows run on Ubuntu 24.04 with Java 25.

## License Scheme

| File Type                   | License                                       |
| --------------------------- | --------------------------------------------- |
| Java source files           | GPL-3.0-or-later WITH Classpath-exception-2.0 |
| Gradle/Kotlin build scripts | MIT                                           |
| Documentation, Markdown     | CC-BY-NC-4.0                                  |
| Configuration files         | CC0-1.0                                       |
| Lock files                  | CC0-1.0                                       |

## Troubleshooting

### Build Failures

```bash
# Clean caches thoroughly
yarn cleaner

# Rebuild without cache
./gradlew build --no-build-cache --no-configuration-cache --rerun-tasks
```

### Lock File Issues

```bash
# Update all locks
yarn ug

# Force refresh and update
yarn ug:dogfood
```

### License Header Issues

```bash
# Check compliance
uv run reuse lint

# Auto-annotate files (use with caution)
uv run reuse annotate --license ... --copyright ... <files>
```

### Configuration Cache Issues

```bash
# Clear configuration cache
rm -rf .gradle/configuration-cache/

# Build with cache debugging
./gradlew build --configuration-cache --configuration-cache-problems=warn
```
