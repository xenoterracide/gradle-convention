// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: MIT

plugins {
  `maven-publish`
  id("com.gradle.plugin-publish")
}

group = rootProject.group
version = rootProject.version

val repo = rootProject.name
val username = "xenoterracide"
val githubUrl = "https://github.com"
val repoShort = "$username/$repo"

gradlePlugin {
  website.set("$githubUrl/$repoShort")
  vcsUrl.set("${website.get()}.git")
}

publishing {
  publications {
    register<MavenPublication>("maven") {
      from(components["java"])
    }
    withType<MavenPublication>().configureEach {
      artifactId = project.name
      groupId = project.group.toString()
      version = project.version.toString()

      logger.quiet("publishing {}:{}:{}", groupId, artifactId, version)

      versionMapping {
        allVariants {
          fromResolutionResult()
        }
      }

      pom {

        description = project.description
        inceptionYear = "2024"
        url = "$githubUrl/$repoShort"
        licenses {
          license {
            name = "AGPL-3.0-or-later"
            url = "https://choosealicense.com/licenses/agpl-3.0/"
            distribution = "repo"
            comments = "See README.md for more information."
          }
          license {
            name = "Universal-FOSS-exception-1.0"
            url = "https://spdx.org/licenses/Universal-FOSS-exception-1.0.html"
            distribution = "repo"
            comments = "See README.md for more information."
          }
          license {
            name = "MIT"
            url = "https://choosealicense.com/licenses/mit/"
            distribution = "repo"
            comments = "See README.md for more information."
          }
          license {
            name = "CC-BY-4.0"
            url = "https://choosealicense.com/licenses/cc-by-4.0/"
            distribution = "repo"
            comments = "See README.md for more information."
          }
          license {
            name = "CC-BY-NC-4.0"
            url = "https://creativecommons.org/licenses/by-nc/4.0/"
            distribution = "repo"
            comments = "See README.md for more information."
          }
        }
        developers {
          developer {
            id = username
            name = "Caleb Cushing"
            email = "caleb.cushing@gmail.com"
          }
        }
        scm {
          connection = "$githubUrl/$repoShort.git"
          developerConnection = "scm:git:$githubUrl/$repoShort.git"
          url = "$githubUrl/$repoShort"
        }
      }
    }
  }

  repositories {
    maven {
      name = "gh"
      url = uri("https://maven.pkg.github.com/$repoShort")
      credentials(PasswordCredentials::class)
    }
  }
}
