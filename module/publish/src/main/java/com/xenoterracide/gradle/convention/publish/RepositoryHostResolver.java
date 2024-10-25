// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.publish;

import java.net.URI;
import java.util.function.BiFunction;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;

/**
 * Converts properties into relevant urls
 */
public class RepositoryHostResolver {

  private final Provider<URI> host;
  private final Provider<String> namespace;
  private final Provider<String> name;
  private final Provider<String> extension;
  private final Provider<URI> developmentPackageHost;

  @SuppressWarnings("UnnecessaryLambda")
  private final BiFunction<URI, String, URI> resolver = (uri, path) ->
    uri.resolve(String.join("/", uri.getPath(), path));

  RepositoryHostResolver(RepositoryHostExtension extension, Project project) {
    this.name = extension.getName().convention(project.getRootProject().getName());
    this.host = extension.getHost();
    this.namespace = extension.getNamespace();
    this.extension = extension.getExtension();
    this.developmentPackageHost = extension.getDevelopmentPackageHost();
  }

  /**
   * Url you can clone from
   *
   * @return clone url
   */
  public Provider<URI> getCloneUrl() {
    return this.getWesiteUrl().zip(this.extension, (uri, ext) -> uri.resolve(uri.getPath() + "." + ext));
  }

  /**
   * Website url for the repository
   *
   * @return website url
   */
  public Provider<URI> getWesiteUrl() {
    return this.host.zip(this.namespace, resolver).zip(this.name, resolver);
  }

  /**
   * Development package url for publishing to, like maven central or a private artifactory
   *
   * @return development package url
   */
  public Provider<URI> getPackageUrl() {
    return this.developmentPackageHost.zip(namespace, resolver).zip(name, resolver);
  }

  /**
   * Maven pom developer connection
   *
   * @return developer connection
   */
  public Provider<String> getDeveloperConnection() {
    return this.getCloneUrl().zip(extension, (uri, ext) -> String.join(":", "scm", ext, uri.toString()));
  }
}
