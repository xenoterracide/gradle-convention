// © Copyright 2024 Caleb Cushing
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

  @Override
  public void apply(@NonNull Project project) {
    var repo = project.getExtensions().create("repositoryHost", RepositoryHostExtension.class);
    var legal = project.getExtensions().create("publicationLegal", PublicationLegal.class);

    var rootProject = project.getRootProject();
    project.setGroup(rootProject.getGroup());
    project.setVersion(rootProject.getVersion());

    project.getPlugins().apply(MavenPublishPlugin.class);

    var publishing = project.getExtensions().getByType(PublishingExtension.class);
    publishing.repositories(pubRepo -> {
      pubRepo.maven(maven -> {
        maven.setName("gh");
        maven.setUrl(repo.packageUrl());
        maven.credentials(PasswordCredentials.class);
      });
    });
    var publications = publishing.getPublications();

    var log = project.getLogger();
    publications
      .withType(MavenPublication.class)
      .configureEach(pub -> {
        pub.suppressAllPomMetadataWarnings();
        log.quiet("publishing {} {}:{}:{}", pub.getName(), pub.getGroupId(), pub.getArtifactId(), pub.getVersion());

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
                  pl.getComments().set("See README.md for more information.");
                  pl.getDistribution().set("repo");
                })
              );
          });
          pom.developers(developers -> {
            developers.developer(developer -> {
              developer.getName().set("Caleb Cushing");
              developer.getEmail().set("caleb.cushing@gmail.com");
              developer.getId().set("xenoterracide");
            });
          });
          pom.scm(scm -> {
            scm.getConnection().set(repo.cloneUrl().toString());
            scm.getDeveloperConnection().set("scm:git:" + repo.cloneUrl());
            scm.getUrl().set(repo.websiteUrl().toString());
          });
        });
      });
  }
}
