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
   * Host URI for the repository
   *
   * @return host
   */
  Property<URI> getHost();

  /**
   * Repository name
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
   * Repository extension
   *
   * @return repository extension
   */
  Property<String> getExtension();

  /**
   * Where the jars are uploaded to
   *
   * @return jar artifact host
   */
  Property<URI> getDevelopmentPackageHost();
}
