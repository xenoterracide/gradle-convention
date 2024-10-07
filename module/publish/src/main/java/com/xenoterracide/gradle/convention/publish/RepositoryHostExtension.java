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

  default URI cloneUrl() {
    return this.getHost().get().resolve(String.join("/", getNamespace().get(), getName().get(), getExtension().get()));
  }

  default URI websiteUrl() {
    return this.getHost().get().resolve(String.join("/", getNamespace().get(), getName().get()));
  }

  default URI packageUrl() {
    return URI.create("https://maven.pkg.github.com").resolve(
      String.join("/", this.getNamespace().get(), this.getName().get())
    );
  }
}