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
  RepositoryHostExtension respositoryHost;
  PublicationLegalExtension publicationLegal;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().withName("that").build();
    project.getPluginManager().apply(JavaLibraryPlugin.class);
    project.getPluginManager().apply(PublishPlugin.class);
    respositoryHost = project.getExtensions().getByType(RepositoryHostExtension.class);
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
  void explicitlySet() {
    var resolver = new RepositoryHostResolver(respositoryHost, project);
    respositoryHost.getNamespace().set("user");
    respositoryHost.getHost().set(URI.create("https://example.org"));
    respositoryHost.getDevelopmentPackageHost().set(URI.create("https://package.example.org"));

    assertThat(resolver.websiteUrl().get()).hasToString("https://example.org/user/that");
    assertThat(resolver.cloneUrl().get()).hasToString("https://example.org/user/that.git");
    assertThat(resolver.packageUrl().get()).hasToString("https://package.example.org/user/that");
  }

  @Test
  void defaults() {
    var resolver = new RepositoryHostResolver(respositoryHost, project);
    assertThat(resolver.websiteUrl().get()).hasToString("https://github.com/xenoterracide/that");
    assertThat(resolver.cloneUrl().get()).hasToString("https://github.com/xenoterracide/that.git");
    assertThat(resolver.packageUrl().get()).hasToString("https://maven.pkg.github.com/xenoterracide/that");
  }
}
