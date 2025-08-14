<!--
SPDX-FileCopyrightText: Copyright © 2025 Caleb Cushing

SPDX-License-Identifier: CC-BY-NC-4.0
-->

# My Conventions

Firstly these are my personal conventions; if you find them useful feel free to let me know and I can migrate them to a
stable API. You're welcome to use them either way.

## Coverage

This plugin is designed to enforce a minimum coverage percentage per module, and also ensures that all `JvmTestSuite`s
are combined so that coverage is calculated across all tests. Instead of providing a ridiculous `BigDecimal` to the
plugin you can simply set a double to the minimum coverage percentage. The default percentage required is 90% or `0.9`.

```kts
plugins {
  id("com.xenoterracide.gradle.convention.coverage")
}

coverage {
  minimum.set(0.3) // default 0.9
}
```

## Publish

**WARNING:** this plugin sets up defaults for me, that will _not_ be suitable for your project.

See the [
`package-info`](https://github.com/xenoterracide/gradle-convention/blob/main/module/publish/src/main/java/com/xenoterracide/gradle/convention/publish/package-info.java)

## FAQ

### Gradle Support

Gradle and Java versions are tested as follows. Older versions may work but are unsupported. Version 1.x starts with
Java 11 but may require 17 without notice.

| Version | Gradle | Java |
| ------- | ------ | ---- |
| v0.2.x  | 8.x    | 11.x |
| v0.3.x  | 9.x    | 17.x |

## Contributing

### Languages

[asdf](https://asdf-vm.com) is suggested, you can use whatever you'd like to get

- Java 11+
- NodeJs

add a way to export these to your `PATH` in your `~/.profile`

### Build Tools

- [Gradle](https://docs.gradle.org/current/userguide/command_line_interface.html)
- [NPM](https://docs.npmjs.com/about-npm)

#### Fetching Dependencies

In order to get snapshots of dependencies, you must have a GitHub token in your `~/.gradle/gradle.properties` file. This
file should look like:

```properties
ghUsername = <your username>
ghPassword = <your token>
```

You should generate your PAT
as [Github Documents here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authenticating-to-github-packages).

> a personal access token (classic) with at least read:packages scope to install packages associated with other private
> repositories (which GITHUB_TOKEN can't access).

Then run.

Run `npm ci && ./gradlew dependencies` to install dependencies.

### Committing

Use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).

## Licenses

- Java and resulting Jars: [Apache-2.0](https://choosealicense.com/licenses/apache-2.0/)
- Gradle Kotlin and Config Files: [MIT](https://choosealicense.com/licenses/mit/)
- Documentation including Javadoc: [CC-BY-NC-4.0](https://creativecommons.org/licenses/by-nc/4.0/)

© Copyright 2024 Caleb Cushing.
