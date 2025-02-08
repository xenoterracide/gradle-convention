// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.publish;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

/**
 * Extension for configuring legal information regarding your project.
 */
public interface PublicationLegalExtension {
  /**
   * The year the project was started.
   *
   * @return inception year
   */
  Property<Number> getInceptionYear();

  /**
   * SPDX license identifiers.
   *
   * @return all SPDX license identifiers
   */
  SetProperty<String> getSpdxLicenseIdentifiers();
}
