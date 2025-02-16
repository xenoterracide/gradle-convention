// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.publish;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.credentials.PasswordCredentials;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.jspecify.annotations.NonNull;

/**
 * Plugin for configuring publishinga java to a repository host.
 */
public class PublishPlugin implements Plugin<Project> {

  /**
   * default constructor.
   */
  public PublishPlugin() {}

  @Override
  @SuppressWarnings("checkstyle:MethodLength")
  public void apply(@NonNull Project project) {
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
    // CHECKSTYLE:OFF: LambdaBodyLength
    publications
      .withType(MavenPublication.class)
      .configureEach(pub -> {
        pub.suppressAllPomMetadataWarnings();
        log.quiet("publication {} {}:{}:{}", pub.getName(), pub.getGroupId(), pub.getArtifactId(), pub.getVersion());

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
                  pl.getDistribution().set("repo");
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
            scm.getUrl().set(repo.getWesiteUrl().map(Object::toString));
            scm.getDeveloperConnection().set(repo.getDeveloperConnection());
          });
        });
      });
    // CHECKSTYLE:ON: LambdaBodyLength
    publishing.repositories(pubRepo -> {
      pubRepo.maven(maven -> {
        maven.setName("gh");
        maven.setUrl(repo.getPackageUrl());
        maven.credentials(PasswordCredentials.class);
      });
    });
  }
}
