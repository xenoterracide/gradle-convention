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
    this.host = extension.getHost().convention(URI.create("https://github.com"));
    this.namespace = extension.getNamespace().convention("xenoterracide");
    this.name = extension.getName().convention(project.getRootProject().getName());
    this.extension = extension.getExtension().convention("git");
    this.developmentPackageHost = extension
      .getDevelopmentPackageHost()
      .convention(URI.create("https://maven.pkg.github.com"));
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
}
