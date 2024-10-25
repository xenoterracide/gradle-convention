// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.publish;

import java.net.URI;
import org.gradle.api.Action;

/**
 * configures the repository host for GitHub
 * <p>
 * sets the following properties
 * <dl>
 *   <dt>host</dt>
 *   <dd>https://github.com</dd>
 *   <dt>developmentPackageHost</dt>
 *   <dd>https://maven.pkg.github.com</dd>
 *   <dt>extension</dt>
 *   <dd>git</dd>
 * </dl>
 *
 * @see RepositoryHostExtension
 */
public class GithubPublicRepositoryConfigurer implements Action<RepositoryHostExtension> {

  @Override
  public void execute(RepositoryHostExtension rhe) {
    rhe.getHost().convention(URI.create("https://github.com"));
    rhe.getDevelopmentPackageHost().convention(URI.create("https://maven.pkg.github.com"));
    rhe.getExtension().convention("git");
  }
}
