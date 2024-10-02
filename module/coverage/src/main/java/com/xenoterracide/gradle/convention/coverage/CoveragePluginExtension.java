// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.coverage;

import org.gradle.api.provider.Property;

/**
 * Coverage Plugin extension.
 */
public interface CoveragePluginExtension {
  /**
   * Property for minimum coverage.
   *
   * @return minimum coverage property
   */
  Property<Double> getMinimum();
}
