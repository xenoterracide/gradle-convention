// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.publish;

import java.net.URI;
import org.gradle.api.provider.Property;

/**
 * Extension for configuring information regarding where your repository lives.
 *
 * @implNote this convention concatenates these properties into final urls such as a clone url
 *   {@code $host/$namespace/$name.$extension} and the website url {@code $host/$namespace/$name}.
 */
public interface RepositoryHostExtension {
  /**
   * Host URI for the repository, for example {@code https://github.com}
   *
   * @return host
   */
  Property<URI> getHost();

  /**
   * Repository name, will otherwise default to your {@code rootProject.name}
   *
   * @return name
   */
  Property<String> getName();

  /**
   * on GitHub this would be your user or organization
   *
   * @return namespace
   */
  Property<String> getNamespace();

  /**
   * Repository extension, defaults to {@code git}
   *
   * @return repository extension
   */
  Property<String> getExtension();

  /**
   * Where the jars are uploaded to, for example {@code https://maven.pkg.github.com}
   *
   * @return jar repository host
   */
  Property<URI> getDevelopmentPackageHost();
}
