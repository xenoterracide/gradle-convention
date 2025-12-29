// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.publish;

import java.io.File;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.credentials.PasswordCredentials;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;

/**
 * Plugin for configuring publishinga java to a repository host.
 */
public class PublishPlugin implements Plugin<Project> {

  private static final String REPO = "repo";
  private static final String STAGING_REPO = REPO;

  /**
   * default constructor.
   */
  public PublishPlugin() {}

  @Override
  @SuppressWarnings({ "checkstyle:MethodLength", "checkstyle:LambdaBodyLength" })
  public void apply(Project project) {
    var rootProject = project.getRootProject();
    project.setGroup(rootProject.getGroup());
    project.setVersion(rootProject.getVersion());

    project.getPlugins().apply(MavenPublishPlugin.class);

    var rhe = project.getExtensions().create("repositoryHost", RepositoryHostExtension.class);
    var legal = project.getExtensions().create("publicationLegal", PublicationLegalExtension.class);
    var repo = rhe.getRepository();
    var publishing = project.getExtensions().getByType(PublishingExtension.class);
    var publications = publishing.getPublications();

    var log = project.getLogger();
    publications
      .withType(MavenPublication.class)
      .configureEach(pub -> {
        pub.suppressAllPomMetadataWarnings();
        log.lifecycle(
          "publication {} {}:{}:{}",
          pub.getName(),
          pub.getGroupId(),
          pub.getArtifactId(),
          pub.getVersion()
        );

        pub.pom(pom -> {
          pom.getInceptionYear().set(legal.getInceptionYear().map(Number::toString));
          pom.licenses(licenses -> {
            legal
              .getSpdxLicenseIdentifiers()
              .get()
              .forEach(license ->
                licenses.license(pl -> {
                  pl.getName().set(license);
                  pl.getUrl().set("https://spdx.org/licenses/" + license + ".html");
                  pl.getComments().set("See git repo README.md for more information.");
                  pl.getDistribution().set(REPO);
                })
              );
          });
          pom.developers(developers -> {
            developers.developer(developer -> {
              developer.getName().set("Caleb Cushing");
              developer.getEmail().set("caleb.cushing@gmail.com");
              developer.getId().set(rhe.getNamespace());
            });
          });
          pom.scm(scm -> {
            scm.getConnection().set(repo.getCloneUrl().map(Object::toString));
            scm.getUrl().set(repo.getWebsiteUrl().map(Object::toString));
            scm.getDeveloperConnection().set(repo.getDeveloperConnection());
          });
        });
      });
    publishing.repositories(pubRepo -> {
      pubRepo.maven(maven -> {
        maven.setName("gh");
        maven.setUrl(repo.getPackageUrl());
        maven.credentials(PasswordCredentials.class);
      });
      pubRepo.maven(maven -> {
        maven.setName("staging");
        maven.setUrl(project.getLayout().getBuildDirectory().dir(STAGING_REPO));
      });
    });

    var tasks = project.getTasks();
    tasks
      .withType(PublishToMavenRepository.class)
      .stream()
      .filter(task -> task.getName().startsWith("publishMavenPublicationTo"))
      .toList()
      .forEach(task -> {
        var repository = task.getRepository();
        tasks.register(repository.getName() + "ArtifactPath", ArtifactPathTask.class, t -> {
          t.getDirectory().set(project.getLayout().dir(project.provider(() -> new File(repository.getUrl()))));
          t.getProjectName().set(project.getName());
          t.getProjectGroup().set(project.getGroup().toString());
          t.getProjectVersion().set(project.provider(project.getVersion()::toString));
        });
      });
  }
}
