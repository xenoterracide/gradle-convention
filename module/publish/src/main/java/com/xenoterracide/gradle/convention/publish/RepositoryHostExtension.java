// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.publish;

import java.net.URI;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;

/**
 * Extension for configuring information regarding where your repository lives.
 *
 * @implNote this convention concatenates these properties into final urls such as a clone url
 *   {@code $host/$namespace/$name.$extension} and the website url {@code $host/$namespace/$name}.
 */
public abstract class RepositoryHostExtension {

  private final Project project;

  /**
   * default constructor.
   *
   * @param project
   *   to configure the repository metadata
   */
  public RepositoryHostExtension(Project project) {
    this.project = project;
  }

  /**
   * Host URI for the repository.
   *
   * @return host
   */
  public abstract Property<URI> getHost();

  /**
   * Repository name.
   *
   * @return name
   */
  public abstract Property<String> getName();

  /**
   * on GitHub this would be your user or organization.
   *
   * @return namespace
   */
  public abstract Property<String> getNamespace();

  /**
   * Repository extension.
   *
   * @return repository extension
   */
  public abstract Property<String> getExtension();

  /**
   * Where the snapshot and pre-release jars are uploaded to.
   *
   * @return jar artifact host
   */
  public abstract Property<URI> getDevelopmentPackageHost();

  /**
   * Repository Metadata.
   *
   * @return repository metadata
   */
  public RepositoryMetadata getRepository() {
    return new RepositoryMetadata(this, this.project);
  }
}
