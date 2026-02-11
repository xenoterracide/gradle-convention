// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.publish;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.jvm.tasks.Jar;
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
        publications.register("maven", MavenPublication.class, mavenPublication -> {
          mavenPublication.from(project.getComponents().getByName("java"));
        });
      });
  }

  @Test
  void githubConfigurer() {
    var resolver = new RepositoryMetadata(repositoryHost, project);
    new GithubPublicRepositoryConfiguration().execute(repositoryHost);
    repositoryHost.getNamespace().set("xenoterracide");
    assertThat(resolver.getWebsiteUrl().get()).hasToString("https://github.com/xenoterracide/that");
    assertThat(resolver.getCloneUrl().get()).hasToString("https://github.com/xenoterracide/that.git");
    assertThat(resolver.getPackageUrl().get()).hasToString("https://maven.pkg.github.com/xenoterracide/that");
    assertThat(resolver.getDeveloperConnection().get()).isEqualTo("scm:git:https://github.com/xenoterracide/that.git");

    var publishing = project.getExtensions().getByType(PublishingExtension.class);
    assertThat(publishing.getRepositories().findByName("gh")).isNotNull();
    assertThat(publishing.getRepositories().findByName("staging")).isNotNull();
  }

  @Test
  void explicitlySet() {
    var resolver = new RepositoryMetadata(repositoryHost, project);
    repositoryHost.getNamespace().set("user");
    repositoryHost.getHost().set(URI.create("https://example.org"));
    repositoryHost.getDevelopmentPackageHost().set(URI.create("https://package.example.org"));
    repositoryHost.getExtension().set("hg");

    assertThat(resolver.getWebsiteUrl().get()).hasToString("https://example.org/user/that");
    assertThat(resolver.getCloneUrl().get()).hasToString("https://example.org/user/that.hg");
    assertThat(resolver.getPackageUrl().get()).hasToString("https://package.example.org/user/that");
    assertThat(resolver.getDeveloperConnection().get()).isEqualTo("scm:hg:https://example.org/user/that.hg");
  }

  @Test
  void jarArchiveBaseNameDerivedFromProjectPath() {
    var root = ProjectBuilder.builder().withName("root").build();
    var parent = ProjectBuilder.builder().withParent(root).withName("a").build();
    var child = ProjectBuilder.builder().withParent(parent).withName("b").build();

    child.getPluginManager().apply(JavaLibraryPlugin.class);
    child.getPluginManager().apply(PublishPlugin.class);

    // ensure a Jar task exists (from Java plugin)
    var jar = (Jar) child.getTasks().getByName("jar");
    assertThat(jar.getArchiveBaseName().get()).isEqualTo("a-b");
  }

  @Test
  void pomIsConfiguredFromProjectAndRepositoryMetadata() {
    // set some project values
    project.setDescription("A sample project");
    publicationLegal.getInceptionYear().set(2024);
    publicationLegal.getSpdxLicenseIdentifiers().set(Set.of("Apache-2.0"));

    // configure repository host like GitHub and namespace
    new GithubPublicRepositoryConfiguration().execute(repositoryHost);
    repositoryHost.getNamespace().set("xenoterracide");

    var publishing = project.getExtensions().getByType(PublishingExtension.class);
    var pub = (MavenPublication) publishing.getPublications().getByName("maven");

    var pom = pub.getPom();
    assertThat(pom.getName().get()).isEqualTo(project.getName());
    assertThat(pom.getDescription().get()).isEqualTo(project.getDescription());
    assertThat(pom.getInceptionYear().get()).isEqualTo("2024");

    // Note: Detailed assertions on nested POM sections (developers/licenses/scm)
    // are omitted here due to limited read accessors in the TestFixtures API.
  }

  @Test
  void groupAndVersionMirrorRootProject() {
    var root = ProjectBuilder.builder().withName("root").build();
    root.setGroup("com.example");
    root.setVersion("1.2.3");

    var sub = ProjectBuilder.builder().withParent(root).withName("lib").build();
    sub.getPluginManager().apply(JavaLibraryPlugin.class);
    sub.getPluginManager().apply(PublishPlugin.class);

    assertThat(sub.getGroup()).isEqualTo(root.getGroup());
    assertThat(sub.getVersion()).isEqualTo(root.getVersion());
  }
}
