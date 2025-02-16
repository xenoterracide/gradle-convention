// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.publish;

import java.net.URI;
import org.gradle.api.Action;

/**
 * configures the repository host for GitHub.
 *
 * <p>sets the following properties
 * <dl>
 *   <dt>host</dt>
 *   <dd>{@code https://github.com}</dd>
 *   <dt>developmentPackageHost</dt>
 *   <dd>{@code https://maven.pkg.github.com}</dd>
 *   <dt>extension</dt>
 *   <dd>git</dd>
 * </dl>
 *
 * @see RepositoryHostExtension
 */
public class GithubPublicRepositoryConfiguration implements Action<RepositoryHostExtension> {

  /**
   * default constructor.
   */
  public GithubPublicRepositoryConfiguration() {}

  @Override
  public void execute(RepositoryHostExtension rhe) {
    rhe.getHost().convention(URI.create("https://github.com"));
    rhe.getDevelopmentPackageHost().convention(URI.create("https://maven.pkg.github.com"));
    rhe.getExtension().convention("git");
  }
}
