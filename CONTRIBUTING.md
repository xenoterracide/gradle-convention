<!--
SPDX-FileCopyrightText: Copyright © 2026 Caleb Cushing

SPDX-License-Identifier: CC-BY-NC-4.0
-->

# Contributing to Gradle Convention Plugins

Thank you for your interest in contributing! This document outlines the development setup and guidelines for this project.

## Prerequisites

### Required Tools

- **Java 25** - Toolchain for building (runtime targets Java 17+)
- **Node.js 24+** - For formatting tools and git hooks

We recommend using [asdf](https://asdf-vm.com) for version management. Tool versions are defined in `.tool-versions`.

### Build Tools

- [Gradle](https://docs.gradle.org/current/userguide/command_line_interface.html)
- [Yarn 4](https://yarnpkg.com/getting-started/install) (via Corepack)

## Development Setup

1. **Enable Corepack** (for Yarn):

   ```sh
   corepack enable
   ```

2. **Install dependencies and setup git hooks**:
   ```sh
   yarn install --immutable --inline-builds --check-resolutions
   yarn run contribute
   ```

The `contribute` script:

- Installs Python dependencies from `requirements.txt`
- Configures Git hooks for pre-commit checks and conventional commits

## Build Commands

```bash
# Build the entire project
./gradlew build

# Run all checks (tests, static analysis, coverage)
./gradlew check

# Run tests only
./gradlew test

# Run integration tests
./gradlew testIntegration

# Clean build directories
./gradlew clean
# Or more thoroughly (removes caches):
yarn cleaner

# Update dependency locks
yarn ug
# With build scan:
yarn ug:scan

# View project version
./gradlew version
```

## Code Style

### Commit Messages

This project uses [Conventional Commits](https://www.conventionalcommits.org/). Valid types:

- `ci` - CI/CD changes
- `feat` - New features
- `fix` - Bug fixes
- `perf` - Performance improvements
- `refactor` - Code restructuring
- `style` - Formatting changes
- `test` - Test additions/changes
- `build` - Build system changes
- `ops` - Operations/infrastructure
- `docs` - Documentation
- `chore` - Maintenance tasks

### License Headers

Every file MUST have an SPDX license header. The project uses the [REUSE](https://reuse.software/) tool for compliance.

```bash
# Check license compliance
reuse lint

# Annotate Java files
reuse annotate --license 'GPL-3.0-or-later WITH Classpath-exception-2.0' \
  --copyright 'Caleb Cushing' --copyright-prefix spdx-string-symbol \
  --merge-copyrights <file>

# Annotate Gradle/Kotlin files
reuse annotate --license 'MIT' --copyright 'Caleb Cushing' \
  --copyright-prefix spdx-string-symbol --merge-copyrights <file>

# Annotate config files
reuse annotate --license 'CC0-1.0' --copyright 'Caleb Cushing' \
  --copyright-prefix spdx-string-symbol --merge-copyrights <file>
```

### Formatting

- **Prettier** formats Java, XML, YAML, JSON, TOML, and properties files
- **ktlint** formats Kotlin DSL files (`*.gradle.kts`)
- EditorConfig enforces 2-space indentation, LF line endings, UTF-8

Pre-commit hooks automatically format files on commit.

## Testing

- **Unit tests**: Located in `src/test/java/`, use Gradle Test Fixtures
- **Integration tests**: Located in `src/testIntegration/java/`, use Gradle TestKit

Coverage requirements:

- Default: 90% minimum
- Convention modules: 30% minimum (via `coverage { minimum.set(0.3) }`)

## GitHub Packages Authentication

To fetch snapshot dependencies locally, create `~/.gradle/gradle.properties`:

```properties
ghUsername=<your-github-username>
ghPassword=<your-github-token-with-read-packages>
```

Generate a personal access token (classic) with at least `read:packages` scope as described in the [GitHub documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authenticating-to-github-packages).

## Pull Request Workflow

Use one of the following commands to create/update a PR:

```bash
# Default (uses junie)
yarn merge

# Or specify the engine:
yarn merge:copilot
yarn merge:junie
yarn merge:kimi
```

This will:

1. Build the project
2. Generate PR title/body using AI
3. Create or update the PR
4. Trigger CI build
5. Auto-merge on success

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

### Configuration Cache Issues

```bash
# Clear configuration cache
rm -rf .gradle/configuration-cache/
```

## License

By contributing, you agree that your contributions will be licensed under the same licenses as the project:

- **Java source:** GPL-3.0-or-later WITH Classpath-exception-2.0
- **Build scripts:** MIT
- **Documentation:** CC-BY-NC-4.0
