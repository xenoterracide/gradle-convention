// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.publish;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublishPluginTest {

  Project project;
  RepositoryHostExtension repositoryHost;
  PublicationLegalExtension publicationLegal;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().withName("that").build();
    project.getPluginManager().apply(JavaLibraryPlugin.class);
    project.getPluginManager().apply(PublishPlugin.class);
    repositoryHost = project.getExtensions().getByType(RepositoryHostExtension.class);
    publicationLegal = project.getExtensions().getByType(PublicationLegalExtension.class);
    project
      .getExtensions()
      .getByType(PublishingExtension.class)
      .publications(publications -> {
        publications.register("mavenJava", MavenPublication.class, mavenPublication -> {
          mavenPublication.from(project.getComponents().getByName("java"));
        });
      });
  }

  @Test
  void githubConfigurer() {
    var resolver = new RepositoryHostResolver(repositoryHost, project);
    new GithubPublicRepositoryConfigurer().execute(repositoryHost);
    repositoryHost.getNamespace().set("xenoterracide");
    assertThat(resolver.getWesiteUrl().get()).hasToString("https://github.com/xenoterracide/that");
    assertThat(resolver.getCloneUrl().get()).hasToString("https://github.com/xenoterracide/that.git");
    assertThat(resolver.getPackageUrl().get()).hasToString("https://maven.pkg.github.com/xenoterracide/that");
    assertThat(resolver.getDeveloperConnection().get()).isEqualTo("scm:git:https://github.com/xenoterracide/that.git");
  }

  @Test
  void explicitlySet() {
    var resolver = new RepositoryHostResolver(repositoryHost, project);
    repositoryHost.getNamespace().set("user");
    repositoryHost.getHost().set(URI.create("https://example.org"));
    repositoryHost.getDevelopmentPackageHost().set(URI.create("https://package.example.org"));
    repositoryHost.getExtension().set("hg");

    assertThat(resolver.getWesiteUrl().get()).hasToString("https://example.org/user/that");
    assertThat(resolver.getCloneUrl().get()).hasToString("https://example.org/user/that.hg");
    assertThat(resolver.getPackageUrl().get()).hasToString("https://package.example.org/user/that");
    assertThat(resolver.getDeveloperConnection().get()).isEqualTo("scm:hg:https://example.org/user/that.hg");
  }
}
