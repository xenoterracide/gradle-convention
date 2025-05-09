// SPDX-FileCopyrightText: Copyright © 2025 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0

// CHECKSTYLE:OFF: JavadocMissingWhitespaceAfterAsterisk
/**
 * Plugin to reduce configuration overhead for publishing to maven repositories.
 * This plugin primarily configures {@link org.gradle.api.publish.maven.plugins.MavenPublishPlugin}.
 *
 * <p>
 * Probably the best feature for other people here is the ability to configure maven's pom with its licenses.
 * Unfortunately maven wasn't designed with SPDX in mind and so expressions aren't supported. Instead, you should add
 * each component of any license expression seperately. For example if you're license is GPL 3.0 with claspath
 * exception, which in spdx would be written as {@code GPL-3.0-or-later WITH Classpath-exception-2.0}. You should add
 * the full expression to your {@code README.md} and each individual file with this license.
 * {@snippet lang = "kotlin":
 * plugins {
 *   id("com.xenoterracide.gradle.convention.publish")
 * }
 *
 * repositoryHost(GithubPublicRepositoryHostConfiguration())
 * repositoryHost.namespace.set("xenoterracide")
 *
 * publicationLegal {
 *   // this is the same as `maven-publish` `pom.inceptionYear`
 *   inceptionYear.set(2024)
 *   // this does NOT take spdx expressions
 *   spdxIdentifiers.addAll("GPL-3.0-or-later", "Classpath-exception-2.0")
 * }
 *}
 * </p>
 * <p>
 * under the hood this will set up the licenses as follows:
 * </p>
 * {@snippet lang = "java":
 *    pl.getName().set(license);
 *    pl.getUrl().set("https://spdx.org/licenses/" + license + ".html");
 *    pl.getComments().set("See git repo README.md for more information.");
 *    pl.getDistribution().set("repo");
 *}
 *
 * <p>
 * this information is constructed to generate urls like
 * {@code https://github.com/xenoterracide/gradle-convention.git} for pom and setting up the repo for publishing to
 * the repository. Currently, the maven repository to publish to has it's name set as `gh`and uses
 * {@link org.gradle.api.credentials.PasswordCredentials} so authenticating it is done like this in a GitHub workflow.
 * </p>
 * <p>
 * {@snippet lang = "yml":
 * - run: ./gradlew publishAllPublicationsToGhRepository --build-cache --scan
 *   env:
 *     ORG_GRADLE_PROJECT_ghUsername: ${{ secrets.GITHUB_ACTOR }}
 *     ORG_GRADLE_PROJECT_ghPassword: ${{ secrets.GITHUB_TOKEN }}
 *}
 * </p>
 */
// CHECKSTYLE:ON: JavadocMissingWhitespaceAfterAsterisk
@NullMarked
package com.xenoterracide.gradle.convention.publish;

import org.jspecify.annotations.NullMarked;
