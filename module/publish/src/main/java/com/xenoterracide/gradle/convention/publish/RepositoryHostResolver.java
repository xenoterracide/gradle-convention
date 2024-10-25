// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.publish;

import java.net.URI;
import java.util.function.BiFunction;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.jspecify.annotations.NonNull;

class RepositoryHostResolver {

  private final Provider<URI> host;
  private final Provider<String> namespace;
  private final Provider<String> name;
  private final Provider<String> extension;
  private final Provider<URI> developmentPackageHost;

  @SuppressWarnings("UnnecessaryLambda")
  private final BiFunction<URI, String, URI> resolver = (uri, path) ->
    uri.resolve(String.join("/", uri.getPath(), path));

  RepositoryHostResolver(@NonNull RepositoryHostExtension extension, @NonNull Project project) {
    this.name = extension.getName().convention(project.getRootProject().getName());
    this.host = extension.getHost();
    this.namespace = extension.getNamespace();
    this.extension = extension.getExtension();
    this.developmentPackageHost = extension.getDevelopmentPackageHost();
  }

  Provider<URI> cloneUrl() {
    return this.websiteUrl().zip(this.extension, (uri, ext) -> uri.resolve(uri.getPath() + "." + ext));
  }

  Provider<URI> websiteUrl() {
    return this.host.zip(this.namespace, resolver).zip(this.name, resolver);
  }

  Provider<URI> packageUrl() {
    return this.developmentPackageHost.zip(namespace, resolver).zip(name, resolver);
  }

  Provider<String> developerConnection() {
    return this.cloneUrl().zip(extension, (uri, ext) -> String.join(":", "scm", ext, uri.toString()));
  }
}
