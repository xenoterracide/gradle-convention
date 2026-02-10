// SPDX-FileCopyrightText: Copyright © 2024 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.publish;

import com.vanniktech.maven.publish.MavenPublishBaseExtension;
import com.vanniktech.maven.publish.MavenPublishPlugin;
import org.apache.commons.lang3.BooleanUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.credentials.PasswordCredentials;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;

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
    project.getPluginManager().apply(MavenPublishPlugin.class);
    var rootProject = project.getRootProject();
    project.setGroup(rootProject.getGroup());
    project.setVersion(rootProject.getVersion());

    var isPublishing = project
      .getProviders()
      .environmentVariable("IS_PUBLISHING")
      .map(val -> "1".equals(val) || BooleanUtils.toBoolean(val))
      .getOrElse(false);
    var mavenPublish = project.getExtensions().getByType(MavenPublishBaseExtension.class);
    if (isPublishing) {
      mavenPublish.signAllPublications();
    }

    var rhe = project.getExtensions().create("repositoryHost", RepositoryHostExtension.class);
    var repo = rhe.getRepository();
    var legal = project.getExtensions().create("publicationLegal", PublicationLegalExtension.class);

    mavenPublish.pom(pom -> {
      pom.getName().set(project.getName());
      pom.getDescription().set(project.getDescription());
      pom.getInceptionYear().set(legal.getInceptionYear().map(Number::toString));
      pom.licenses(licenses -> {
        legal
          .getSpdxLicenseIdentifiers()
          .get()
          .forEach(license ->
            licenses.license(pl -> {
              pl.getName().set(license);
              pl.getUrl().set(repo.getWebsiteUrl().map(uri -> uri + "/tree/develop/LICENSES"));
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

    var publishing = project.getExtensions().getByType(PublishingExtension.class);
    var publications = publishing.getPublications();

    var log = project.getLogger();
    publications
      .withType(MavenPublication.class)
      .configureEach(pub -> {
        log.lifecycle(
          "publication {} {}:{}:{}",
          pub.getName(),
          pub.getGroupId(),
          pub.getArtifactId(),
          pub.getVersion()
        );
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
  }
}
