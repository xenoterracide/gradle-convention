<!--
© Copyright 2024 Caleb Cushing.
SPDX-License-Identifier: CC-BY-4.0
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

reduce configuration overhead for publishing to maven repositories. This plugin sets up the `maven-publish` plugin

probably the best feature for other people here is the ability to configure maven's pom with its licenses. Unfortunately
maven wasn't designed with SPDX in mind and so expressions aren't supported. Instead you should add each component of
any license expression seperately. For example if you you're licenseMIT is GPL 3.0 with claspath exception, which in
spdx would be written as `GPL-3.0-or-later WITH Classpath-exception-2.0`. You should add the full expression to your
`README.md`
and each individual file with this license.

```kts
plugins {
  id("com.xenoterracide.gradle.convention.publish")
}

//
publicationLegal {
  inceptionYear.set(2024) // this is the same as `maven-publish` `pom.inceptionYear`
  spdxIdentifiers.addAll("GPL-3.0-or-later", "Classpath-exception-2.0") // this does NOT take spdx expressions
}
```

under the hood it will set the licenses as follows, you

```java
  pl.getName().set(license);
  pl.getUrl().set("https://spdx.org/licenses/" + license + ".html");
  pl.getComments().set("See git repo README.md for more information.");
  pl.getDistribution().set("repo");
```

This plugin also makes it convenient to set up publishing to githubs own package repo. You **WILL** need to override
this.

```kts
repositoryHost {
  host.set("https://github.com")                             // default
  name.set("repositoryName")                                 // default is rootProject.name
  namespace.set("myUser")                                    // default is xenoterracide
  extension.set("git")                                       // default
  developmentPackageHost.set("https://maven.pkg.github.com") // default
}
```

this information is constructed to generate urls like `https://github.com/xenoterracide/gradle-convention.git` for pom
and setting up the repo for publishing to the repository. Currently the maven repository to publish to has it's name set
as `gh`and uses `PasswordCredentials` so authenticating it is done like this in a github workflow.

```yml
- run: ./gradlew publishAllPublicationsToGhRepository --build-cache --scan
  env:
    ORG_GRADLE_PROJECT_ghUsername: ${{ secrets.GITHUB_ACTOR }}
    ORG_GRADLE_PROJECT_ghPassword: ${{ secrets.GITHUB_TOKEN }}
```

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
